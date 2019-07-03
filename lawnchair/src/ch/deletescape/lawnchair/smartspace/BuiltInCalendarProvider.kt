/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.smartspace

import android.content.pm.PackageManager
import android.database.Cursor
import android.database.CursorIndexOutOfBoundsException
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.provider.CalendarContract
import android.support.annotation.Keep
import android.text.TextUtils
import android.util.Log
import ch.deletescape.lawnchair.drawableToBitmap
import ch.deletescape.lawnchair.util.Temperature
import com.android.launcher3.R
import java.util.*


@Keep class BuiltInCalendarProvider(controller: LawnchairSmartspaceController) :
        LawnchairSmartspaceController.DataProvider(controller) {

    private var silentlyFail: Boolean = false
    private val iconProvider = WeatherIconProvider(controller.context)
    private val weather = LawnchairSmartspaceController
            .WeatherData(iconProvider.getIcon("-1"), Temperature(0, Temperature.Unit.Celsius), "")
    private var card: LawnchairSmartspaceController.CardData? = null
    private val handlerThread by lazy { HandlerThread(javaClass.hashCode().toString()) }
    private val workerHandler by lazy { Handler(handlerThread.looper) }
    private val contentResolver = controller.context.contentResolver

    init {
        Log.d(javaClass.name, "class initializer: init")
        handlerThread.start()
        Log.d(javaClass.name, "updateInformation: refreshing calendar")
        workerHandler.postAtTime(this::forceUpdate, this, SystemClock.uptimeMillis() + 5000)
    }

    private fun updateInformation() {
        Log.d(javaClass.name, "updateInformation: refreshing calendar")
        if (controller.context.checkSelfPermission(
                    android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.e(javaClass.name, "updateInformation: calendar permissions *not* granted")
            silentlyFail = true;
        } else {
            silentlyFail = false;
        }
        /*
         * Right now this is the only place at which silentlyFail can change, but there
         * will be more, which is why this is in a separate block
         */
        if (silentlyFail) {
            Log.e(javaClass.name, "updateInformation: silent fail")
            updateData(weather, null);
            return;
        }

        val currentTime = GregorianCalendar();
        val endTime = GregorianCalendar();
        endTime.add(Calendar.MINUTE, 240);
        Log.v(javaClass.name,
              "updateInformation: searching for events between " + currentTime + " and " + endTime.toString())
        val query =
                "(( " + CalendarContract.Events.DTSTART + " >= " + currentTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ))"
        val eventCursorNullable: Cursor? = contentResolver
                .query(CalendarContract.Events.CONTENT_URI,
                       arrayOf(CalendarContract.Instances.TITLE, CalendarContract.Instances.DTSTART,
                               CalendarContract.Instances.DTEND,
                               CalendarContract.Instances.DESCRIPTION), query, null,
                       CalendarContract.Instances.DTSTART + " ASC")
        if (eventCursorNullable == null) {
            Log.v(javaClass.name,
                  "updateInformation: query is null, probably since there are no events that meet the specified criteria")
            card = null
            updateData(weather, card = null);
            return;
        }
        try {
            val eventCursor = eventCursorNullable
            eventCursor.moveToFirst();
            val title = eventCursor.getString(0);
            Log.v(javaClass.name, "updateInformation: query found event")
            Log.v(javaClass.name, "updateInformation:     title: " + title)
            val startTime = GregorianCalendar()
            startTime.timeInMillis = eventCursor.getLong(1);
            Log.v(javaClass.name, "updateInformation:     startTime: " + startTime)
            val eventEndTime = GregorianCalendar()
            eventEndTime.timeInMillis = eventCursor.getLong(2)
            Log.v(javaClass.name, "updateInformation:     eventEndTime: " + eventEndTime)
            val description = eventCursor.getString(3);
            eventCursor.close();
            val diff = startTime.timeInMillis - currentTime.timeInMillis
            Log.v(javaClass.name, "updateInformation: difference in milliseconds: " + diff)
            val diffSeconds = diff / 1000
            val diffMinutes = diff / (60 * 1000)
            val diffHours = diff / (60 * 60 * 1000)
            val text = if (diffMinutes <= 0) controller.context.getString(
                R.string.reusable_str_now) else controller.context.getString(
                if (diffMinutes < 1 || diffMinutes > 1) R.string.subtitle_smartspace_in_minutes else R.string.subtitle_smartspace_in_minute,
                diffMinutes)
            card = LawnchairSmartspaceController.CardData(drawableToBitmap(
                controller.context.resources.getDrawable(R.drawable.ic_event_black_24dp)),
                                                          if (title == null || title.trim().isEmpty()) controller.context.getString(
                                                              R.string.placeholder_empty_title) else title,
                                                          TextUtils.TruncateAt.MARQUEE, text,
                                                          TextUtils.TruncateAt.END)
            updateData(weather, card)
        } catch (e: CursorIndexOutOfBoundsException) {
            val currentTime = GregorianCalendar();
            Log.v(javaClass.name,
                  "updateInformation: searching for events that are active at ${currentTime}")
            val query =
                    "(( " + CalendarContract.Events.DTSTART + " <= " + currentTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTEND + " >= " + currentTime.getTimeInMillis() + " ))"
            val eventCursorNullable: Cursor? = contentResolver
                    .query(CalendarContract.Events.CONTENT_URI,
                           arrayOf(CalendarContract.Instances.TITLE,
                                   CalendarContract.Instances.DTSTART,
                                   CalendarContract.Instances.DTEND,
                                   CalendarContract.Instances.DESCRIPTION,
                                   CalendarContract.Instances.ALL_DAY), query, null,
                           CalendarContract.Instances.DTSTART + " ASC")
            if (eventCursorNullable == null) {
                Log.v(javaClass.name,
                      "updateInformation: query is null, probably since there are no events that meet the specified criteria")
                card = null
                updateData(weather, card = null);
                return;
            }
            try {
                val eventCursor = eventCursorNullable
                eventCursor.moveToFirst();
                val title = eventCursor.getString(0);
                Log.v(javaClass.name, "updateInformation: query found event")
                Log.v(javaClass.name, "updateInformation:     title: " + title)
                val startTime = GregorianCalendar()
                startTime.timeInMillis = eventCursor.getLong(1);
                Log.v(javaClass.name, "updateInformation:     startTime: " + startTime)
                val eventEndTime = GregorianCalendar()
                eventEndTime.timeInMillis = eventCursor.getLong(2)
                Log.v(javaClass.name, "updateInformation:     eventEndTime: " + eventEndTime)
                val lines = listOf(LawnchairSmartspaceController.Line(
                    if (title == null || title.trim().isEmpty()) controller.context.getString(
                        R.string.placeholder_empty_title) else title, TextUtils.TruncateAt.MARQUEE),
                                   LawnchairSmartspaceController.Line(controller.context.getString(
                                       if (eventCursor.getInt(
                                                   4) != 0) R.string.reusable_string_all_day_event else R.string.ongoing),
                                                                      TextUtils.TruncateAt.END))
                val description = eventCursor.getString(3);
                card = LawnchairSmartspaceController.CardData(drawableToBitmap(
                    controller.context.getDrawable(R.drawable.ic_event_black_24dp)), lines, true)
                eventCursor.close();
                updateData(weather, card)
            } catch (e: CursorIndexOutOfBoundsException) {
                updateData(weather, card = null)
            }
        }
    }

    override fun onDestroy() {
        workerHandler.removeCallbacksAndMessages(this)
        handlerThread.quitSafely()
    }

    override fun forceUpdate() {
        workerHandler.postAtTime(this::forceUpdate, this, SystemClock.uptimeMillis() + 5000)
        updateInformation()
    }
}

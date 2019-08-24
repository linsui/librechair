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

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.CursorIndexOutOfBoundsException
import android.net.Uri
import android.provider.CalendarContract
import android.support.annotation.Keep
import android.text.TextUtils
import ch.deletescape.lawnchair.drawableToBitmap
import ch.deletescape.lawnchair.formatTime
import com.android.launcher3.R
import java.util.*

@Keep
class BuiltInCalendarProvider(controller: LawnchairSmartspaceController) :
        BroadcastDataProvider(controller) {
    private var silentlyFail: Boolean = false
    private var card: LawnchairSmartspaceController.CardData? = null
    private val contentResolver
        get() = context.contentResolver

    private fun updateInformation() {
        silentlyFail = controller.context.checkSelfPermission(
                android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
        if (silentlyFail) {
            updateData(null, null);
            return;
        }
        val currentTime = GregorianCalendar();
        val endTime = GregorianCalendar();
        endTime.add(Calendar.MINUTE, 240);
        val query =
                "(( " + CalendarContract.Events.DTSTART + " >= " + currentTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ))"
        val eventCursorNullable: Cursor? = contentResolver
                .query(CalendarContract.Events.CONTENT_URI,
                       arrayOf(CalendarContract.Instances.TITLE, CalendarContract.Instances.DTSTART,
                               CalendarContract.Instances.DTEND,
                               CalendarContract.Instances.DESCRIPTION, CalendarContract.Events._ID,
                               CalendarContract.Instances.CUSTOM_APP_PACKAGE), query, null,
                       CalendarContract.Instances.DTSTART + " ASC")
        if (eventCursorNullable == null) {
            card = null
            updateData(null, card = null);
            return;
        }
        try {
            val eventCursor = eventCursorNullable
            eventCursor.moveToFirst();
            val title = eventCursor.getString(0);
            val startTime = GregorianCalendar()
            startTime.timeInMillis = eventCursor.getLong(1);
            val eventEndTime = GregorianCalendar()
            eventEndTime.timeInMillis = eventCursor.getLong(2)
            val description = eventCursor.getString(3);
            val diff = startTime.timeInMillis - currentTime.timeInMillis
            val diffSeconds = diff / 1000
            val diffMinutes = diff / (60 * 1000)
            val diffHours = diff / (60 * 60 * 1000)
            val text = if (diffMinutes <= 0) controller.context.getString(
                    R.string.reusable_str_now) else controller.context.getString(
                    if (diffMinutes < 1 || diffMinutes > 1) R.string.subtitle_smartspace_in_minutes else R.string.subtitle_smartspace_in_minute,
                    diffMinutes)
            val intent = Intent(Intent.ACTION_VIEW)
            if (eventCursor.getString(5) != null) {
                if (controller.context.packageManager.getApplicationEnabledSetting(
                                eventCursor.getString(
                                        5)!!) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                    intent.`package` = eventCursor.getString(5)!!
                }
            }
            intent.data = Uri.parse("content://com.android.calendar/events/" + eventCursor.getLong(
                    4).toString())
            card = LawnchairSmartspaceController.CardData(drawableToBitmap(
                    controller.context.getDrawable(R.drawable.ic_event_black_24dp)!!),
                                                          listOf(LawnchairSmartspaceController.Line(
                                                                  if (title == null || title.trim().isEmpty()) controller.context.getString(
                                                                          R.string.placeholder_empty_title) else title,
                                                                  TextUtils.TruncateAt.MARQUEE),
                                                                 LawnchairSmartspaceController.Line(
                                                                         text,
                                                                         TextUtils.TruncateAt.END),
                                                                 LawnchairSmartspaceController.Line(
                                                                         formatTime(startTime,
                                                                                    context))),
                                                          PendingIntent.getActivity(
                                                                  controller.context, 0, intent, 0,
                                                                  null))
            eventCursor.close();
            updateData(null, card)
        } catch (e: CursorIndexOutOfBoundsException) {
            updateData(null, null)
        }
    }

    override fun getIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(Intent.ACTION_PROVIDER_CHANGED)
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
            addDataScheme("content");
            addDataAuthority("com.android.calendar", null);
        }
    }

    override fun onBroadcastRecieved(intent: Intent) {
        updateInformation()
    }

    override fun forceUpdate() {
        updateInformation()
    }
}

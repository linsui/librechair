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
import android.provider.CalendarContract
import android.support.annotation.Keep
import android.text.TextUtils
import ch.deletescape.lawnchair.util.Temperature
import com.android.launcher3.Launcher
import com.android.launcher3.R
import java.util.*

@Keep
class BuiltInCalendarProvider(controller: LawnchairSmartspaceController) :
        LawnchairSmartspaceController.DataProvider(controller) {

    private val iconProvider = WeatherIconProvider(controller.context)
    private val weather = LawnchairSmartspaceController.WeatherData(
            iconProvider.getIcon("-1"),
            Temperature(0, Temperature.Unit.Celsius), ""
                                                                   )
    private var card: LawnchairSmartspaceController.CardData? = null
    private val contentResolver = Launcher.fromContext(controller.context).contentResolver;

    init {
        forceUpdate()
    }

    override fun forceUpdate() {
        val currentTime = GregorianCalendar();
        val endTime = GregorianCalendar();
        endTime.add(Calendar.MINUTE, 120);
        updateData(weather, card)
        val query =
                "(( " + CalendarContract.Events.DTSTART + " >= " + currentTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ))"
        val eventCursorNullable: Cursor? = contentResolver.query(
                CalendarContract.Events.CONTENT_URI, arrayOf(
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.DTSTART, CalendarContract.Instances.DTEND,
                CalendarContract.Instances.DESCRIPTION
                                                            ),
                query, null, "ASC"
                                                                )
        if (eventCursorNullable == null) {
            card = null
            updateData(weather, card);
            return;
        }
        val eventCursor = eventCursorNullable
        eventCursor.moveToFirst();
        val title = eventCursor.getString(1);
        val startTime = GregorianCalendar()
        startTime.timeInMillis = eventCursor.getLong(2);
        val eventEndTime = GregorianCalendar()
        eventEndTime.timeInMillis = eventCursor.getLong(3)
        val description = eventCursor.getString(4);
        eventCursor.close();
        val diff = currentTime.compareTo(startTime);
        val diffSeconds = diff / 1000 % 60
        val diffMinutes = diff / (60 * 1000) % 60
        val diffHours = diff / (60 * 60 * 1000)
        card = LawnchairSmartspaceController.CardData(
                iconProvider.getIcon("-1"), title, TextUtils.TruncateAt.MARQUEE,
                controller.context.getString(
                        R.string.subtitle_smartspace_in_minutes,
                        diffMinutes), TextUtils.TruncateAt.END)
        super.forceUpdate()
    }

    override fun performSetup() {
        if (Launcher.getLauncher(controller.context).checkSelfPermission(
                        android.Manifest.permission.READ_CALENDAR
                                                                        ) != PackageManager.PERMISSION_GRANTED) {
            throw RuntimeException(
                    "The permission android.permission.READ_CALENDAR was not granted!"
                                  )
        }
        super.performSetup()
    }
}

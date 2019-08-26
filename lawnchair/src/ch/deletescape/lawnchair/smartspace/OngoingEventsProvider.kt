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
import android.provider.CalendarContract
import android.support.annotation.Keep
import android.text.TextUtils
import ch.deletescape.lawnchair.drawableToBitmap
import com.android.launcher3.R
import java.util.*
import java.util.concurrent.TimeUnit

@Keep
class OngoingEventsProvider(controller: LawnchairSmartspaceController) :
        LawnchairSmartspaceController.PeriodicDataProvider(controller) {
    private var silentlyFail: Boolean = false
    private var card: LawnchairSmartspaceController.CardData? = null
    private val contentResolver
        get() = context.contentResolver
    override val timeout = TimeUnit.SECONDS.toMillis(5)
    private fun updateInformation() {
        silentlyFail = controller.context.checkSelfPermission(
                android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
        if (silentlyFail) {
            updateData(null, null);
            return;
        }
        val currentTime = GregorianCalendar();
        val query =
                "(( " + CalendarContract.Events.DTSTART + " <= " + currentTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTEND + " >= " + currentTime.getTimeInMillis() + " ))"
        val eventCursorNullable: Cursor? = contentResolver
                .query(CalendarContract.Events.CONTENT_URI,
                       arrayOf(CalendarContract.Instances.TITLE, CalendarContract.Instances.DTSTART,
                               CalendarContract.Instances.DTEND,
                               CalendarContract.Instances.DESCRIPTION,
                               CalendarContract.Instances.ALL_DAY, CalendarContract.Events._ID),
                       query, null, CalendarContract.Instances.DTSTART + " ASC")
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
            val lines = listOf(LawnchairSmartspaceController.Line(
                    if (title == null || title.trim().isEmpty()) controller.context.getString(
                            R.string.placeholder_empty_title) else title,
                    TextUtils.TruncateAt.MARQUEE), LawnchairSmartspaceController.Line(
                    controller.context.getString(if (eventCursor.getInt(
                                    4) != 0) R.string.reusable_string_all_day_event else R.string.ongoing),
                    TextUtils.TruncateAt.END))
            val description = eventCursor.getString(3);
            card = LawnchairSmartspaceController.CardData(drawableToBitmap(
                    controller.context.getDrawable(R.drawable.ic_event_black_24dp)), lines, true)
            eventCursor.close();
            updateData(null, card)
            return;
        } catch (e: CursorIndexOutOfBoundsException) {
            updateData(null, card = null)
        }
        updateData(null, null)
    }

    override fun updateData() {
        updateInformation()
    }

    override fun forceUpdate() {
        updateInformation()
    }
}

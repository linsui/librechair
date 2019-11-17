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

import android.text.TextUtils
import androidx.annotation.Keep
import ch.deletescape.lawnchair.awareness.CalendarManager
import ch.deletescape.lawnchair.awareness.TickManager
import ch.deletescape.lawnchair.drawableToBitmap
import ch.deletescape.lawnchair.feed.CalendarScope
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Keep
class OngoingEventsProvider(controller: LawnchairSmartspaceController) :
        LawnchairSmartspaceController.DataProvider(controller) {
    private val events = mutableListOf<CalendarManager.CalendarEvent>()
    private val ongoingEvents = mutableListOf<CalendarManager.CalendarEvent>()

    init {
        CalendarManager.subscribe {
            events.clear()
            events += it.filter {
                it.startTime >= LocalDateTime.now() &&
                        it.startTime <= LocalDateTime.now()
                        .plusDays(context.lawnchairPrefs.feedCalendarEventThreshold.toLong())
            }
            d("init: events are $events")
            ongoingEvents.clear()
            ongoingEvents += it.filter {
                it.startTime <= LocalDateTime.now() &&
                        it.endTime >= LocalDateTime.now()
            }
            d("init: ongoing events are $ongoingEvents")
            updateInformation()
        }

        TickManager.subscribe {
            CalendarScope.launch {
                val backup = listOf(* events.toTypedArray())
                events.clear()
                events += backup.filter {
                    it.startTime >= LocalDateTime.now() &&
                            it.startTime <= LocalDateTime.now()
                            .plusDays(context.lawnchairPrefs.feedCalendarEventThreshold.toLong())
                }
                val ongoingBackup = listOf(* ongoingEvents.toTypedArray())
                ongoingEvents += ongoingBackup.filter {
                    d("init: (tick) ongoing event: $it currentTime: ${LocalDateTime.now()}")
                    it.startTime.toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(Instant.now())) <= System.currentTimeMillis() / 1000 &&
                            it.startTime.toEpochSecond(
                                    ZoneOffset.systemDefault().rules.getOffset(Instant.now())) >= System.currentTimeMillis() / 1000
                }
                d("init: (tick) ongoing events are $ongoingEvents")
                updateInformation()
            }
        }
    }
    private fun updateInformation() {
        if (ongoingEvents.isNotEmpty()) {
            val title = ongoingEvents.first().title
            val lines = listOf(LawnchairSmartspaceController.Line(
                    if (title.trim().isEmpty()) controller.context.getString(
                            R.string.placeholder_empty_title) else title,
                    TextUtils.TruncateAt.MARQUEE), LawnchairSmartspaceController.Line(
                    controller.context.getString(if (ongoingEvents.first().isAllDay) R.string.reusable_string_all_day_event else R.string.ongoing),
                    TextUtils.TruncateAt.END))
            updateData(null, LawnchairSmartspaceController.CardData(drawableToBitmap(
                    controller.context.getDrawable(R.drawable.ic_event_black_24dp)!!), lines, true))
            return
        } else {
            updateData(null, null)
        }
    }

    override fun forceUpdate() {
        updateInformation()
    }
}

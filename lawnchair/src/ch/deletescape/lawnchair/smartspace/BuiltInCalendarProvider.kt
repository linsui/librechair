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
import android.text.TextUtils
import androidx.annotation.Keep
import ch.deletescape.lawnchair.awareness.CalendarManager
import ch.deletescape.lawnchair.awareness.TickManager
import ch.deletescape.lawnchair.drawableToBitmap
import ch.deletescape.lawnchair.feed.CalendarScope
import ch.deletescape.lawnchair.formatTime
import ch.deletescape.lawnchair.runOnMainThread
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@Keep
class BuiltInCalendarProvider(controller: LawnchairSmartspaceController) :
        LawnchairSmartspaceController.DataProvider(controller) {
    private var card: LawnchairSmartspaceController.CardData? = null
    private val contentResolver
        get() = context.contentResolver
    private val events = mutableListOf<CalendarManager.CalendarEvent>()

    init {
        CalendarManager.subscribe {
            events.clear()
            events += it.filter {
                it.startTime >= LocalDateTime.now() &&
                        it.startTime <= LocalDateTime.now()
                        .plusHours(2)
            }
            d("init: $events")
            runOnMainThread {
                updateInformation()
            }
        }
        TickManager.subscribe {
            CalendarScope.launch {
                events.clear()
                events += events.filter {
                    it.startTime >= LocalDateTime.now() &&
                            it.startTime <= LocalDateTime.now()
                            .plusHours(2)
                }
                runOnMainThread {
                    updateInformation()
                }
            }
        }
    }

    private fun updateInformation() {
        if (events.isNotEmpty()) {
            val event = events.first()
            val diff = event.startTime.toEpochSecond(ZoneId.systemDefault().rules.getOffset(
                    Instant.now())) * 1000 - System.currentTimeMillis()
            val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            val text = if (diffMinutes <= 0) controller.context.getString(
                    R.string.reusable_str_now) else controller.context.getString(
                    if (diffMinutes < 1 || diffMinutes > 1) R.string.subtitle_smartspace_in_minutes else R.string.subtitle_smartspace_in_minute,
                    diffMinutes)
            val lines = mutableListOf(LawnchairSmartspaceController.Line(
                    if (event.title.trim().isEmpty()) controller.context.getString(
                            R.string.placeholder_empty_title) else event.title,
                    TextUtils.TruncateAt.MARQUEE),
                    LawnchairSmartspaceController.Line(
                            text,
                            TextUtils.TruncateAt.END),
                    LawnchairSmartspaceController.Line(
                            formatTime(ZonedDateTime.of(event.startTime, ZoneId.systemDefault()),
                                    context)))
            card = LawnchairSmartspaceController.CardData(drawableToBitmap(
                    controller.context.getDrawable(R.drawable.ic_event_black_24dp)!!),
                    lines,
                    PendingIntent.getActivity(
                            controller.context, 0, event.intent, 0,
                            null))
            updateData(null, card)
        } else {
            updateData(null, null)
        }
    }

    override fun forceUpdate() {
        updateInformation()
    }
}

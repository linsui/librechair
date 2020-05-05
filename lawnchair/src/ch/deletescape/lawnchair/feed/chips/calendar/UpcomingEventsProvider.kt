/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.chips.calendar

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import ch.deletescape.lawnchair.awareness.CalendarManager
import ch.deletescape.lawnchair.awareness.TickManager
import ch.deletescape.lawnchair.feed.CalendarScope
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.feed.chips.ChipProvider
import ch.deletescape.lawnchair.feed.maps.MapScreen
import ch.deletescape.lawnchair.feed.maps.locationsearch.LocationSearchManager
import ch.deletescape.lawnchair.feed.util.FeedUtil
import ch.deletescape.lawnchair.fromDrawableRes
import ch.deletescape.lawnchair.lawnchairPrefs
import com.android.launcher3.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import java.time.LocalDateTime
import java.util.function.Consumer

class UpcomingEventsProvider(val context: Context) : ChipProvider() {
    private val events = mutableListOf<CalendarManager.CalendarEvent>()

    init {
        CalendarManager.subscribe { ce ->
            events.toImmutableList()
            events.clear()
            events += ce.filter {
                it.startTime >= LocalDateTime.now() &&
                        it.startTime <= LocalDateTime.now()
                        .plusDays(context.lawnchairPrefs.feedCalendarEventThreshold.toLong())
            }
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
            }
        }
    }

    @SuppressLint("Recycle")
    override fun getItems(context: Context):
            List<Item> = events.map {
        Item().apply {
            title = it.title
            icon = R.drawable.ic_event_black_24dp.fromDrawableRes(context)
            viewClickListener = Consumer { v ->
                if (it.address != null) {
                    FeedScope.launch {
                        val loc = LocationSearchManager.getInstance(context).get(it.address)
                        if (loc != null) {
                            FeedScope.launch(Dispatchers.Main) {
                                MapScreen(context, launcherFeed, loc.first, loc.second,
                                        13.0).display(launcherFeed, null, null, v)
                            }
                        } else {
                            FeedScope.launch(Dispatchers.Main) {
                                try {
                                    FeedUtil.startActivity(context, it.intent, v)
                                } catch (e: ActivityNotFoundException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
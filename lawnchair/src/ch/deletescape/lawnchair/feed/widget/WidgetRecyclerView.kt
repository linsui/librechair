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

package ch.deletescape.lawnchair.feed.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import ch.deletescape.lawnchair.feed.CalendarEventProvider
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.FeedForecastProvider
import ch.deletescape.lawnchair.feed.FeedWeatherStatsProvider
import ch.deletescape.lawnchair.runOnNewThread

class WidgetRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
    val tickReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            runOnNewThread {
                (adapter as? FeedAdapter)?.refresh()
                post { adapter?.notifyDataSetChanged() }
            }
        }
    }.also {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
        context.registerReceiver(it, intentFilter)
    }

    init {
        adapter = FeedAdapter(
                listOf(FeedWeatherStatsProvider(context), FeedForecastProvider(context),
                       CalendarEventProvider(context)), 0, context, null);
        layoutManager = LinearLayoutManager(context)

        postDelayed({
                        runOnNewThread {
                            (adapter as? FeedAdapter)?.refresh()
                            post { adapter?.notifyDataSetChanged() }
                        }
                    }, 4000)
    }
}
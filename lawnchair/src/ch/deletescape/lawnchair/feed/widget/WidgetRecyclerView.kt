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
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import ch.deletescape.lawnchair.colors.ColorEngine
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.getFeedController
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.runOnNewThread
import ch.deletescape.lawnchair.setAlpha
import kotlin.math.roundToInt

class WidgetRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
    init {
            background = ColorDrawable(
                    ColorEngine.getInstance(context).feedBackground.value.resolveColor().setAlpha(
                            (context.lawnchairPrefs.feedBackgroundOpacity * (255f / 100f)).roundToInt()))
            adapter = FeedAdapter(getFeedController(context).getProviders(),
                                  ColorEngine.getInstance(
                                          context).feedBackground.value.resolveColor().setAlpha(
                                          (context.lawnchairPrefs.feedBackgroundOpacity * (255 / 100)).roundToInt()),
                                  context, null);
            layoutManager = LinearLayoutManager(context)

            postDelayed({
                            runOnNewThread {
                                (adapter as? FeedAdapter)?.refresh()
                                post {
                                    adapter?.notifyDataSetChanged()
                                }
                            }
                        }, 4000)
    }

    val tickReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            runOnNewThread {
                (adapter as? FeedAdapter)?.refresh()
                post {
                    scrollToPosition(0)
                    isLayoutFrozen = true
                    postDelayed({
                                    adapter?.notifyDataSetChanged()
                                    isLayoutFrozen = false
                                }, 100)
                }
            }
        }
    }.also {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
        context.registerReceiver(it, intentFilter)
    }
}
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

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import ch.deletescape.lawnchair.colors.ColorEngine
import ch.deletescape.lawnchair.feed.*
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.setAlpha
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class WidgetRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
    init {
        background = ColorDrawable(
                ColorEngine.getInstance(context).feedBackground.value.resolveColor().setAlpha(
                        (context.lawnchairPrefs.feedBackgroundOpacity * (255f / 100f)).roundToInt()))
        adapter = FeedAdapter(
                listOf(FeedWeatherStatsProvider(context), CalendarEventProvider(context),
                        TheGuardianFeedProvider(context)),
                ColorEngine.getInstance(
                        context).feedBackground.value.resolveColor().setAlpha(
                        (context.lawnchairPrefs.feedBackgroundOpacity * (255 / 100)).roundToInt()),
                context, null)
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        postDelayed({
            FeedScope.launch {
                (adapter as? FeedAdapter)?.refresh()
                post {
                    adapter?.notifyDataSetChanged()
                }
            }
        }, 4000)
    }

}
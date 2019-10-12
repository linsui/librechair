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

package ch.deletescape.lawnchair.feed

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.deletescape.lawnchair.applyAsDip
import ch.deletescape.lawnchair.feed.impl.OverlayService
import ch.deletescape.lawnchair.fromStringRes
import ch.deletescape.lawnchair.predictions.PredictedApplicationsAdapter
import com.android.launcher3.R
import kotlin.math.roundToInt


class PredictedAppsProvider(c: Context) : FeedProvider(c) {
    private val recyclerView = RecyclerView(context)
    private val adapter = PredictedApplicationsAdapter(c)

    init {
        recyclerView.adapter = adapter
        recyclerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                              LinearLayout.LayoutParams.WRAP_CONTENT)
        recyclerView.layoutManager = GridLayoutManager(context, adapter.gridSize)
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                        state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.top = 8f.applyAsDip(context).roundToInt()
                outRect.bottom = 8f.applyAsDip(context).roundToInt()
                outRect.left = 8f.applyAsDip(context).roundToInt()
                outRect.right = 8f.applyAsDip(context).roundToInt()
            }
        })
        adapter.notifyDataSetChanged()
    }

    private fun refreshPredictions() {
        adapter.predictions = OverlayService.CompanionService.InterfaceHolder.getPredictions()
        adapter.notifyDataSetChanged()
    }

    override fun isVolatile() = true

    override fun onFeedShown() {
    }

    override fun onFeedHidden() {
    }

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun getCards(): List<Card> {
        refreshPredictions()
        return listOf(
                Card(null, R.string.title_card_suggested_apps.fromStringRes(context), { v, _ ->
                    recyclerView
                }, Card.RAISE, "nosort, top", "predictedApps".hashCode()))
    }
}

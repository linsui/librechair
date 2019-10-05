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
import ch.deletescape.lawnchair.feed.impl.OverlayService
import ch.deletescape.lawnchair.fromStringRes
import ch.deletescape.lawnchair.predictions.PredictedApplicationsAdapter
import com.android.launcher3.R
import com.google.android.flexbox.FlexboxLayoutManager

class PredictedAppsProvider(c: Context) : FeedProvider(c) {
    private val recyclerView = androidx.recyclerview.widget.RecyclerView(context)
    private val adapter = PredictedApplicationsAdapter(c)

    init {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = FlexboxLayoutManager(context)
        adapter.notifyDataSetChanged()
    }

    private fun refreshPredictions() {
        adapter.predictions = OverlayService.CompanionService.InterfaceHolder.getPredictions()
        adapter.notifyDataSetChanged()
    }

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

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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.allapps.PredictionsProvider
import ch.deletescape.lawnchair.predictions.LawnchairEventPredictor
import ch.deletescape.lawnchair.predictions.PredictedApplicationsAdapter
import ch.deletescape.lawnchair.predictions.PredictionsProviderService
import ch.deletescape.lawnchair.util.extensions.currentStackTrace
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.BuildConfig
import com.android.launcher3.LauncherAppState
import com.android.launcher3.R
import com.android.launcher3.logging.UserEventDispatcher
import com.google.android.apps.nexuslauncher.CustomAppPredictor
import com.google.android.apps.nexuslauncher.allapps.PredictionRowView
import com.google.android.apps.nexuslauncher.util.ComponentKeyMapper

class PredictedAppsProvider(c: Context) : FeedProvider(c) {
    private val recyclerView = RecyclerView(context)
    private val adapter = PredictedApplicationsAdapter()
    private var predictions: List<ComponentKeyMapper> = emptyList()

    init {
        recyclerView.adapter = adapter
        adapter.predictions = predictions
        recyclerView.layoutManager = GridLayoutManager(context, adapter.gridSize)
        adapter.notifyDataSetChanged()
        d("init: refreshing predictions")
        refreshPredictions()
    }

    fun refreshPredictions() {
        d("refreshPredictions: refreshing predictions")
        context.bindService(Intent().setComponent(ComponentName(BuildConfig.APPLICATION_ID,
                                                                PredictionsProviderService::class.java.name)),
                            object : ServiceConnection {
                                override fun onServiceDisconnected(name: ComponentName?) {
                                }

                                override fun onServiceConnected(name: ComponentName?,
                                                                service: IBinder?) {
                                    d("onServiceConnected: connected to prediction service")
                                    predictions = PredictionsProvider.Stub.asInterface(service)
                                            .predictions
                                            .map { ComponentKeyMapper(context, it.componentKey) }
                                            .also {
                                                it.forEach {
                                                    d("refreshPredictions: got prediction $it")
                                                }
                                            }
                                    adapter.predictions = predictions
                                    adapter.notifyDataSetChanged()
                                }
                            }, Context.BIND_AUTO_CREATE)
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

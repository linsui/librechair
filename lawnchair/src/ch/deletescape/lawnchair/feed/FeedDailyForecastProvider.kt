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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ch.deletescape.lawnchair.LawnchairApp
import ch.deletescape.lawnchair.awareness.WeatherManager
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider
import ch.deletescape.lawnchair.smartspace.weather.owm.OWMWeatherActivity
import ch.deletescape.lawnchair.useWhiteText
import com.android.launcher3.R

class FeedDailyForecastProvider(c: Context) : FeedProvider(c) {
    private var forecast: ForecastProvider.DailyForecast? = null

    init {
        WeatherManager.subscribeDaily { forecast = it }
    }

    override fun onFeedShown() {
        // TODO
    }

    override fun onFeedHidden() {
        // TODO
    }

    override fun onCreate() {
        // TODO
    }

    override fun onDestroy() {
        // TODO
    }

    override fun getCards(): List<Card> {
        return if (forecast == null) emptyList() else listOf(
                Card(null,
                        context.getString(R.string.forecast_s),
                        object : Card.Companion.InflateHelper {
                            override fun inflate(parent: ViewGroup): View {
                                val recyclerView = LayoutInflater.from(parent.context).inflate(
                                        R.layout.width_inflatable_recyclerview, parent,
                                        false) as androidx.recyclerview.widget.RecyclerView
                                if (forecast != null) {
                                    recyclerView.layoutManager =
                                            LinearLayoutManager(
                                                    context,
                                                    LinearLayoutManager.HORIZONTAL,
                                                    false)
                                    recyclerView.adapter = OWMWeatherActivity
                                            .DailyForecastAdapter(forecast!!, context,
                                                    (context.applicationContext as LawnchairApp).lawnchairPrefs.weatherUnit,
                                                    useWhiteText(backgroundColor, parent.context))
                                }
                                recyclerView.layoutParams = ViewGroup
                                        .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.MATCH_PARENT)
                                return recyclerView
                            }

                        }, Card.NO_HEADER, "nosort,top"))
    }
}
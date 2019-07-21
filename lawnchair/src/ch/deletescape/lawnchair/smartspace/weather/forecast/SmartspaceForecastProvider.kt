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

package ch.deletescape.lawnchair.smartspace.weather.forecast

import android.text.TextUtils
import ch.deletescape.lawnchair.forecastProvider
import ch.deletescape.lawnchair.runOnMainThread
import ch.deletescape.lawnchair.runOnNewThread
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.*
import com.android.launcher3.R
import java.util.concurrent.TimeUnit

class SmartspaceForecastProvider(controller: LawnchairSmartspaceController) :
        PeriodicDataProvider(controller), Listener {
    private var lat: Double? = null
    private var lon: Double? = null
    override val timeout: Long
        get() = TimeUnit.MINUTES.toMillis(5)

    init {
        controller.addListener(this)
    }

    override fun updateData() {
        super.updateData()
        runOnNewThread {
            if (lat != null && lon != null) {
                val lat = lat!!
                val lon = lon!!
                try {
                    val forecast = context.forecastProvider.getHourlyForecast(lat, lon)
                    if (forecast.data.firstOrNull()?.condCode?.filter { it in 600..699 }?.size ?: 0 > 0) {
                        val data = CardData(null, listOf(
                            Line(context.getString(R.string.title_card_incoming_snow),
                                 TextUtils.TruncateAt.MARQUEE)), true)
                        val current = context.forecastProvider.getCurrentWeather(lat, lon)
                        if (current.condCodes.any { it in 600..699 }) {
                            runOnMainThread {
                                updateData(null, data)
                            }
                        }
                    } else if (forecast.data.firstOrNull()?.condCode?.filter { it in 200..299 }?.size ?: 0 > 0) {
                        val data = CardData(null, listOf(
                            Line(context.getString(R.string.title_card_incoming_thunder),
                                 TextUtils.TruncateAt.MARQUEE)), true)
                        val current = context.forecastProvider.getCurrentWeather(lat, lon)
                        if (current.condCodes.any { it in 200..299 }) {
                            runOnMainThread {
                                updateData(null, data)
                            }
                        }
                    } else if (forecast.data.firstOrNull()?.condCode?.filter { it in 300..599 }?.size ?: 0 > 0) {
                        val data = CardData(null, listOf(
                            Line(context.getString(R.string.title_card_upcoming_rain),
                                 TextUtils.TruncateAt.MARQUEE)), true)
                        val current = context.forecastProvider.getCurrentWeather(lat, lon)
                        if (current.condCodes.any { it in 300..599 }) {
                            runOnMainThread {
                                updateData(null, data)
                            }
                        }
                    }
                } catch (e: ForecastProvider.ForecastException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDataUpdated(data: DataContainer) {
        if (data.weather?.coordLat != null && data.weather.coordLon != null) {
            lon = data.weather.coordLat
            lat = data.weather.coordLon
            forceUpdate()
        }
    }
}

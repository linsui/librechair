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
import ch.deletescape.lawnchair.awareness.WeatherManager
import ch.deletescape.lawnchair.runOnMainThread
import ch.deletescape.lawnchair.runOnNewThread
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.*
import ch.deletescape.lawnchair.smartspace.weather.icons.WeatherIconManager
import com.android.launcher3.R

class SmartspaceForecastProvider(controller: LawnchairSmartspaceController) :
        DataProvider(controller) {

    private lateinit var forecast: ForecastProvider.Forecast
    private lateinit var current: ForecastProvider.CurrentWeather

    init {
        WeatherManager.subscribeWeather {
            current = it
            updateData()
        }
        WeatherManager.subscribeHourly {
            forecast = it
            updateData()
        }
    }

    fun updateData() {
        runOnNewThread {
            if (::forecast.isInitialized && ::current.isInitialized) {
                try {
                    if (forecast.data.firstOrNull()?.condCode?.filter { it in 600..699 }?.size ?: 0 > 0) {
                        val data = CardData(WeatherIconManager.getInstance(context).getIcon(
                                WeatherIconManager.Icon.SNOW, false), listOf(Line(
                                context.getString(R.string.title_card_incoming_snow),
                                TextUtils.TruncateAt.END)), true)
                        if (current.condCodes.any { it in 600..699 }) {
                            runOnMainThread {
                                updateData(null, data)
                            }
                        }
                    } else if (forecast.data.firstOrNull()?.condCode?.filter { it in 200..299 }?.size ?: 0 > 0) {
                        val data = CardData(WeatherIconManager.getInstance(context).getIcon(
                                WeatherIconManager.Icon.THUNDERSTORMS, false), listOf(Line(
                                context.getString(R.string.title_card_incoming_thunder),
                                TextUtils.TruncateAt.END)), true)
                        if (current.condCodes.any { it in 200..299 }) {
                            runOnMainThread {
                                updateData(null, data)
                            }
                        }
                    } else if (forecast.data.firstOrNull()?.condCode?.filter { it in 300..599 }?.size ?: 0 > 0) {
                        val data = CardData(WeatherIconManager.getInstance(context).getIcon(
                                WeatherIconManager.Icon.RAIN, false), listOf(Line(
                                context.getString(R.string.title_card_upcoming_rain),
                                TextUtils.TruncateAt.END)), true)
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
}

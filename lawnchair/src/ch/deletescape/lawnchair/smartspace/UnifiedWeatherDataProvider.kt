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

package ch.deletescape.lawnchair.smartspace

import ch.deletescape.lawnchair.forecastProvider
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.runOnNewThread
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.PeriodicDataProvider
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider
import java.util.concurrent.TimeUnit

class UnifiedWeatherDataProvider(
        controller: LawnchairSmartspaceController) : PeriodicDataProvider(controller) {

    override val timeout: Long
        get() = TimeUnit.MINUTES.toMillis(10)

    override fun updateData() {
        try {
            runOnNewThread {
                val (lat, lon) =
                        context.forecastProvider.getGeolocation(context.lawnchairPrefs.weatherCity)
                context.forecastProvider.getCurrentWeather(lat, lon).also {
                    updateData(
                            LawnchairSmartspaceController.WeatherData(it.icon, it.temperature, null,
                                                                      null, null, lat, lon, "-1d"),
                            null)
                }
            }
        } catch (e: ForecastProvider.ForecastException) {
            e.printStackTrace();
        }

    }
}

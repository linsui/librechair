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

import android.content.Context
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.WeatherData
import com.android.launcher3.R
import java.util.*

interface ForecastProvider {

    @Throws(ForecastException::class) fun getHourlyForecast(lat: Double, lon: Double): Forecast
    @Throws(ForecastException::class) fun getDailyForecast(lat: Double, lon: Double): DailyForecast

    class Forecast(val data: Array<ForecastData>, vararg weatherCodes: Int) {
        private val weatherCodes: IntArray

        init {
            this.weatherCodes = weatherCodes
        }

        constructor(dataList: List<ForecastData>, vararg weatherCodes: Int) : this(
            Arrays.copyOf<ForecastData, Any>(dataList.toTypedArray(), dataList.size,
                                             Array<ForecastData>::class.java), *weatherCodes) {
        }
    }

    data class DailyForecast(private val dailyForecastData: List<DailyForecastData>)


    data class DailyForecastData(private val high: Int, private val low: Int, private val icon: String,
                            private val forecast: Forecast?)

    data class ForecastData(private val data: WeatherData, private val date: Date)

    class ForecastException(e: Throwable) : RuntimeException(e)

    class Controller {
        companion object {
            fun getProviderList(c: Context): List<String> {
                return listOf(OWMForecastProvider::class.java.name)
            }

            fun getProviderName(c: Context, provider: String): String {
                return when (provider) {
                    OWMForecastProvider::class.java.name -> c.getString(R.string.weather_provider_owm)

                    else -> error("no such provider $provider")
                }
            }
        }
    }
}

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
import android.graphics.Bitmap
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.WeatherData
import ch.deletescape.lawnchair.util.Temperature
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import java.io.IOException
import java.util.*

interface ForecastProvider {

    @Throws(ForecastException::class) fun getHourlyForecast(lat: Double, lon: Double): Forecast
    @Throws(ForecastException::class) fun getDailyForecast(lat: Double, lon: Double): DailyForecast
    @Throws(ForecastException::class) fun getCurrentWeather(lat: Double, lon: Double): CurrentWeather

    class Forecast(val data: Array<ForecastData>) {
        constructor(dataList: List<ForecastData>) : this(dataList.toTypedArray())
    }

    data class CurrentWeather(val condCodes: Array<Int>, val date: Date, val temperature: Temperature) {
        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            } else if (javaClass != other?.javaClass) {
                return false
            }

            other as CurrentWeather

            if (!condCodes.contentEquals(other.condCodes)){
                return false
            } else if (date != other.date) {
                return false
            } else if (temperature != other.temperature) {
                return false
            }

            return true
        }

        override fun hashCode(): Int {
            var result = condCodes.contentHashCode()
            result = 31 * result + date.hashCode()
            result = 31 * result + temperature.hashCode()
            return result
        }

    }

    data class DailyForecast(val dailyForecastData: List<DailyForecastData>)


    data class DailyForecastData(val high: Temperature, val low: Temperature, val date: Date,
                                 val icon: Bitmap, val forecast: Forecast?)

    data class ForecastData(val data: WeatherData, val date: Date, val condCode: Array<Int>?) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ForecastData

            if (data != other.data) {
                return false
            }
            if (date != other.date) {
                return false
            }
            if (condCode != null) {
                if (other.condCode == null) {
                    return false
                }
                if (!condCode.contentEquals(other.condCode)) {
                    return false
                }
            } else if (other.condCode != null) return false

            return true
        }

        override fun hashCode(): Int {
            var result = data.hashCode()
            result = 31 * result + date.hashCode()
            result = 31 * result + (condCode?.contentHashCode() ?: 0)
            return result
        }
    }

    class ForecastException : IOException {
        constructor(s: String) : super(s)
        constructor(e: Throwable) : super(e)
    }

    class Controller {
        companion object {
            fun getProviderList(): List<String> {
                return listOf(OWMForecastProvider::class.java.name,
                              AccuWeatherForecastProvider::class.java.name,
                              WeatherChannelForecastProvider::class.java.name)
            }

            fun getProviderName(c: Context, provider: String): String {
                return when (provider) {
                    OWMForecastProvider::class.java.name -> c.getString(
                        R.string.weather_provider_owm)
                    AccuWeatherForecastProvider::class.java.name -> c.getString(R.string.weather_provider_accu)
                    WeatherChannelForecastProvider::class.java.name -> c.getString(R.string.title_weather_provider_weather_com)

                    else -> error("no such provider $provider")
                }
            }

            fun inflateForecastProvider(c: Context, provider: String): ForecastProvider {
                d("inflateForecastProvider: inflating $provider")
                val clazz = Class.forName(provider)
                val constructor = clazz.getConstructor(Context::class.java)
                return constructor.newInstance(c) as ForecastProvider
            }
        }
    }
}

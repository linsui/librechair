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

package ch.deletescape.lawnchair

import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import java.util.*

enum class WeatherTypes {
    CLEAR, CLEAR_CLOUDS, CLEAR_RAIN, CLEAR_SNOW, CLEAR_THUNDER, CLOUDS, CLOUDS_CLEAR, CLOUDS_RAIN, CLOUDS_SNOW, CLOUDS_THUNDER, RAIN, RAIN_CLEAR, RAIN_CLOUDS, RAIN_SNOW, RAIN_THUNDER, THUNDER, THUNDER_CLEAR, THUNDER_CLOUDS, THUNDER_SNOW, THUNDER_RAIN, SNOW, SNOW_CLEAR, SNOW_CLOUDS, SNOW_RAIN, SNOW_THUNDER;

    companion object {
        fun getStringResource(type: WeatherTypes): Int {
            return when (type) {
                WeatherTypes.CLEAR -> R.string.hud_weather_information_sunny
                WeatherTypes.CLEAR_CLOUDS -> R.string.hud_weather_information_sunny_occasional_clouds
                WeatherTypes.CLEAR_RAIN -> R.string.hud_weather_information_sunny_occasional_rain
                WeatherTypes.CLEAR_SNOW -> R.string.hud_weather_information_sunny_occasional_snow
                WeatherTypes.CLEAR_THUNDER -> R.string.hud_weather_information_sunny_occasional_thunder
                WeatherTypes.CLOUDS -> R.string.hud_weather_information_cloudy
                WeatherTypes.CLOUDS_CLEAR -> R.string.hud_weather_information_cloudy_occasional_sun
                WeatherTypes.CLOUDS_RAIN -> R.string.hud_weather_information_cloudy_occasional_rain
                WeatherTypes.CLOUDS_SNOW -> R.string.hud_weather_information_cloudy_occasional_snow
                WeatherTypes.CLOUDS_THUNDER -> R.string.hud_weather_information_cloudy_occasional_thunder
                WeatherTypes.RAIN -> R.string.hud_weather_information_rainy
                WeatherTypes.RAIN_CLEAR -> R.string.hud_weather_information_rainy_occasional_sun
                WeatherTypes.RAIN_CLOUDS -> R.string.hud_weather_information_rainy_occasional_clouds
                WeatherTypes.RAIN_SNOW -> R.string.hud_weather_information_rainy_occasional_snow
                WeatherTypes.RAIN_THUNDER -> R.string.hud_weather_information_rainy_occasional_thunder
                WeatherTypes.THUNDER -> R.string.hud_weather_information_thunder
                WeatherTypes.THUNDER_CLEAR -> R.string.hud_weather_information_thunder_occasional_sun
                WeatherTypes.THUNDER_CLOUDS -> R.string.hud_weather_information_thunder_occasional_clouds
                WeatherTypes.THUNDER_SNOW -> R.string.hud_weather_information_thunder_occasional_snow
                WeatherTypes.THUNDER_RAIN -> R.string.hud_weather_information_thunder_occasional_rain
                WeatherTypes.SNOW -> R.string.hud_weather_information_snowy
                WeatherTypes.SNOW_CLEAR -> R.string.hud_weather_information_snowy_occasional_sun
                WeatherTypes.SNOW_CLOUDS -> R.string.hud_weather_information_snowy_occasional_clouds
                WeatherTypes.SNOW_RAIN -> R.string.hud_weather_information_snowy_occasional_rain
                WeatherTypes.SNOW_THUNDER -> R.string.hud_weather_information_snowy_occasional_thunder
            }
        }

        fun getStatistics(conditionCodes: Array<Int>): Array<Int> {
            var thunder = 0
            var rain = 0
            var snow = 0
            var clear = 0
            var clouds = 0
            conditionCodes.forEach {
                when {
                    it in 200..299 -> {
                        thunder += if (it - 200 < 10) 1 else if (it - 200 < 20) 5 else 10
                        rain += 5
                    }
                    it in 300..399 -> {
                        rain += 1
                    }
                    it in 500..599 -> {
                        rain += if (it - 400 < 10) 1 else if (it - 400 < 20) 2 else 5
                    }
                    it in 600..699 -> {
                        snow += if (it - 600 < 10) 3 else if (it - 600 < 20) 5 else 10
                    }
                    it in 800..899 -> {
                        if (it != 800) {
                            if (it - 800 < 2) {
                                clear += 2
                            } else if (it - 800 == 3) {
                                clear += 1
                            }
                            clouds += ((it - 800) / 1.25).toInt()
                        } else {
                            clear += 5
                        }
                    }
                    else -> {
                    }
                }
            }
            return arrayOf(clear, clouds, rain, snow, thunder)
        }

        fun getWeatherTypeFromStatistics(clear: Int, clouds: Int, rain: Int, snow: Int,
                                         thunder: Int): WeatherTypes {
            d("getWeatherTypeFromStatistics: concatenating data")
            val concat = arrayListOf(clear, clouds, rain, snow, thunder)
            d("getWeatherTypeFromStatistics: data: $concat")
            val main = Collections.max(concat)
            var secondary = Collections.max(concat.filter { it != main })
            if (secondary < (main * (40 / 100) /* Magic threshold though I don't know why */)) {
                secondary = main
            }
            d("getWeatherTypeFromStatistics: deducing result ${concat.indexOf(main)} and then ${concat.indexOf(secondary)}")
            val result = when (concat.indexOf(main)) {
                0 -> {
                    when (concat.indexOf(secondary)) {
                        0 -> CLEAR
                        1 -> CLEAR_CLOUDS
                        2 -> CLEAR_RAIN
                        3 -> CLEAR_SNOW
                        4 -> CLEAR_THUNDER

                        else -> error("Invalid secondary index")
                    }
                }
                1 -> {
                    when (concat.indexOf(secondary)) {
                        0 -> CLOUDS_CLEAR
                        1 -> CLOUDS
                        2 -> CLOUDS_RAIN
                        3 -> CLOUDS_SNOW
                        4 -> CLOUDS_THUNDER

                        else -> error("Invalid secondary index")
                    }
                }
                2 -> {
                    when (concat.indexOf(secondary)) {
                        0 -> RAIN_CLEAR
                        1 -> RAIN_CLOUDS
                        2 -> RAIN
                        3 -> RAIN_SNOW
                        4 -> RAIN_THUNDER

                        else -> error("Invalid secondary index")
                    }
                }
                3 -> {
                    when (concat.indexOf(secondary)) {
                        0 -> SNOW_CLEAR
                        1 -> SNOW_CLOUDS
                        2 -> SNOW_RAIN
                        3 -> SNOW
                        4 -> SNOW_THUNDER

                        else -> error("Invalid secondary index")
                    }
                }
                4 -> {
                    when (concat.indexOf(secondary)) {
                        0 -> THUNDER_CLEAR
                        1 -> THUNDER_CLOUDS
                        2 -> THUNDER_RAIN
                        3 -> THUNDER_SNOW
                        4 -> THUNDER

                        else -> error("Invalid secondary index")
                    }
                }

                else -> error("Invalid main index")
            }
            d("getWeatherTypeFromStatistics: result is $result")
            return result
        }
    }
}
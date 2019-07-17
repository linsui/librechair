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

import java.util.*

enum class WeatherTypes {
    CLEAR, CLEAR_CLOUDS, CLEAR_RAIN, CLEAR_SNOW, CLEAR_THUNDER, CLOUDS, CLOUDS_CLEAR, CLOUDS_RAIN, CLOUDS_SNOW, CLOUDS_THUNDER, RAIN, RAIN_CLEAR, RAIN_CLOUDS, RAIN_SNOW, RAIN_THUNDER, THUNDER, THUNDER_CLEAR, THUNDER_CLOUDS, THUNDER_SNOW, THUNDER_RAIN, SNOW, SNOW_CLEAR, SNOW_CLOUDS, SNOW_RAIN, SNOW_THUNDER;

    companion object {
        public fun getWeatherTypeFromStatastics(clear: Int, clouds: Int, rain: Int, snow: Int,
                                         thunder: Int): WeatherTypes {
            val concat = arrayListOf(clear, clouds, rain, snow, thunder)
            val main = Collections.max(concat)
            val secondary = Collections.max(concat.filter { it != main })

            return when (concat.indexOf(main)) {
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
                2 -> {
                    when (concat.indexOf(secondary)) {
                        0 -> CLOUDS_CLEAR
                        1 -> CLOUDS
                        2 -> CLOUDS_RAIN
                        3 -> CLOUDS_SNOW
                        4 -> CLOUDS_THUNDER

                        else -> error("Invalid secondary index")
                    }
                }
                3 -> {
                    when (concat.indexOf(secondary)) {
                        0 -> RAIN_CLEAR
                        1 -> RAIN_CLOUDS
                        2 -> RAIN
                        3 -> RAIN_SNOW
                        4 -> RAIN_THUNDER

                        else -> error("Invalid secondary index")
                    }
                }
                4 -> {
                    when (concat.indexOf(secondary)) {
                        0 -> SNOW_CLEAR
                        1 -> SNOW_CLOUDS
                        2 -> SNOW_RAIN
                        3 -> SNOW
                        4 -> SNOW_THUNDER

                        else -> error("Invalid secondary index")
                    }
                }
                5 -> {
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
        }
    }
}
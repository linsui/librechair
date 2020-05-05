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

import android.location.Location
import ch.deletescape.lawnchair.awareness.WeatherManager
import ch.deletescape.lawnchair.runOnMainThread

class UnifiedWeatherDataProvider(
        controller: LawnchairSmartspaceController) :
        LawnchairSmartspaceController.DataProvider(controller) {
    private lateinit var location: Pair<Double, Double>

    init {
        WeatherManager.subscribeGeo {
            location = it
        }
        WeatherManager.subscribeWeather {
            runOnMainThread {
                updateData(
                        LawnchairSmartspaceController.WeatherData(it.icon,
                                it.temperature,
                                it.url,
                                null,
                                null,
                                location.first,
                                location.second,
                                "-1d"),
                        null)
            }
        }
    }
}

private operator fun Location?.component1(): Double? {
    return this?.latitude
}

private operator fun Location?.component2(): Double? {
    return this?.longitude
}


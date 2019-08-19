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

package ch.deletescape.lawnchair.location

import android.annotation.SuppressLint
import android.content.Context
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object LocationManager {
    var cache: Pair<Long, Pair<Double, Double>?>? = null
    var context: Context? = null
        set(value) = {
            providers.addAll(listOf(GpsLocationProvider(value!!), IPLocation(value),
                                    WeatherCityLocationProvider(value),
                                    LastKnownLocationProvider(value)))
            field = value
        }()
    val changeCallbacks: MutableList<(lat: Double, lon: Double) -> Unit> = mutableListOf()
    val providers: MutableList<LocationProvider> = mutableListOf()
    val location: Pair<Double, Double>?
        get() = run {
            if (cache == null || System.currentTimeMillis() - cache!!.first > TimeUnit.MINUTES.toMillis(
                            10) || cache!!.second == null) internalGet().also {
                cache = System.currentTimeMillis() to it
            }
            changeCallbacks.forEach {
                if (cache?.second != null) {
                    it(cache!!.second!!.first, cache!!.second!!.second)
                }
            }
            cache?.second
        }

    fun internalGet(): Pair<Double, Double>? {
        providers.forEach {
            if (it.location != null) {
                return it.location
            }
        }
        return null
    }

    abstract class LocationProvider(val context: Context) {
        abstract val location: Pair<Double, Double>?
    }
}
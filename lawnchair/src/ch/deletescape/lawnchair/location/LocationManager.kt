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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.lang3.tuple.MutablePair
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object LocationManager {
    val SYNC_LOCK = Any()
    val slots: MutableList<Pair<LocationProvider, MutablePair<Double?, Double?>>> = mutableListOf()
    var context: Context? = null
        set(value) = {
            providers.addAll(listOf(GpsLocationProvider(value!!), IPLocation(value),
                    LastKnownLocationProvider(value),
                    WeatherCityLocationProvider(value)))
            slots.clear()
            providers.forEach {
                slots.add(it to MutablePair.of<Double?, Double?>(null, null))
                GlobalScope.launch {
                    while (true) {
                        it.refresh()
                        delay(TimeUnit.MINUTES.toMillis(10))
                    }
                }
            }
            field = value
        }()
    val changeCallbacks: MutableList<(lat: Double, lon: Double) -> Unit> = mutableListOf()

    val providers: MutableList<LocationProvider> = mutableListOf()
    val location: Pair<Double, Double>?
        get() = slots.firstOrNull { it.second.left != null && it.second.right != null }?.second?.let { it.left!! to it.right!! }

    fun addCallback(callback: (lat: Double, lon: Double) -> Unit) {
        if (location != null) {
            callback(location!!.first, location!!.second)
        }
        changeCallbacks += callback
    }

    abstract class LocationProvider(val context: Context) {

        abstract fun refresh()

        fun updateLocation(lat: Double?, lon: Double?,
                           notifyCallbacks: Boolean = true) {
            synchronized(SYNC_LOCK) {
                slots.first { it.first == this }.second.apply {
                    left = lat
                    right = lon
                }
            }
            if (notifyCallbacks) {
                changeCallbacks.forEach {
                    if (location != null) {
                        it(location!!.first, location!!.second)
                    }
                }
            }
        }
    }
}
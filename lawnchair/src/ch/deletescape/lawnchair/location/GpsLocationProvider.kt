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
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import ch.deletescape.lawnchair.checkLocationAccess
import ch.deletescape.lawnchair.lastKnownPosition
import ch.deletescape.lawnchair.locationManager
import ch.deletescape.lawnchair.util.extensions.d
import java.util.concurrent.TimeUnit

class GpsLocationProvider(c: Context) : LocationManager.LocationProvider(c), LocationListener {
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    override fun onLocationChanged(location: Location?) {
        d("onLocationChanged: new location is $location")
        updateLocation(location?.latitude, location?.longitude)
    }

    override fun refresh() {
        updateLocation(location?.first, location?.second)
    }

    val location: Pair<Double, Double>?
        get() = run {
            try {
                if (context.checkLocationAccess()) {
                    val location = context.locationManager.lastKnownPosition
                    if (location != null) {
                        return@run location.latitude to location.longitude
                    }
                    return@run null
                } else {
                    return@run null
                }
            } catch (e: IllegalArgumentException) {
                return@run null;
            }
        }

    @SuppressLint("MissingPermission")
    override fun onInitialAttach() {
        if (context.checkLocationAccess()) {
            val provider = context.locationManager.getBestProvider(
                    Criteria(), true)
            if (provider != null) {
                context.locationManager.requestLocationUpdates(provider, TimeUnit.MINUTES.toMillis(1),
                        100f, this)
            }
        }
    }
}
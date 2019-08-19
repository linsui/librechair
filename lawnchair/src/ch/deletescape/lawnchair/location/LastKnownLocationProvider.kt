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

import android.content.Context
import ch.deletescape.lawnchair.lawnchairPrefs

class LastKnownLocationProvider(context: Context) : LocationManager.LocationProvider(context) {
    override val location: Pair<Double, Double>?
        get() = if (context.lawnchairPrefs.lastKnownLocation.isNotEmpty()) context.lawnchairPrefs.lastKnownLocation.split(
                ",")[0].toDouble() to context.lawnchairPrefs.lastKnownLocation.split(
                ",")[1].toDouble() else null

    init {
        LocationManager.changeCallbacks += { lat, lon ->
            context.lawnchairPrefs.lastKnownLocation = "$lat,$lon"
        }
    }
}
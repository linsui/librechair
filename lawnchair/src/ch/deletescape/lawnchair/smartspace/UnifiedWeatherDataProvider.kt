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

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.PeriodicDataProvider
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider
import ch.deletescape.lawnchair.util.extensions.d
import ch.deletescape.lawnchair.util.extensions.w
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

class UnifiedWeatherDataProvider(
        controller: LawnchairSmartspaceController) : PeriodicDataProvider(controller) {

    override val timeout: Long
        get() = TimeUnit.MINUTES.toMillis(10)

    private val locationManager: LocationManager? by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    }

    @SuppressLint("MissingPermission")
    override fun updateData() {
        try {
            runOnNewThread {
                if (context.lawnchairPrefs.weatherCity != "##Auto") {
                    d("updateData: retrieving current geolocation")
                    val (lat, lon) =
                            context.forecastProvider.getGeolocation(
                                    context.lawnchairPrefs.weatherCity)
                    d("updateData: geolocation is $lat, $lon")
                    val currentWeather = context.forecastProvider.getCurrentWeather(lat, lon)
                    d("updateData: current weather is ${Gson().toJson(currentWeather)}")
                    runOnMainThread {
                        updateData(
                                LawnchairSmartspaceController.WeatherData(currentWeather.icon,
                                                                          currentWeather.temperature,
                                                                          null,
                                                                          null, null, lat, lon,
                                                                          "-1d"),
                                null)
                    }
                } else {
                    val updateWeather = {
                        val locationProvider = locationManager?.getBestProvider(Criteria(), true)
                        val (lat, lon) = locationManager?.getLastKnownLocation(locationProvider)
                        val currentWeather = context.forecastProvider
                                .getCurrentWeather(lat ?: (-1).toDouble(), lon ?: (-1).toDouble());
                        d("updateData: current weather is ${Gson().toJson(currentWeather)}")
                        runOnMainThread {
                            updateData(
                                    LawnchairSmartspaceController.WeatherData(currentWeather.icon,
                                                                              currentWeather.temperature,
                                                                              null,
                                                                              null, null, lat, lon,
                                                                              "-1d"),
                                    null)
                        }
                    }
                    if (!context.checkLocationAccess()) {
                        runOnMainThread {
                            BlankActivity.requestPermission(context,
                                                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                                            LawnchairLauncher.REQUEST_PERMISSION_LOCATION_ACCESS) {
                                runOnNewThread {
                                    updateWeather()
                                }
                            }
                        }
                    } else {
                        updateWeather()
                    }
                }
            }
        } catch (e: ForecastProvider.ForecastException) {
            w("updateData: update failed", e)
        }

    }
}

private operator fun Location?.component1(): Double? {
    return this?.latitude
}

private operator fun Location?.component2(): Double? {
    return this?.longitude
}


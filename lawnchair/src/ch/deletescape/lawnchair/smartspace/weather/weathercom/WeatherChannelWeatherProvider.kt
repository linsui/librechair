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

package ch.deletescape.lawnchair.smartspace.weather.weathercom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.location.Criteria
import android.location.LocationManager
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.PeriodicDataProvider
import ch.deletescape.lawnchair.smartspace.WeatherIconProvider
import ch.deletescape.lawnchair.smartspace.weathercom.Constants
import ch.deletescape.lawnchair.smartspace.weathercom.WeatherComRetrofitServiceFactory
import ch.deletescape.lawnchair.util.Temperature
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.Utilities

class WeatherChannelWeatherProvider(controller: LawnchairSmartspaceController) :
        PeriodicDataProvider(controller) {

    private val locationAccess by lazy { context.checkLocationAccess() }

    private val locationManager: LocationManager? by lazy {
        if (locationAccess) {
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        } else null
    }

    @SuppressLint("MissingPermission")
    override fun updateData() {
        runOnNewThread {
            if (context.lawnchairPrefs.weatherCity != "##Auto") {
                try {
                    val position = WeatherComRetrofitServiceFactory.weatherComWeatherRetrofitService
                            .searchLocationByName(context.lawnchairPrefs.weatherCity, "city",
                                                  context.locale.language, "json").execute()

                    d("updateData: position $position")
                    val currentConditions =
                            WeatherComRetrofitServiceFactory.weatherComWeatherRetrofitService.getCurrentConditions(
                                position.body()!!.location.latitude[0],
                                position.body()!!.location.longitude[0]).execute().body()!!
                    val icon: Bitmap
                    if (currentConditions.observation.dayInd == "D") {
                        icon = WeatherIconProvider(context).getIcon(
                            Constants.WeatherComConstants.WEATHER_ICONS_DAY[currentConditions.observation.wxIcon].second)
                    } else {
                        /*
                         There are weird cases when there's no day/night indicator
                         */
                        icon = WeatherIconProvider(context).getIcon(
                            Constants.WeatherComConstants.WEATHER_ICONS_NIGHT[currentConditions.observation.wxIcon].second)
                    }
                    runOnMainThread {
                        updateData(LawnchairSmartspaceController.WeatherData(icon, Temperature(
                            currentConditions.observation.temp, Temperature.Unit.Fahrenheit), null,
                                                                             null, null,
                                                                             position.body()!!.location.latitude[0],
                                                                             position.body()!!.location.longitude[0],
                                                                             if (currentConditions.observation.dayInd == "D") {
                                                                                 Constants.WeatherComConstants.WEATHER_ICONS_DAY[currentConditions.observation.wxIcon]
                                                                                         .second
                                                                             } else {
                                                                                 /*
                                                                                  There are weird cases when there's no day/night indicator, for instance when the location is near the poles
                                                                                  */
                                                                                 Constants.WeatherComConstants.WEATHER_ICONS_NIGHT[currentConditions.observation.wxIcon]
                                                                                         .second
                                                                             }), null)
                    }
                    d("updateData: retrieved current conditions ${currentConditions}")
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            } else {
                if (!locationAccess) {
                    Utilities
                            .requestLocationPermission(context.lawnchairApp.activityHandler.foregroundActivity)
                    return@runOnNewThread
                }
                val locationProvider = locationManager?.getBestProvider(Criteria(), true)
                val location = locationManager?.getLastKnownLocation(locationProvider) ?: return@runOnNewThread
                val currentConditions =
                        WeatherComRetrofitServiceFactory.weatherComWeatherRetrofitService.getCurrentConditions(
                                location.latitude,
                                location.longitude).execute().body()!!
                val icon: Bitmap
                if (currentConditions.observation.dayInd == "D") {
                    icon = WeatherIconProvider(context).getIcon(
                            Constants.WeatherComConstants.WEATHER_ICONS_DAY[currentConditions.observation.wxIcon].second)
                } else {
                    /*
                     There are weird cases when there's no day/night indicator
                     */
                    icon = WeatherIconProvider(context).getIcon(
                            Constants.WeatherComConstants.WEATHER_ICONS_NIGHT[currentConditions.observation.wxIcon].second)
                }
                runOnMainThread {
                    updateData(LawnchairSmartspaceController.WeatherData(icon, Temperature(
                            currentConditions.observation.temp, Temperature.Unit.Fahrenheit), null,
                                                                         null, null,
                                                                         location.latitude,
                                                                         location.longitude,
                                                                         if (currentConditions.observation.dayInd == "D") {
                                                                             Constants.WeatherComConstants.WEATHER_ICONS_DAY[currentConditions.observation.wxIcon]
                                                                                     .second
                                                                         } else {
                                                                             /*
                                                                              There are weird cases when there's no day/night indicator, for instance when the location is near the poles
                                                                              */
                                                                             Constants.WeatherComConstants.WEATHER_ICONS_NIGHT[currentConditions.observation.wxIcon]
                                                                                     .second
                                                                         }), null)
                }
                d("updateData: retrieved current conditions ${currentConditions}")
            }
        }
    }
}

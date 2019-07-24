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

package ch.deletescape.lawnchair.smartspace.weather

import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.locale
import ch.deletescape.lawnchair.runOnMainThread
import ch.deletescape.lawnchair.runOnNewThread
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController
import ch.deletescape.lawnchair.smartspace.weather.icons.WeatherIconManager
import ch.deletescape.lawnchair.util.Temperature
import io.weatherbase.api.model.CurrentObsGroup
import io.weatherbase.api.model.WeatherbitServiceFactory
import io.weatherbit.api.CurrentWeatherDataApi

class WeatherbitDataProvider(controller: LawnchairSmartspaceController) :
        LawnchairSmartspaceController.PeriodicDataProvider(controller) {
    var cachedResponseInternal: CachedResponse<CurrentObsGroup>? = null
    val cachedResponse: CachedResponse<CurrentObsGroup>?
        get() = run {
            if (cachedResponseInternal != null && cachedResponseInternal?.expired?.not() == false) {
                return@run cachedResponseInternal
            } else {
                val response = WeatherbitServiceFactory.getRetrofitService(CurrentWeatherDataApi::class).currentcitycitycountrycountryGet(context.lawnchairPrefs.weatherCity.split(",")[0].trim(), context.lawnchairPrefs.weatherCity.split(",")[1].trim(), null, null, null, context.locale.language, null).execute()
                return@run CachedResponse(System.currentTimeMillis() + (1000 * 60 * 20), response.body()!!).also { cachedResponseInternal = it }
            }
        };

    override fun updateData() {
        runOnNewThread {
            try {
                val response = cachedResponse!!.value.data[0]
                val icon = WeatherIconManager.getInstance(context).getIcon(ICON_IDS[response.weather.icon]!!.first, ICON_IDS[response.weather.icon]!!.second)
                val temperature = Temperature(response.temp.intValueExact(), Temperature.Unit.Celsius)
                runOnMainThread {
                    updateData(LawnchairSmartspaceController.WeatherData(icon, temperature, null, null, null, response.lat.toDouble(), response.lon.toDouble(), response.weather.icon), null)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        super.updateData()
    }

    data class CachedResponse<T>(val expiry: Long, val value: T) {
        val expired: Boolean
            get() = expiry < System.currentTimeMillis()
    }

    companion object {
        val ICON_IDS = mapOf("t01d" to (WeatherIconManager.Icon.THUNDERSTORMS to false),
                             "t01n" to (WeatherIconManager.Icon.THUNDERSTORMS to true),
                             "t02d" to (WeatherIconManager.Icon.THUNDERSTORMS to false),
                             "t02n" to (WeatherIconManager.Icon.THUNDERSTORMS to true),
                             "t03d" to (WeatherIconManager.Icon.THUNDERSTORMS to false),
                             "t03n" to (WeatherIconManager.Icon.THUNDERSTORMS to true),
                             "t04d" to (WeatherIconManager.Icon.THUNDERSTORMS to false),
                             "t04n" to (WeatherIconManager.Icon.THUNDERSTORMS to true),
                             "t05d" to (WeatherIconManager.Icon.HAIL to false),
                             "t05n" to (WeatherIconManager.Icon.HAIL to true),
                             "d01d" to (WeatherIconManager.Icon.SHOWERS to false),
                             "d01n" to (WeatherIconManager.Icon.SHOWERS to true),
                             "d02d" to (WeatherIconManager.Icon.SHOWERS to false),
                             "d02n" to (WeatherIconManager.Icon.SHOWERS to true),
                             "d03d" to (WeatherIconManager.Icon.SHOWERS to false),
                             "d03n" to (WeatherIconManager.Icon.SHOWERS to true),
                             "r01d" to (WeatherIconManager.Icon.RAIN to false),
                             "r01n" to (WeatherIconManager.Icon.RAIN to true),
                             "r02d" to (WeatherIconManager.Icon.RAIN to false),
                             "r02n" to (WeatherIconManager.Icon.RAIN to true),
                             "r03d" to (WeatherIconManager.Icon.RAIN to false),
                             "r03n" to (WeatherIconManager.Icon.RAIN to true),
                             "f01d" to (WeatherIconManager.Icon.FREEZING_RAIN to false),
                             "f01n" to (WeatherIconManager.Icon.FREEZING_RAIN to true),
                             "f02d" to (WeatherIconManager.Icon.FREEZING_RAIN to false),
                             "f02n" to (WeatherIconManager.Icon.FREEZING_RAIN to true),
                             "f03d" to (WeatherIconManager.Icon.FREEZING_RAIN to false),
                             "f03n" to (WeatherIconManager.Icon.FREEZING_RAIN to true),
                             "r04d" to (WeatherIconManager.Icon.SHOWERS to false),
                             "r04n" to (WeatherIconManager.Icon.SHOWERS to true),
                             "r05d" to (WeatherIconManager.Icon.SHOWERS to false),
                             "r05n" to (WeatherIconManager.Icon.SHOWERS to true),
                             "s01d" to (WeatherIconManager.Icon.SNOW to false),
                             "s01n" to (WeatherIconManager.Icon.SNOW to true),
                             "s02d" to (WeatherIconManager.Icon.SNOW to false),
                             "s02n" to (WeatherIconManager.Icon.SNOW to true),
                             "s03d" to (WeatherIconManager.Icon.SNOWSTORM to false),
                             "s03n" to (WeatherIconManager.Icon.SNOWSTORM to true),
                             "s04d" to (WeatherIconManager.Icon.RAIN_AND_SNOW to false),
                             "s04n" to (WeatherIconManager.Icon.RAIN_AND_SNOW to true),
                             "s05d" to (WeatherIconManager.Icon.SLEET to false),
                             "s05n" to (WeatherIconManager.Icon.SLEET to true),
                             "s06d" to (WeatherIconManager.Icon.FLURRIES to false),
                             "s06n" to (WeatherIconManager.Icon.FLURRIES to true),
                             "a01d" to (WeatherIconManager.Icon.FOG to false),
                             "a01n" to (WeatherIconManager.Icon.FOG to true),
                             "a02d" to (WeatherIconManager.Icon.DUST to false),
                             "a02n" to (WeatherIconManager.Icon.DUST to true),
                             "a03d" to (WeatherIconManager.Icon.HAZY to false),
                             "a03n" to (WeatherIconManager.Icon.HAZY to true),
                             "a04d" to (WeatherIconManager.Icon.DUST to false),
                             "a04n" to (WeatherIconManager.Icon.DUST to true),
                             "a05d" to (WeatherIconManager.Icon.FOG to false),
                             "a05n" to (WeatherIconManager.Icon.FOG to true),
                             "a06d" to (WeatherIconManager.Icon.FOG to false),
                             "a06n" to (WeatherIconManager.Icon.FOG to true),
                             "c01d" to (WeatherIconManager.Icon.CLEAR to false),
                             "c01n" to (WeatherIconManager.Icon.CLEAR to true),
                             "c02d" to (WeatherIconManager.Icon.INTERMITTENT_CLOUDS to false),
                             "c02n" to (WeatherIconManager.Icon.INTERMITTENT_CLOUDS to true),
                             "c03d" to (WeatherIconManager.Icon.PARTLY_CLOUDY to false),
                             "c03n" to (WeatherIconManager.Icon.PARTLY_CLOUDY to true),
                             "c04d" to (WeatherIconManager.Icon.MOSTLY_CLOUDY to false),
                             "c04d" to (WeatherIconManager.Icon.MOSTLY_CLOUDY to true),
                             "c05d" to (WeatherIconManager.Icon.OVERCAST to false),
                             "c05n" to (WeatherIconManager.Icon.OVERCAST to true),
                             "u00d" to (WeatherIconManager.Icon.NA to false),
                             "u00n" to (WeatherIconManager.Icon.NA to true))
        val COND_IDS = mapOf("t01d" to (210 to false),
                             "t01n" to (210 to true),
                             "t02d" to (210 to false),
                             "t02n" to (210 to true),
                             "t03d" to (210 to false),
                             "t03n" to (210 to true),
                             "t04d" to (210 to false),
                             "t04n" to (210 to true),
                             "t05d" to (613 to false),
                             "t05n" to (613 to true),
                             "d01d" to (521 to false),
                             "d01n" to (521 to true),
                             "d02d" to (521 to false),
                             "d02n" to (521 to true),
                             "d03d" to (521 to false),
                             "d03n" to (521 to true),
                             "r01d" to (501 to false),
                             "r01n" to (501 to true),
                             "r02d" to (501 to false),
                             "r02n" to (501 to true),
                             "r03d" to (501 to false),
                             "r03n" to (501 to true),
                             "f01d" to (511 to false),
                             "f01n" to (511 to true),
                             "f02d" to (511 to false),
                             "f02n" to (511 to true),
                             "f03d" to (511 to false),
                             "f03n" to (511 to true),
                             "r04d" to (521 to false),
                             "r04n" to (521 to true),
                             "r05d" to (521 to false),
                             "r05n" to (521 to true),
                             "s01d" to (511 to false),
                             "s01n" to (511 to true),
                             "s02d" to (511 to false),
                             "s02n" to (511 to true),
                             "s03d" to (502 to false),
                             "s03n" to (502 to true),
                             "s04d" to (501 to false),
                             "s04n" to (501 to true),
                             "s05d" to (611 to false),
                             "s05n" to (611 to true),
                             "s06d" to (601 to false),
                             "s06n" to (601 to true),
                             "a01d" to (741 to false),
                             "a01n" to (741 to true),
                             "a02d" to (761 to false),
                             "a02n" to (761 to true),
                             "a03d" to (721 to false),
                             "a03n" to (721 to true),
                             "a04d" to (761 to false),
                             "a04n" to (761 to true),
                             "a05d" to (741 to false),
                             "a05n" to (741 to true),
                             "a06d" to (741 to false),
                             "a06n" to (741 to true),
                             "c01d" to (800 to false),
                             "c01n" to (800 to true),
                             "c02d" to (802 to false),
                             "c02n" to (802 to true),
                             "c03d" to (803 to false),
                             "c03n" to (803 to true),
                             "c04d" to (803 to false),
                             "c04d" to (803 to true),
                             "c05d" to (804 to false),
                             "c05n" to (804 to true),
                             "u00d" to (0 to false),
                             "u00n" to (0 to true))
    }
}
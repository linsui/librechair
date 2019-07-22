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

package ch.deletescape.lawnchair.smartspace.weather.forecast

import android.content.Context
import ch.deletescape.lawnchair.locale
import ch.deletescape.lawnchair.newList
import ch.deletescape.lawnchair.smartspace.AccuWeatherDataProvider
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController
import ch.deletescape.lawnchair.smartspace.accu.AccuRetrofitServiceFactory
import ch.deletescape.lawnchair.smartspace.accu.model.AccuDailyForecastsGSon
import ch.deletescape.lawnchair.smartspace.accu.model.AccuHourlyForecastGSon
import ch.deletescape.lawnchair.util.Temperature
import ch.deletescape.lawnchair.util.extensions.d
import com.google.gson.Gson
import retrofit2.Response
import java.time.Instant
import java.util.*
import kotlin.math.roundToInt

class AccuWeatherForecastProvider(val c: Context) : ForecastProvider {
    var cachedHourly: CachedResponse<Response<List<AccuHourlyForecastGSon>>>? = null
    var cachedDaily: CachedResponse<Response<AccuDailyForecastsGSon>>? = null
    var cachedCurrent: CachedResponse<ForecastProvider.CurrentWeather>? = null

    override fun getHourlyForecast(lat: Double, lon: Double): ForecastProvider.Forecast {
        synchronized(this) {
            try {
                d("getHourlyForecast: $lat, $lon")
                val responseResult: Response<List<AccuHourlyForecastGSon>>?
                d("getHourlyForecast: retrieving geolocation")
                try {
                    if (cachedHourly == null || cachedHourly?.expired == true) {
                        d("getHourlyForecast: re-retrieving geolocation")
                        val geolocationResponse =
                                AccuRetrofitServiceFactory.accuSearchRetrofitService
                                        .getGeoPosition("$lat,$lon", c.locale.language).execute()
                        if (!geolocationResponse.isSuccessful) {
                            d("getHourlyForecast: geolocation not successful")
                            throw ForecastProvider.ForecastException(
                                "geolocation couldn't be retrieved")
                        } else {
                            d("getHourlyForecast: retrieving AccuWeather forecasts for location ${geolocationResponse.body()?.key}")
                            responseResult = AccuRetrofitServiceFactory.accuWeatherRetrofitService
                                    .getHourly(geolocationResponse.body()!!.key, c.locale.language)
                                    .execute()
                            cachedHourly =
                                    CachedResponse(System.currentTimeMillis() + (1000 * 60 * 10),
                                                   responseResult!!)
                        }
                    } else {
                        responseResult = cachedHourly?.value
                    }
                } catch (e: Throwable) {
                    throw ForecastProvider.ForecastException(e)
                }

                d("getHourlyForecast: accuWeather response retrieved")

                if (responseResult?.isSuccessful != true) {
                    throw ForecastProvider.ForecastException(
                        responseResult?.message() ?: "unknown error")
                } else {
                    val data: MutableList<ForecastProvider.ForecastData> = newList()
                    d("getHourlyForecast: converting AccuWeather data into OWM format")
                    responseResult.body()!!.forEach {
                        d("getHourlyForecast: converting AccuWeather data ${Gson().toJson(
                            it)} into OWM format")
                        val icon = AccuWeatherDataProvider.getIcon(c, it.weatherIcon, it.isDaylight)
                        val temperature = Temperature(
                            java.lang.Float.valueOf(it.temperature.value).roundToInt(),
                            Temperature.Unit.Celsius)
                        val iconRes = (it.weatherIcon.toString() + if (it.isDaylight) "d" else "n")
                        val date = Date.from(Instant.ofEpochSecond(it.epochDateTime))
                        val conds = arrayOf(COND_MAP[it.weatherIcon]!!)

                        d("getHourlyForecast: converted AccuWeather data into OWM format $icon, $temperature, $iconRes, $date, $conds")

                        data += ForecastProvider.ForecastData(
                            LawnchairSmartspaceController.WeatherData(icon, temperature, null, null,
                                                                      null, lat, lon, iconRes),
                            date, conds)
                    }
                    return ForecastProvider.Forecast(data)
                }
            } catch (e: NullPointerException) {
                throw ForecastProvider.ForecastException(e)
            }
        }
    }

    override fun getCurrentWeather(lat: Double, lon: Double): ForecastProvider.CurrentWeather {
        synchronized(CURRENT_LOCK) {
            if (cachedCurrent != null && !cachedCurrent!!.expired) {
                return cachedCurrent!!.value
            } else {
                try {
                    val locationInfo = AccuRetrofitServiceFactory.accuSearchRetrofitService
                            .getGeoPosition("$lat,$lon", c.locale.language).execute()
                    val weatherResponse = AccuRetrofitServiceFactory.accuWeatherRetrofitService
                            .getLocalWeather(locationInfo.body()!!.key, c.locale.language)
                            .execute()
                    cachedCurrent = CachedResponse(System.currentTimeMillis() + 2, ForecastProvider.CurrentWeather(
                        arrayOf(COND_MAP[weatherResponse.body()!!.currentConditions.weatherIcon]!!),
                        Date.from(Instant.ofEpochSecond(
                            weatherResponse.body()!!.currentConditions.epochTime)), Temperature(
                            weatherResponse.body()!!.currentConditions.temperature.value.toDouble().toInt(),
                            Temperature.Unit.Celsius)))
                    return cachedCurrent!!.value
                } catch (e: Throwable) {
                    throw ForecastProvider.ForecastException(e)
                }
            }
        }
    }

    override fun getDailyForecast(lat: Double, lon: Double): ForecastProvider.DailyForecast {
        synchronized(DAILY_LOCK) {
            d("getDailyForecast: $lat, $lon")
            val responseResult: Response<AccuDailyForecastsGSon>?

            try {
                if (cachedDaily == null || cachedDaily?.expired == true) {

                    d("getDailyForecast: re-retrieving geolocation")
                    val geolocationResponse = AccuRetrofitServiceFactory.accuSearchRetrofitService
                            .getGeoPosition("$lat,$lon", c.locale.language).execute()
                    if (!geolocationResponse.isSuccessful) {
                        d("getDailyForecast: geolocation not successful")
                        throw ForecastProvider.ForecastException(
                            "geolocation couldn't be retrieved")
                    } else {
                        d("getDailyForecast: retrieving AccuWeather forecasts for location ${geolocationResponse.body()?.key}")
                        responseResult = AccuRetrofitServiceFactory.accuWeatherRetrofitService
                                .getDaily10Day(geolocationResponse.body()!!.key, c.locale.language)
                                .execute()
                        cachedDaily = CachedResponse(System.currentTimeMillis() + (1000 * 60 * 10),
                                                     responseResult!!)
                    }
                } else {
                    responseResult = cachedDaily?.value
                }
            } catch (e: Throwable) {
                throw ForecastProvider.ForecastException(e)
            }
            try {
                if (responseResult == null || !responseResult.isSuccessful) {
                    throw ForecastProvider.ForecastException(
                        "responseResult is null or request failed: $responseResult")
                } else {
                    val data: MutableList<ForecastProvider.DailyForecastData> = newList()
                    responseResult.body()!!.dailyForecasts!!.forEach {
                        data += ForecastProvider.DailyForecastData(
                            Temperature(it.temperature.maximum.value.toDouble().toInt(),
                                        Temperature.Unit.Celsius),
                            Temperature(it.temperature.minimum.value.toDouble().toInt(),
                                        Temperature.Unit.Celsius),
                            Date.from(Instant.ofEpochSecond(it.epochDate)),
                            AccuWeatherDataProvider.getIcon(c, it.day.icon, true), null)
                    }
                    return ForecastProvider.DailyForecast(data)
                }
            } catch (e: java.lang.NullPointerException) {
                throw ForecastProvider.ForecastException(e)
            }
        }
    }


    companion object {
        val COND_MAP = mapOf(1 to 800, 2 to 801, 3 to 805, 4 to 802, 5 to 900, 6 to 810, 7 to 802,
                             8 to 804, 11 to 721, 12 to 510, 13 to 509, 14 to 504, 15 to 211,
                             16 to 209, 17 to 218, 18 to 501, 19 to 510, 20 to 500, 21 to 500,
                             22 to 601, 23 to 600, 24 to 1104, 25 to 611, 26 to 500, 29 to 600,
                             32 to 1200, 33 to 800, 34 to 801, 35 to 804, 36 to 801, 37 to 721,
                             38 to 803, 39 to 500, 40 to 500, 41 to 200, 42 to 200, 43 to 612,
                             44 to 600, 99 to 0)
        @JvmStatic val DAILY_LOCK = Any() /* Stuff might lock against this from Java */
        @JvmStatic val CURRENT_LOCK = Any()
    }

    data class CachedResponse<T>(val expiry: Long, val value: T) {
        val expired: Boolean
            get() = expiry < System.currentTimeMillis()
    }
}
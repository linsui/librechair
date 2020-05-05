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
import retrofit2.Response
import java.io.IOException
import java.time.Instant
import java.util.*
import kotlin.math.roundToInt

class AccuWeatherForecastProvider(val c: Context) : ForecastProvider {
    var cachedHourly: CachedResponse<Response<List<AccuHourlyForecastGSon>>>? = null
    var cachedDaily: CachedResponse<Response<AccuDailyForecastsGSon>>? = null
    var cachedCurrent: CachedResponse<ForecastProvider.CurrentWeather>? = null

    override fun getGeolocation(query: String): Pair<Double, Double> {
        try {
            val response = AccuRetrofitServiceFactory.getAccuSearchRetrofitService(c)
                    .search(query, c.locale.language).execute()
            if (!response.isSuccessful || response.body()?.isEmpty() == true) {
                throw ForecastProvider.ForecastException(
                        "request not successful or no location found")
            } else {
                return (response.body()!![0].geoPosition.latitude.toDoubleOrNull()
                        ?: 0.toDouble()) to (response.body()!![0].geoPosition.longitude.toDoubleOrNull()
                        ?: 0.toDouble())
            }
        } catch (e: IOException) {
            throw ForecastProvider.ForecastException(e)
        }
    }

    override fun getHourlyForecast(lat: Double, lon: Double): ForecastProvider.Forecast {
        synchronized(this) {
            try {
                val responseResult: Response<List<AccuHourlyForecastGSon>>?
                try {
                    if (cachedHourly == null || cachedHourly?.expired == true) {
                        val geolocationResponse =
                                AccuRetrofitServiceFactory.getAccuSearchRetrofitService(c)
                                        .getGeoPosition("$lat,$lon", c.locale.language).execute()
                        if (!geolocationResponse.isSuccessful) {
                            throw ForecastProvider.ForecastException(
                                    "geolocation couldn't be retrieved")
                        } else {
                            responseResult = AccuRetrofitServiceFactory.getAccuWeatherRetrofitService(c)
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

                if (responseResult?.isSuccessful != true) {
                    throw ForecastProvider.ForecastException(
                            responseResult?.message() ?: "unknown error")
                } else {
                    val data: MutableList<ForecastProvider.ForecastData> = newList()

                    responseResult.body()!!.forEach {
                        val icon = AccuWeatherDataProvider.getIcon(c, it.weatherIcon, it.isDaylight)
                        val temperature = Temperature(
                                java.lang.Float.valueOf(it.temperature.value).roundToInt(),
                                Temperature.Unit.Celsius)
                        val iconRes = (it.weatherIcon.toString() + if (it.isDaylight) "d" else "n")
                        val date = Date.from(Instant.ofEpochSecond(it.epochDateTime))
                        val conds = arrayOf(COND_MAP[it.weatherIcon]!!)



                        data += ForecastProvider.ForecastData(
                                LawnchairSmartspaceController.WeatherData(icon, temperature, null,
                                        null, null, lat, lon,
                                        iconRes), date, conds)
                    }
                    return ForecastProvider.Forecast(data, responseResult.body()!![0].mobileLink)
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
                    val locationInfo = AccuRetrofitServiceFactory.getAccuSearchRetrofitService(c)
                            .getGeoPosition("$lat,$lon", c.locale.language).execute()
                    val weatherResponse = AccuRetrofitServiceFactory.getAccuWeatherRetrofitService(c)
                            .getLocalWeather(locationInfo.body()!!.key, c.locale.language).execute()
                    cachedCurrent = CachedResponse(System.currentTimeMillis() + 2,
                            ForecastProvider.CurrentWeather(
                                    arrayOf(COND_MAP[weatherResponse.body()!!.currentConditions.weatherIcon]!!),
                                    Date.from(Instant.ofEpochSecond(
                                            weatherResponse.body()!!.currentConditions.epochTime)),
                                    Temperature(
                                            weatherResponse.body()!!.currentConditions.temperature.value.toDouble().toInt(),
                                            Temperature.Unit.Celsius),
                                    AccuWeatherDataProvider.getIcon(c,
                                            weatherResponse.body()!!.currentConditions.weatherIcon,
                                            weatherResponse.body()!!.currentConditions.isDayTime),
                                    weatherResponse.body()!!.currentConditions.precip1hr.value.toDouble(), weatherResponse.body()!!.currentConditions.mobileLink))
                    return cachedCurrent!!.value
                } catch (e: Throwable) {
                    throw ForecastProvider.ForecastException(e)
                }
            }
        }
    }

    override fun getDailyForecast(lat: Double, lon: Double): ForecastProvider.DailyForecast {
        synchronized(DAILY_LOCK) {

            val responseResult: Response<AccuDailyForecastsGSon>?

            try {
                if (cachedDaily == null || cachedDaily?.expired == true) {


                    val geolocationResponse = AccuRetrofitServiceFactory.getAccuSearchRetrofitService(c)
                            .getGeoPosition("$lat,$lon", c.locale.language).execute()
                    if (!geolocationResponse.isSuccessful) {

                        throw ForecastProvider.ForecastException(
                                "geolocation couldn't be retrieved")
                    } else {

                        responseResult = AccuRetrofitServiceFactory.getAccuWeatherRetrofitService(c)
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
        @JvmStatic
        val DAILY_LOCK = Any() /* Stuff might lock against this from Java */
        @JvmStatic
        val CURRENT_LOCK = Any()
    }

    data class CachedResponse<T>(val expiry: Long, val value: T) {
        val expired: Boolean
            get() = expiry < System.currentTimeMillis()
    }
}
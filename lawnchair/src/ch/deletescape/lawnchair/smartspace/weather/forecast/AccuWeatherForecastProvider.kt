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
import ch.deletescape.lawnchair.runOnNewThread
import ch.deletescape.lawnchair.runOnUiWorkerThread
import ch.deletescape.lawnchair.smartspace.AccuWeatherDataProvider
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController
import ch.deletescape.lawnchair.smartspace.accu.AccuRetrofitServiceFactory
import ch.deletescape.lawnchair.smartspace.accu.model.AccuHourlyForecastGSon
import ch.deletescape.lawnchair.smartspace.accu.model.sub.AccuLocationGSon
import ch.deletescape.lawnchair.util.Temperature
import ch.deletescape.lawnchair.util.extensions.d
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.util.*

class AccuWeatherForecastProvider(val c: Context) : ForecastProvider {
    var cachedResponse: CachedResponse<Response<List<AccuHourlyForecastGSon>>>? = null


    override fun getHourlyForecast(lat: Double, lon: Double): ForecastProvider.Forecast {
        synchronized(AccuWeatherForecastProvider::class) {
            d("getHourlyForecast: $lat, $lon")
            var flag = false
            var error: Throwable? = null
            var responseResult: Response<List<AccuHourlyForecastGSon>>? = null
            d("getHourlyForecast: retrieving geolocation")
            runOnUiWorkerThread {
                if (cachedResponse == null || cachedResponse?.expired == true) {
                    d("getHourlyForecast: re-retrieving geolocation")
                    AccuRetrofitServiceFactory.accuSearchRetrofitService
                            .getGeoPosition("$lat,$lon", c.locale.language)
                            .enqueue(object : Callback<AccuLocationGSon> {
                                override fun onFailure(call: Call<AccuLocationGSon>, t: Throwable) {
                                    d("getHourlyForecast: geolocation failed", t)
                                    flag = true
                                    error = t
                                }

                                override fun onResponse(call: Call<AccuLocationGSon>,
                                                        response: Response<AccuLocationGSon>) {
                                    if (!response.isSuccessful) {
                                        d("getHourlyForecast: geolocation not successful")
                                        flag = true
                                        error = Exception(response.message())
                                    } else {
                                        d("getHourlyForecast: retrieving AccuWeather forecast for location ${response.body()?.key}")
                                        runOnNewThread {
                                            AccuRetrofitServiceFactory.accuWeatherRetrofitService
                                                    .getHourly(response.body()!!.key,
                                                               c.locale.language).enqueue(object :
                                                                                                  Callback<List<AccuHourlyForecastGSon>> {
                                                        override fun onResponse(
                                                            call: Call<List<AccuHourlyForecastGSon>>,
                                                            response: Response<List<AccuHourlyForecastGSon>>) {
                                                            if (response.isSuccessful) {
                                                                d("onResponse: retrieved forecast $response")
                                                                flag = true
                                                                responseResult = response
                                                                cachedResponse = CachedResponse(
                                                                    System.currentTimeMillis() + (1000 * 60 * 10),
                                                                    response)
                                                            } else {
                                                                onFailure(call, Throwable(
                                                                    "response was not successful"))
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: Call<List<AccuHourlyForecastGSon>>,
                                                            t: Throwable) {
                                                            d("onFailure: API call failed", t)
                                                            flag = true
                                                            error = t
                                                        }

                                                    })
                                        }
                                    }
                                }
                            })
                } else {
                    d("response ${cachedResponse?.value} was cached")
                    flag = true
                    responseResult = cachedResponse!!.value
                }
            }

            while (!flag) {
            }

            d("")

            if (error != null || !responseResult!!.isSuccessful) {
                throw ForecastProvider.ForecastException(
                    if (error != null) error!! else java.lang.Exception())
            } else {
                val data: MutableList<ForecastProvider.ForecastData> =
                        newList<ForecastProvider.ForecastData>().toMutableList()

                responseResult!!.body()!!.forEach {
                    data += ForecastProvider.ForecastData(LawnchairSmartspaceController.WeatherData(
                        AccuWeatherDataProvider.getIcon(c, it.weatherIcon, it.isDaylight),
                        Temperature(it.temperature.value.toInt(), Temperature.Unit.Celsius), null,
                        null, null, 0.toDouble(), 0.toDouble(),
                        (it.weatherIcon.toString() + if (it.isDaylight) "d" else "n")), Date.from(
                        Instant.ofEpochSecond(it.epochDateTime)),
                                                          arrayOf(COND_MAP[it.weatherIcon]!!))
                }
                return ForecastProvider.Forecast(data)
            }
        }
    }

    override fun getDailyForecast(lat: Double, lon: Double): ForecastProvider.DailyForecast {
        throw ForecastProvider.ForecastException(Exception("Daily forecast not yet implemented"))
    }

    companion object {
        val COND_MAP = mapOf(1 to 800, 2 to 801, 3 to 805, 4 to 802, 5 to 900, 6 to 810, 7 to 802,
                             8 to 804, 11 to 721, 12 to 510, 13 to 509, 14 to 504, 15 to 211,
                             16 to 209, 17 to 218, 18 to 501, 19 to 510, 20 to 500, 21 to 500,
                             22 to 601, 23 to 600, 24 to 1104, 25 to 611, 26 to 500, 29 to 600,
                             32 to 1200, 33 to 800, 34 to 801, 35 to 804, 36 to 801, 37 to 721,
                             38 to 803, 39 to 500, 40 to 500, 41 to 200, 42 to 200, 43 to 612,
                             44 to 600, 99 to 0)
    }

    data class CachedResponse<T>(val expiry: Long, val value: T) {
        val expired: Boolean
            get() = expiry < System.currentTimeMillis()
    }
}
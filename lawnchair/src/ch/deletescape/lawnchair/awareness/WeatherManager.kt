/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("unused")

package ch.deletescape.lawnchair.awareness

import android.annotation.AnyThread
import android.annotation.MainThread
import android.annotation.WorkerThread
import android.app.Application
import android.content.Context
import ch.deletescape.lawnchair.forecastProvider
import ch.deletescape.lawnchair.lawnchairApp
import ch.deletescape.lawnchair.lawnchairLocationManager
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider
import ch.deletescape.lawnchair.util.extensions.d
import ch.deletescape.lawnchair.util.extensions.w
import java.util.concurrent.TimeUnit

object WeatherManager {
    var currentForecast: ForecastProvider.Forecast? = null
        set(value) {
            field = value
            if (value != null) {
                hourlyForecastCallbacks.forEach { it(value) }
            }
        }
    var currentWeather: ForecastProvider.CurrentWeather? = null
        set(value) {
            field = value
            if (value != null) {
                weatherCallbacks.forEach { it(value) }
            }
        }
    var currentDailyForecast: ForecastProvider.DailyForecast? = null
        set(value) {
            field = value
            if (value != null) {
                dailyForecastCallbacks.forEach { it(value) }
            }
        }
    var currentGeo: Pair<Double, Double>? = null
        set(value) {
            field = value
            if (value != null) {
                geoCallbacks.forEach { it(value) }
            }
        }


    private lateinit var app: Application
    private val weatherCallbacks =
            mutableListOf<(weather: ForecastProvider.CurrentWeather) -> Unit>()
    private val hourlyForecastCallbacks =
            mutableListOf<(forecast: ForecastProvider.Forecast) -> Unit>()
    private val dailyForecastCallbacks =
            mutableListOf<(forecast: ForecastProvider.DailyForecast) -> Unit>()
    private val geoCallbacks =
            mutableListOf<(geo: Pair<Double, Double>) -> Unit>()

    @MainThread
    fun subscribeWeather(@AnyThread cb: (weather: ForecastProvider.CurrentWeather) -> Unit) {
        val tcb = currentWeather
        if (tcb != null) {
            cb(tcb)
        }
        weatherCallbacks += cb
    }

    @MainThread
    fun subscribeGeo(@AnyThread cb: (weather: Pair<Double, Double>) -> Unit) {
        val tcb = currentGeo
        if (tcb != null) {
            cb(tcb)
        }
        geoCallbacks += cb
    }

    @MainThread
    fun subscribeHourly(@AnyThread cb: (forecast: ForecastProvider.Forecast) -> Unit) {
        val tcb = currentForecast
        if (tcb != null) {
            cb(tcb)
        }
        hourlyForecastCallbacks += cb
    }


    @MainThread
    fun subscribeDaily(@AnyThread cb: (forecast: ForecastProvider.DailyForecast) -> Unit) {
        val tcb = currentDailyForecast
        if (tcb != null) {
            cb(tcb)
        }
        dailyForecastCallbacks += cb
    }

    @MainThread
    fun attachToApplication(app: Application) {
        this.app = app
        val refresh = object : Runnable {
            override fun run() {
                try {
                    d("run: refreshing forecast")
                    currentGeo = resolveGeolocation(app)
                    val (lat, lon) = currentGeo ?: null to null
                    app.lawnchairApp.weatherLooper.post {
                        if (lat != null && lon != null) {
                            try {
                                currentWeather = app.forecastProvider.getCurrentWeather(lat, lon)
                            } catch (e: ForecastProvider.ForecastException) {
                                w("run: failed to retrieve forecast", e)
                            } catch (e: RuntimeException) {
                                ch.deletescape.lawnchair.util.extensions.e(
                                        "run: failed to retrieve forecast", e)
                            }
                        }
                    }
                    app.lawnchairApp.weatherLooper.post {
                        if (lat != null && lon != null) {
                            try {
                                currentDailyForecast = app.forecastProvider.getDailyForecast(lat, lon)
                            } catch (e: ForecastProvider.ForecastException) {
                                w("run: failed to retrieve forecast", e)
                            } catch (e: RuntimeException) {
                                ch.deletescape.lawnchair.util.extensions.e(
                                        "run: failed to retrieve forecast", e)
                            }
                        }
                    }
                    app.lawnchairApp.weatherLooper.post {
                        if (lat != null && lon != null) {
                            try {
                                currentForecast = app.forecastProvider.getHourlyForecast(lat, lon)
                            } catch (e: ForecastProvider.ForecastException) {
                                w("run: failed to retrieve forecast", e)
                            } catch (e: RuntimeException) {
                                ch.deletescape.lawnchair.util.extensions.e(
                                        "run: failed to retrieve forecast", e)
                            }
                        }
                    }
                } catch (e: ForecastProvider.ForecastException) {
                    w("run: failed to retrieve forecast", e)
                } catch (e: RuntimeException) {
                    ch.deletescape.lawnchair.util.extensions.e(
                            "run: failed to retrieve forecast", e)
                }

                app.lawnchairApp.weatherLooper.postDelayed(this, TimeUnit.MINUTES.toMillis(10))
            }
        }
        app.lawnchairApp.weatherLooper.post(refresh)
    }

    @WorkerThread
    fun resolveGeolocation(ctx: Context) =
            if (ctx.lawnchairPrefs.weatherCity != "##Auto") ctx.forecastProvider.getGeolocation(
                    ctx.lawnchairPrefs.weatherCity) else ctx.lawnchairLocationManager.location
}
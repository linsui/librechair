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

/*
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

package ch.deletescape.lawnchair.smartspace.weathercom

import android.content.Context
import ch.deletescape.lawnchair.util.okhttp.OkHttpClientBuilder
import com.android.launcher3.LauncherAppState
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object WeatherComRetrofitServiceFactory {
    private var API_KEY: Pair<String, String> = Pair("apiKey", Constants.WeatherComConstants.WEATHER_COM_API_KEY)
    private var API_KEY_FORECAST: Pair<String, String> = Pair("apiKey", Constants.WeatherComConstants.WEATHER_COM_FORECAST_KEY)
    private val BASE_URL = "https://api.weather.com"
    private var okHttpClient: OkHttpClient? = null
    private var okHttpClientForForecast: OkHttpClient? = null

    fun getRetrofitService(c: Context): WeatherComWeatherRetrofitService {
        return getRetrofitService(WeatherComWeatherRetrofitService::class.java, c)
    }

    fun getRetrofitServiceForecast(c: Context): WeatherComWeatherRetrofitService {
        return getRetrofitServiceForForecast(WeatherComWeatherRetrofitService::class.java, c)
    }

    private fun <T> getRetrofitService(serviceClass: Class<T>, c: Context): T {
        val client = buildOkHttpClient(false, c)
        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(client).build().create(serviceClass)
    }

    private fun <T> getRetrofitServiceForForecast(serviceClass: Class<T>, c: Context): T {
        val client = buildOkHttpClient(true, c)
        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(client).build().create(serviceClass)
    }

    private fun buildOkHttpClient(forForecast: Boolean = false, c: Context): OkHttpClient? {
        if (!forForecast) {
            if (okHttpClient == null) {
                synchronized(WeatherComRetrofitServiceFactory::class.java) {
                    if (okHttpClient == null) {
                        okHttpClient = OkHttpClientBuilder().addQueryParam(API_KEY).build(c)
                    }
                }
            }
        } else {
            if (okHttpClientForForecast == null) {
                synchronized(WeatherComRetrofitServiceFactory::class.java) {
                    if (okHttpClientForForecast == null) {
                        okHttpClientForForecast = OkHttpClientBuilder().addQueryParam(API_KEY_FORECAST).build(c)
                    }
                }
            }
        }
        return if (forForecast) okHttpClientForForecast else okHttpClient
    }
}
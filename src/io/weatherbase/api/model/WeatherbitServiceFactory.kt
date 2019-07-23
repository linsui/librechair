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

package io.weatherbase.api.model

import android.text.TextUtils
import ch.deletescape.lawnchair.smartspace.accu.AccuRetrofitServiceFactory
import ch.deletescape.lawnchair.smartspace.accu.AccuSearchRetrofitService
import ch.deletescape.lawnchair.smartspace.accu.AccuWeatherRetrofitService
import ch.deletescape.lawnchair.util.okhttp.OkHttpClientBuilder
import com.android.launcher3.LauncherAppState
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherbitServiceFactory {
    private var API_KEY: Pair<String, String> = "key" to "570a97170b604eebb9630a679b7234e8"
    private val WEATHERBIT_BASE_URL = "https://api.weatherbit.io/v2.0"
    private var okHttpClient: OkHttpClient? = null


    private fun <T> getRetrofitService(serviceClass: Class<T>): T {
        val client = buildOkHttpClient()
        return Retrofit.Builder().baseUrl(WEATHERBIT_BASE_URL).addConverterFactory(
                GsonConverterFactory.create()).client(client).build().create(serviceClass)
    }

    private fun buildOkHttpClient(): OkHttpClient? {
        if (okHttpClient == null) {
            synchronized(AccuRetrofitServiceFactory::class.java) {
                if (okHttpClient == null) {
                    okHttpClient = OkHttpClientBuilder()
                            .addQueryParam(API_KEY).build(
                                    LauncherAppState.getInstanceNoCreate()?.context)
                }
            }
        }
        return okHttpClient
    }
}
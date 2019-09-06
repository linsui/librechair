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

package ch.deletescape.lawnchair.smartspace.accu

import android.content.Context
import android.text.TextUtils
import ch.deletescape.lawnchair.util.okhttp.OkHttpClientBuilder
import com.android.launcher3.BuildConfig
import com.android.launcher3.LauncherAppState
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object AccuRetrofitServiceFactory {
    private var ACCU_APIKEY: Pair<String, String> = Pair("apikey", BuildConfig.ACCUWEATHER_KEY)
    private val ACCU_BASE_URL = "https://api.accuweather.com"
    private val ACCU_DETAILS = Pair("details", "true")
    private val ACCU_GETPHOTOS = Pair("getPhotos", "true")
    private val ACCU_METRIC = Pair("metric", "true")
    private var okHttpClient: OkHttpClient? = null

    fun getAccuWeatherRetrofitService(c: Context): AccuWeatherRetrofitService {
        return getRetrofitService(AccuWeatherRetrofitService::class.java, c)
    }

    fun getAccuSearchRetrofitService(c: Context): AccuSearchRetrofitService {
        return getRetrofitService(AccuSearchRetrofitService::class.java, c)
    }

    private fun <T> getRetrofitService(serviceClass: Class<T>, c: Context): T {
        val client = buildOkHttpClient(c)
        return Retrofit.Builder().baseUrl(ACCU_BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(client).build().create(serviceClass)
    }

    private fun buildOkHttpClient(c: Context): OkHttpClient? {
        if (okHttpClient == null) {
            synchronized(AccuRetrofitServiceFactory::class.java) {
                if (okHttpClient == null) {
                    okHttpClient = OkHttpClientBuilder().addQueryParam(ACCU_APIKEY).addQueryParam(ACCU_DETAILS).addQueryParam(ACCU_METRIC).build(c)
                }
            }
        }
        return okHttpClient
    }
}
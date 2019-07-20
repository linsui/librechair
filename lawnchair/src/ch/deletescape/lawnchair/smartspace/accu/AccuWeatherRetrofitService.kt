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

import ch.deletescape.lawnchair.smartspace.accu.model.AccuDailyForecastsGSon
import ch.deletescape.lawnchair.smartspace.accu.model.AccuHourlyForecastGSon
import ch.deletescape.lawnchair.smartspace.accu.model.AccuLocalWeatherGSon
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface AccuWeatherRetrofitService {
    @GET("forecasts/v1/hourly/24hour/{key}")
    fun getHourly(@Path("key") key: String, @Query("language") language: String): Call<List<AccuHourlyForecastGSon>>

    @GET("forecasts/v1/daily/1day/{key}")
    fun getDaily1Day(@Path("key") key: String, @Query("language") language: String): Call<AccuDailyForecastsGSon>

    @GET("forecasts/v1/daily/5day/{key}")
    fun getDaily5Day(@Path("key") key: String, @Query("language") language: String): Call<AccuDailyForecastsGSon>

    @GET("forecasts/v1/daily/10day/{key}")
    fun getDaily10Day(@Path("key") key: String, @Query("language") language: String): Call<AccuDailyForecastsGSon>

    @GET("forecasts/v1/daily/15day/{key}")
    fun getDaily15Day(@Path("key") key: String, @Query("language") language: String): Call<AccuDailyForecastsGSon>

    @GET("localweather/v1/{key}")
    fun getLocalWeather(@Path("key") key: String, @Query("language") language: String): Call<AccuLocalWeatherGSon>
}

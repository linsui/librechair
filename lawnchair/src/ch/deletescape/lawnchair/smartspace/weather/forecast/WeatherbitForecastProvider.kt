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
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController
import ch.deletescape.lawnchair.smartspace.weather.WeatherbitDataProvider
import ch.deletescape.lawnchair.smartspace.weather.icons.WeatherIconManager
import ch.deletescape.lawnchair.util.Temperature
import io.weatherbase.api.model.WeatherbitServiceFactory
import io.weatherbit.api.Class120HourHourlyForecastApi
import io.weatherbit.api.Class5Day3HourForecastApi
import org.apache.commons.lang3.time.DateFormatUtils
import java.text.DateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class WeatherbitForecastProvider(val context: Context) : ForecastProvider {
    override fun getHourlyForecast(lat: Double, lon: Double): ForecastProvider.Forecast {
        val response = WeatherbitServiceFactory.getRetrofitService(Class120HourHourlyForecastApi::class).forecastHourlylatlatlonlonGet(lat, lon, null, context.locale.language, null, 120).execute();
        if (!response.isSuccessful) {
            throw ForecastProvider.ForecastException(response.toString())
        }
        val weatherData: MutableList<ForecastProvider.ForecastData> = newList();
        response.body()!!.data!!.forEach {
            weatherData += ForecastProvider.ForecastData(
                    LawnchairSmartspaceController.WeatherData(WeatherIconManager(context).getIcon(WeatherbitDataProvider.ICON_IDS[it.weather.icon]!!.first, WeatherbitDataProvider.ICON_IDS[it.weather.icon]!!.second),
                                                              Temperature(it.temp.intValueExact(), Temperature.Unit.Celsius),
                                                              null,
                                                              null,
                                                              null,
                                                              lat,
                                                              lon,
                                                              it.weather.icon.toString())
                    , Date(Date.parse(it.datetime)), arrayOf(WeatherbitDataProvider.COND_IDS[it.weather.icon]!!.first))
        }
        return ForecastProvider.Forecast(weatherData)
    }

    override fun getDailyForecast(lat: Double, lon: Double): ForecastProvider.DailyForecast {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentWeather(lat: Double, lon: Double): ForecastProvider.CurrentWeather {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
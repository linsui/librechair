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

package ch.deletescape.lawnchair.feed

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ch.deletescape.lawnchair.lawnchairApp
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.*
import ch.deletescape.lawnchair.useWhiteText
import ch.deletescape.lawnchair.util.Temperature
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import net.aksingh.owmjapis.api.APIException
import net.aksingh.owmjapis.core.OWM
import net.aksingh.owmjapis.model.HourlyWeatherForecast
import java.util.concurrent.Executors

class FeedWeatherStatsProvider(c: Context) : FeedProvider(c), Listener {

    private var weatherData: WeatherData? = null
    private var forecastHigh: Int? = null
    private var forecastLow: Int? = null
    private var hourlyWeatherForecast: HourlyWeatherForecast? = null

    init {
        c.applicationContext.lawnchairApp.smartspace.addListener(this)
    }

    override fun onFeedShown() {
        // TODO
    }

    override fun onFeedHidden() {
        // TODO
    }

    override fun onCreate() {
        // TODO
    }

    override fun onDestroy() {
        // TODO
    }

    override fun getCards(): List<Card> {
        Log.d(javaClass.name, "getCards: " + weatherData!!)
        return if (weatherData != null && forecastHigh != null && forecastLow != null) listOf(
            Card(BitmapDrawable(context.resources, weatherData!!.icon),
                 context.getString(R.string.title_card_weather_temperature,
                                   weatherData!!.getTitle(context.lawnchairPrefs.weatherUnit)),
                 object : Card.Companion.InflateHelper {
                     @SuppressLint("SetTextI18n") override fun inflate(parent: ViewGroup): View {
                         val v = LayoutInflater.from(parent.getContext())
                                 .inflate(R.layout.weather_heads_up, parent, false)
                         val highLow = v.findViewById(R.id.weather_hud_day_night) as TextView
                         val information = v.findViewById(R.id.weather_hud_information) as TextView
                         highLow.text = "${forecastHigh}${context.lawnchairPrefs.weatherUnit.suffix} / ${forecastLow}${context.lawnchairPrefs.weatherUnit.suffix}"

                         if (useWhiteText(backgroundColor)) {
                             highLow.setTextColor(context.resources.getColor(R.color.textColorPrimary))
                             information.setTextColor(context.resources.getColor(R.color.textColorPrimary))
                         }
                         return v;
                     }
                 }, Card.NO_HEADER, "nosort,top"))
        else emptyList()
    }

    override fun onDataUpdated(data: DataContainer) {
        weatherData = data.weather;
        val api = OWM(context.lawnchairPrefs.weatherApiKey)
        api.unit = when (context.lawnchairPrefs.weatherUnit) {
            Temperature.Unit.Celsius -> OWM.Unit.METRIC
            Temperature.Unit.Fahrenheit ->  OWM.Unit.IMPERIAL
            Temperature.Unit.Kelvin ->  OWM.Unit.STANDARD
            Temperature.Unit.Rakine -> TODO()
            Temperature.Unit.Delisle -> TODO()
            Temperature.Unit.Newton -> TODO()
            Temperature.Unit.Reaumur -> TODO()
            Temperature.Unit.Romer -> TODO()
        }
        if (data.weather != null && data.weather.coordLat != null && data.weather.coordLong != null) {
            Executors.newSingleThreadExecutor().submit {

                d("onDataUpdated: updating forcast HUD")

                try {
                    d("onDataUpdated: fetching weather data")
                    hourlyWeatherForecast = api
                            .hourlyWeatherForecastByCoords(data.weather.coordLat,
                                                           data.weather.coordLong)
                    d("onDataUpdated: mapping day data to list")
                    val integersDay = hourlyWeatherForecast!!.dataList!!.map {
                        d("onDataUpdated:     mapping tempDay: ${it}, ${it?.tempData?.tempDay}")
                        it?.mainData?.tempMax
                    }

                    val integersNight = hourlyWeatherForecast!!.dataList!!.map {
                        d("onDataUpdated:     mapping tempNight: ${it}, ${it?.tempData?.tempMin}")
                        it?.mainData?.tempMin
                    }

                    d("onDataUpdated: assigning data to results")
                    forecastHigh = integersDay[0]!!.toInt()
                    forecastLow = integersNight[0]!!.toInt()
                    d("onDataUpdated: forecastHigh: ${forecastHigh}, forecastLow: ${forecastLow}")

                } catch (e: APIException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace();
                }
            }
        }
    }
}
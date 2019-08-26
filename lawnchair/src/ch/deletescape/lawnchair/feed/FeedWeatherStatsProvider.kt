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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.*
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import net.aksingh.owmjapis.model.DailyWeatherForecast
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class FeedWeatherStatsProvider(c: Context) : FeedProvider(c), Listener {

    private var weatherData: WeatherData? = null
    private var forecastHigh: Int? = null
    private var forecastLow: Int? = null
    private var hourlyWeatherForecast: ForecastProvider.Forecast? = null
    private var dailyForecast: DailyWeatherForecast? = null
    @StringRes
    private var weatherTypeResource: Int? = null

    private val refreshExecutor = Executors.newSingleThreadExecutor()

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
        return if (weatherData != null && forecastHigh != null && forecastLow != null && weatherTypeResource != null) listOf(
                Card(null, null,
                     object : Card.Companion.InflateHelper {
                         @SuppressLint("SetTextI18n")
                         override fun inflate(parent: ViewGroup): View {
                             d("inflate: inflating view")
                             val v = LayoutInflater.from(parent.getContext())
                                     .inflate(R.layout.weather_heads_up, parent, false)
                             d("inflate: inflated view")
                             val highLow = v.findViewById(R.id.weather_hud_day_night) as TextView
                             val information =
                                     v.findViewById(R.id.weather_hud_information) as TextView
                             val currentInformation =
                                     v.findViewById(R.id.weather_hud_current_temp) as TextView
                             val currentIcon = v.findViewById(R.id.weather_hud_icon) as ImageView
                             d("inflate: initialized views")

                             currentInformation.text =
                                     weatherData?.getTitle(context.lawnchairPrefs.weatherUnit)
                             currentIcon.setImageBitmap(weatherData?.icon)

                             d("inflate: set text for current data text view")

                             highLow.text =
                                     "${forecastHigh}${context.lawnchairPrefs.weatherUnit.suffix} / ${forecastLow}${context.lawnchairPrefs.weatherUnit.suffix}"
                             information.text = context.getString(weatherTypeResource!!)

                             d("inflate: set thext for rest of views")
                             if (useWhiteText(backgroundColor, parent.context)) {
                                 highLow.setTextColor(
                                         context.resources.getColor(R.color.textColorPrimary))
                                 information.setTextColor(
                                         context.resources.getColor(R.color.textColorPrimary))
                                 currentInformation.setTextColor(
                                         context.resources.getColor(R.color.textColorPrimary))
                             }
                             d("inflate: returning view")
                             return v
                         }
                     }, Card.NO_HEADER, "nosort,top"))
        else mutableListOf()
    }

    override fun onDataUpdated(weatherData: WeatherData?, card: CardData?) {
        if (weatherData?.coordLat != null && weatherData.coordLon != null) {
            d("onDataUpdated: updating forcast HUD", Throwable())
            refreshExecutor.submit {
                this.weatherData = weatherData;

                try {
                    d("onDataUpdated: fetching weather data")
                    hourlyWeatherForecast = context.forecastProvider
                            .getHourlyForecast(weatherData.coordLat, weatherData.coordLon)
                    d("onDataUpdated: data retrieved")
                    val tempList: List<Int?> = hourlyWeatherForecast!!.data.map {
                        if (it.date.before(tomorrow())) {
                            it.data.temperature.inUnit(context.lawnchairPrefs.weatherUnit)
                        } else {
                            null
                        }
                    }

                    d("tomorrow is: ${tomorrow()}")
                    d("today is: ${Date()}")

                    d("onDataUpdated: temp list: $tempList")
                    val today = ArrayList<Int>()
                    tempList.forEach {
                        if (it != null) {
                            today.add(it)
                        }
                    }

                    d("onDataUpdated: today's weather: $today")

                    forecastLow = Collections.min(today)
                    forecastHigh = Collections.max(today)

                    d("onDataUpdated: hi: $forecastHigh lo: $forecastLow")
                    val condCodes = run {
                        val list = newList<Int>()
                        hourlyWeatherForecast!!.data.filter { it.date.before(tomorrow()) }.forEach { list += it.condCode?.toList() ?: listOf(1) }
                        list
                    }

                    d("onDataUpdated: classifying weather")

                    val (clear, clouds, rain, snow, thunder) = WeatherTypes.getStatistics(
                            condCodes.toTypedArray())


                    d("onDataUpdated: sending classification statsitics to statistics function")
                    val type = WeatherTypes.getWeatherTypeFromStatistics(clear, clouds, rain, snow, thunder)

                    d("onDataUpdated: weather type is ${type.name}")

                    weatherTypeResource = WeatherTypes.getStringResource(type)

                    d("onDataUpdated: weather type is ${context.getString(weatherTypeResource!!)}")
                } catch (e: ForecastProvider.ForecastException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace();
                }
            }
        }
    }
}
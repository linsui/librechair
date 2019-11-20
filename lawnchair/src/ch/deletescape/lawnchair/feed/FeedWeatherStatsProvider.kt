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
import ch.deletescape.lawnchair.awareness.WeatherManager
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import java.util.*

class FeedWeatherStatsProvider(c: Context) : FeedProvider(c) {

    private var weatherData: ForecastProvider.CurrentWeather? = null
    private var forecastHigh: Int? = null
    private var forecastLow: Int? = null
    private var hourlyWeatherForecast: ForecastProvider.Forecast? = null
    @StringRes
    private var weatherTypeResource: Int? = null

    init {
        WeatherManager.subscribeWeather {
            weatherData = it
        }
        WeatherManager.subscribeHourly {
            hourlyWeatherForecast = it
            val today: List<Int> = it.data.filter {
                it.date.before(tomorrow())
            }.map { it.data.temperature.inUnit(context.lawnchairPrefs.weatherUnit) }
            forecastLow = Collections.min(today)
            forecastHigh = Collections.max(today)
            val condCodes = run {
                val list = newList<Int>()
                hourlyWeatherForecast!!.data.filter { it.date.before(tomorrow()) }
                        .forEach { list += it.condCode?.toList() ?: listOf(1) }
                list
            }
            val (clear, clouds, rain, snow, thunder) = WeatherTypes.getStatistics(
                    condCodes.toTypedArray())
            val type = WeatherTypes
                    .getWeatherTypeFromStatistics(clear, clouds, rain, snow, thunder)
            weatherTypeResource = WeatherTypes.getStringResource(type)
        }
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
                                     weatherData?.temperature?.toString(context.lawnchairPrefs.weatherUnit)
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
}
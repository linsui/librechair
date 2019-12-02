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

package ch.deletescape.lawnchair.feed.go

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.persistence.cache.CacheTime
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider
import com.android.launcher3.R
import com.google.android.apps.nexuslauncher.graphics.IcuDateTextView
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class GoWeatherProvider(context: Context) : GoCardFactory(context),
        LawnchairSmartspaceController.Listener {
    private var weatherData: LawnchairSmartspaceController.WeatherData? = null
    private var forecastHigh: Int? = null
    private var forecastLow: Int? = null
    private var hourlyWeatherForecast: ForecastProvider.Forecast? = null
    private var dailyForecast: ForecastProvider.DailyForecast? = null
    @StringRes
    private var weatherTypeResource: Int? = null
    private val refreshExecutor = Executors.newSingleThreadExecutor()
    private val cache = CacheTime();

    init {
        context.lawnchairApp.smartspace.addListener(this)
    }

    override val card: GoCard?
        get() = if (weatherData != null) GoCard { parent ->

            val v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.unified_weather, parent, false)
            v.setPadding(context.eightI,
                    context.eightI,
                    context.eightI,
                    context.eightI)

            val highLow = v.findViewById(R.id.weather_hud_day_night) as TextView
            val information = v.findViewById(R.id.weather_hud_information) as TextView
            val currentInformation =
                    v.findViewById(R.id.weather_hud_current_temp) as TextView
            val currentIcon = v.findViewById(R.id.weather_hud_icon) as ImageView
            val hourlyLayout =
                    v.findViewById(R.id.unified_weather_forecast) as LinearLayout
            val dailyLayout = v.findViewById(R.id.unified_weather_daily) as LinearLayout

            if (context.lawnchairPrefs.showVerticalDailyForecast) {
                dailyLayout.orientation = LinearLayout.VERTICAL
            }
            if (context.lawnchairPrefs.showVerticalHourlyForecast) {
                hourlyLayout.orientation = LinearLayout.VERTICAL
            }
            hourlyWeatherForecast?.data
                    ?.take(context.lawnchairPrefs.feedForecastItemCount.roundToInt())
                    ?.forEach {
                        hourlyLayout.addView(
                                LayoutInflater.from(hourlyLayout.context).inflate(
                                        if (!context.lawnchairPrefs.showVerticalHourlyForecast) R.layout.narrow_forecast_item else R.layout.straight_forecast_item,
                                        parent,
                                        false).apply {
                                    val temperature = findViewById<TextView>(
                                            R.id.forecast_current_temperature)
                                    val time = findViewById<TextView>(
                                            R.id.forecast_current_time)
                                    val icon = findViewById<ImageView>(
                                            R.id.forecast_weather_icon)

                                    viewTreeObserver.addOnGlobalLayoutListener {
                                        if (context.lawnchairPrefs.showVerticalHourlyForecast) {
                                            layoutParams = LinearLayout.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT)
                                        }
                                        true
                                    }

                                    icon.setImageBitmap(it.data.icon)
                                    val zonedDateTime = ZonedDateTime
                                            .ofInstant(it.date.toInstant(),
                                                    ZoneId.of("UTC"))
                                            .withZoneSameInstant(ZoneId.systemDefault())
                                    time.text = formatTime(zonedDateTime, context)
                                    temperature.text = it.data.temperature.toString(
                                            context.lawnchairPrefs.weatherUnit)
                                    layoutParams = LinearLayout
                                            .LayoutParams(
                                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT)
                                            .apply {
                                                weight = 1f
                                            }
                                })
                    }

            dailyForecast?.dailyForecastData
                    ?.take(context.lawnchairPrefs.feedDailyForecastItemCount.roundToInt())
                    ?.forEach {
                        dailyLayout.addView(
                                LayoutInflater.from(hourlyLayout.context).inflate(
                                        if (context.lawnchairPrefs.showVerticalDailyForecast) R.layout.straight_forecast_item else R.layout.narrow_forecast_item,
                                        dailyLayout, false).apply {
                                    viewTreeObserver.addOnGlobalLayoutListener {
                                        if (context.lawnchairPrefs.showVerticalDailyForecast) {
                                            layoutParams = LinearLayout.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT)
                                        }
                                    }
                                    val temperature = findViewById<TextView>(
                                            R.id.forecast_current_temperature)
                                    val time = findViewById<TextView>(
                                            R.id.forecast_current_time)
                                    val icon = findViewById<ImageView>(
                                            R.id.forecast_weather_icon)

                                    icon.setImageBitmap(it.icon)
                                    val zonedDateTime = ZonedDateTime
                                            .ofInstant(it.date.toInstant(),
                                                    ZoneId.of("UTC"))
                                            .withZoneSameInstant(ZoneId.systemDefault())
                                    if (context.lawnchairPrefs.showVerticalDailyForecast) {
                                        time.text =
                                                IcuDateTextView.getDateFormat(context, false, null,
                                                        false).format(
                                                        Date.from(
                                                                Instant.ofEpochSecond(
                                                                        zonedDateTime.toEpochSecond())))

                                    } else {
                                        time.text =
                                                "${zonedDateTime.month.value} / ${zonedDateTime.dayOfMonth}"
                                    }
                                    temperature.text = "${it.low.toString(
                                            context.lawnchairPrefs.weatherUnit)} / ${it.high.toString(
                                            context.lawnchairPrefs.weatherUnit)}"

                                    layoutParams = LinearLayout
                                            .LayoutParams(
                                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT)
                                            .apply {
                                                weight = 1f
                                            }
                                })
                    }



            currentInformation.text =
                    weatherData?.getTitle(context.lawnchairPrefs.weatherUnit)
            currentIcon.setImageBitmap(weatherData?.icon)



            if (forecastHigh != null && forecastLow != null) {
                highLow.text =
                        "${forecastHigh}${context.lawnchairPrefs.weatherUnit.suffix} / ${forecastLow}${context.lawnchairPrefs.weatherUnit.suffix}"
            }
            information.text = weatherTypeResource?.let { context.getString(it) }
            v
        } else null

    override fun onDataUpdated(weatherData: LawnchairSmartspaceController.WeatherData?,
                               card: LawnchairSmartspaceController.CardData?) {
        if (weatherData?.coordLat != null && weatherData.coordLon != null && cache.expired) {
            refreshExecutor.submit {
                this.weatherData = weatherData;
                try {
                    try {
                        hourlyWeatherForecast = context.forecastProvider
                                .getHourlyForecast(weatherData.coordLat, weatherData.coordLon)
                    } catch (e: ForecastProvider.ForecastException) {
                        e.printStackTrace()
                    }
                    try {
                        dailyForecast = context.forecastProvider
                                .getDailyForecast(weatherData.coordLat, weatherData.coordLon)
                    } catch (e: ForecastProvider.ForecastException) {
                        e.printStackTrace()
                    }
                    val tempList: List<Int?> = hourlyWeatherForecast!!.data.map {
                        if (it.date.before(tomorrow())) {
                            it.data.temperature.inUnit(context.lawnchairPrefs.weatherUnit)
                        } else {
                            null
                        }
                    }
                    val today = ArrayList<Int>()
                    tempList.forEach {
                        if (it != null) {
                            today.add(it)
                        }
                    }
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
                    cache.trigger()
                } catch (e: ForecastProvider.ForecastException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace();
                }
            }
        }
    }
}
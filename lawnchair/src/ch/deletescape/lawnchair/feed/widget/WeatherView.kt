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

package ch.deletescape.lawnchair.feed.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.awareness.TickManager
import ch.deletescape.lawnchair.awareness.WeatherManager
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider
import ch.deletescape.lawnchair.theme.ThemeManager
import com.android.launcher3.R
import com.google.android.apps.nexuslauncher.graphics.IcuDateTextView
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.math.roundToInt

class WeatherView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private var weatherData: ForecastProvider.CurrentWeather? = null
    private var forecastHigh: Int? = null
    private var forecastLow: Int? = null
    private var hourlyWeatherForecast: ForecastProvider.Forecast? = null
    private var dailyForecast: ForecastProvider.DailyForecast? = null
    @StringRes
    private var weatherTypeResource: Int? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        WeatherManager.subscribeDaily {
            dailyForecast = it
            post {
            }
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
            post {
                onTick()
            }
        }
        WeatherManager.subscribeWeather {
            weatherData = it
            post {
                onTick()
            }
        }
        TickManager.subscribe {
            onTick()
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateData() = if (weatherData != null) {
        val highLow = findViewById<TextView>(R.id.weather_hud_day_night)
        val information = findViewById<TextView>(R.id.weather_hud_information)
        val currentInformation = findViewById<TextView>(R.id.weather_hud_current_temp)
        val currentIcon = findViewById<ImageView>(R.id.weather_hud_icon)
        val hourlyLayout = findViewById<LinearLayout>(R.id.unified_weather_forecast)
        val dailyLayout = findViewById<LinearLayout>(R.id.unified_weather_daily)

        hourlyLayout.removeAllViews()
        dailyLayout.removeAllViews()

        if (context.lawnchairPrefs.showVerticalHourlyForecast) {
            hourlyLayout.orientation = LinearLayout.VERTICAL
        }
        hourlyWeatherForecast?.data
                ?.take(context.lawnchairPrefs.feedForecastItemCount.roundToInt())
                ?.forEach {
                    hourlyLayout.addView(
                            LayoutInflater.from(hourlyLayout.context).inflate(
                                    if (!context.lawnchairPrefs.showVerticalHourlyForecast) R.layout.narrow_forecast_item else R.layout.straight_forecast_item, hourlyLayout,
                                    false).apply {
                                val temperature = findViewById<TextView>(
                                        R.id.forecast_current_temperature)
                                val time = findViewById<TextView>(
                                        R.id.forecast_current_time)
                                val icon = findViewById<ImageView>(
                                        R.id.forecast_weather_icon)

                                viewTreeObserver.addOnPreDrawListener {
                                    if (context.lawnchairPrefs.showVerticalHourlyForecast) {
                                        layoutParams = LinearLayout.LayoutParams(
                                                dailyLayout.also {
                                                    it.measure(
                                                            MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
                                                }.width, ViewGroup.LayoutParams.WRAP_CONTENT)
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
                                if (!ThemeManager.getInstance(context).supportsDarkText) {
                                    time.setTextColor(Color.WHITE)
                                    temperature.setTextColor(Color.WHITE)
                                }
                                layoutParams = LinearLayout
                                        .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                        .apply {
                                            weight = 1f
                                        }
                            })
                }

        dailyForecast?.dailyForecastData
                ?.take(context.lawnchairPrefs.feedDailyForecastItemCount.roundToInt())?.forEach {
                    if (context.lawnchairPrefs.showVerticalDailyForecast) {
                        dailyLayout.orientation = LinearLayout.VERTICAL
                    }
                    dailyLayout.addView(LayoutInflater.from(hourlyLayout.context).inflate(
                            if (context.lawnchairPrefs.showVerticalDailyForecast) R.layout.straight_forecast_item else R.layout.narrow_forecast_item,
                            dailyLayout, false).apply {
                        viewTreeObserver.addOnGlobalLayoutListener {
                            if (context.lawnchairPrefs.showVerticalDailyForecast) {
                                layoutParams = LinearLayout.LayoutParams(dailyLayout.also {
                                    it.measure(MeasureSpec.UNSPECIFIED,
                                            MeasureSpec.UNSPECIFIED)
                                }.width, ViewGroup.LayoutParams.WRAP_CONTENT)
                            }
                        }
                        val temperature =
                                findViewById<TextView>(R.id.forecast_current_temperature)
                        val time = findViewById<TextView>(R.id.forecast_current_time)
                        val icon = findViewById<ImageView>(R.id.forecast_weather_icon)

                        icon.setImageBitmap(it.icon)
                        val zonedDateTime =
                                ZonedDateTime.ofInstant(it.date.toInstant(), ZoneId.of("UTC"))
                                        .withZoneSameInstant(ZoneId.systemDefault())
                        if (context.lawnchairPrefs.showVerticalDailyForecast) {
                            time.text = IcuDateTextView.getDateFormat(context, false, null, false)
                                    .format(Date.from(
                                            Instant.ofEpochSecond(zonedDateTime.toEpochSecond())))
                        } else {
                            time.text = "${zonedDateTime.month.value} / ${zonedDateTime.dayOfMonth}"
                        }
                        temperature.text = "${it.low.toString(
                                context.lawnchairPrefs.weatherUnit)} / ${it.high.toString(
                                context.lawnchairPrefs.weatherUnit)}"
                        if (!ThemeManager.getInstance(context).supportsDarkText) {
                            time.setTextColor(Color.WHITE)
                            temperature.setTextColor(Color.WHITE)
                        }
                        layoutParams = LinearLayout
                                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                                    weight = 1f
                                }
                    })
                }


        currentInformation.text =
                weatherData?.temperature?.toString(context.lawnchairPrefs.weatherUnit)
        currentIcon.setImageBitmap(weatherData?.icon)


        if (forecastHigh != null && forecastLow != null) {
            highLow.text =
                    "${forecastHigh}${context.lawnchairPrefs.weatherUnit.suffix} / ${forecastLow}${context.lawnchairPrefs.weatherUnit.suffix}"
        }

        if (!ThemeManager.getInstance(context).supportsDarkText) {
            highLow.setTextColor(context.getColor(R.color.textColorPrimary))
            information.setTextColor(context.getColor(R.color.textColorPrimary))
            currentInformation.setTextColor(context.getColor(R.color.textColorPrimary))
        }
        information.text = weatherTypeResource?.let { context.getString(it) }
    } else {
        val information = findViewById<TextView>(R.id.weather_hud_day_night)
        information.setText(R.string.loading)
        if (!ThemeManager.getInstance(context).supportsDarkText) {
            information.setTextColor(context.getColor(R.color.textColorPrimary))
        }
        Unit
    }

    private fun onTick() {
        updateData()
    }
}
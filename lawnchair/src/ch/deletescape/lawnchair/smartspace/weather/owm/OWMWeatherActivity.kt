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

package ch.deletescape.lawnchair.smartspace.weather.owm

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ch.deletescape.lawnchair.settings.ui.SettingsBaseActivity
import ch.deletescape.lawnchair.smartspace.WeatherIconProvider
import ch.deletescape.lawnchair.util.Temperature
import com.android.launcher3.R
import com.android.launcher3.Utilities
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper
import net.aksingh.owmjapis.api.APIException
import net.aksingh.owmjapis.core.OWM
import net.aksingh.owmjapis.core.OWMPro
import net.aksingh.owmjapis.model.DailyWeatherForecast
import net.aksingh.owmjapis.model.HourlyWeatherForecast
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class OWMWeatherActivity : SettingsBaseActivity() {
    private var iconView: ImageView? = null
    private var weatherTitleText: TextView? = null
    private var weatherHelpfulTip: TextView? = null
    private var threeHourForecastRecyclerView: RecyclerView? = null;
    private var twentyFourHourForecastRecyclerView: RecyclerView? = null;
    private var icon: Bitmap? = null;
    private var threeHourAdapter: HourlyForecastAdapter? = null
    private var twentyFourHourAdapter: DailyForecastAdapter? = null
    private val prefs = Utilities.getLawnchairPrefs(this)
    private val owm = OpenWeatherMapHelper(prefs.weatherApiKey)
    private val owmApi = OWM(prefs.weatherApiKey)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owmweather)
        iconView = findViewById(R.id.current_weather_icon)
        weatherTitleText = findViewById(R.id.current_weather_text)
        weatherHelpfulTip = findViewById(R.id.weather_helpful_tip)
        threeHourForecastRecyclerView = findViewById(R.id.next_three_hours_forecast);
        twentyFourHourForecastRecyclerView = findViewById(R.id.next_twenty_four_forecast);

        icon = WeatherIconProvider(this).getIcon(intent!!.extras!!.getString("weather_icon"))
        weatherTitleText!!.text = intent!!.extras!!.getString("weather_text")
        iconView!!.setImageDrawable(BitmapDrawable(resources, icon!!))

        var resId: Int = R.string.helpful_tip_non_available
        when (intent!!.extras!!.getString("weather_icon")) {
            "01d", "02d", "03d" -> resId = R.string.helpful_tip_01_03
            "01n", "02n", "03n" -> resId = R.string.helpful_tip_01n_03n
            "04d" -> resId = R.string.helpful_tip_04
            "04n" -> resId = R.string.helpful_tip_04n
            "09d", "09n", "10d" -> resId = R.string.helpful_tip_09_10
            "10n" -> resId = R.string.helpful_tip_10n
            "11d", "11n" -> resId = R.string.helpful_tip_11
            "13d", "13n" -> resId = R.string.helpful_tip_13
            "50d", "50n" -> resId = R.string.helpful_tip_50
        }
        weatherHelpfulTip!!.text = getString(resId)
        Executors.newSingleThreadExecutor().submit {
            owmApi.unit = when (prefs.weatherUnit) {
                Temperature.Unit.Celsius -> OWM.Unit.METRIC
                Temperature.Unit.Fahrenheit -> OWM.Unit.IMPERIAL
                Temperature.Unit.Kelvin -> OWM.Unit.STANDARD
                Temperature.Unit.Rakine -> TODO()
                Temperature.Unit.Delisle -> TODO()
                Temperature.Unit.Newton -> TODO()
                Temperature.Unit.Reaumur -> TODO()
                Temperature.Unit.Romer -> TODO()
            }
            try {
                val hourlyForecast = owmApi.hourlyWeatherForecastByCoords(intent!!.extras!!.getDouble("city_lat"),
                                                                          intent!!.extras!!.getDouble("city_lon"))
                runOnUiThread() {
                    threeHourAdapter =
                            HourlyForecastAdapter(hourlyForecast, this, prefs.weatherUnit)
                    threeHourForecastRecyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    threeHourForecastRecyclerView!!.adapter = threeHourAdapter!!
                    threeHourForecastRecyclerView!!.layoutManager!!.isItemPrefetchEnabled = false
                }
            } catch (e: APIException) {
                Log.w(javaClass.name, "onCreate lambda failed to obtain hourly weather report!")
            }
            try {
                val dailyForecast = OWMPro(prefs.weatherApiKey).dailyWeatherForecastByCoords(intent!!.extras!!.getDouble("city_lat"),
                                                                                              intent!!.extras!!.getDouble("city_lon"));
                runOnUiThread() {
                    twentyFourHourAdapter =
                            DailyForecastAdapter(dailyForecast, this, prefs.weatherUnit)
                    twentyFourHourForecastRecyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    twentyFourHourForecastRecyclerView!!.adapter = threeHourAdapter!!
                    twentyFourHourForecastRecyclerView!!.layoutManager!!.isItemPrefetchEnabled = false
                }
            } catch (e: APIException) {
                Log.w(javaClass.name, "onCreate lambda failed to obtain daily weather report!")
            }
        }
    }

    class ThreeHourForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.forecast_weather_icon)
        val time: TextView = itemView.findViewById(R.id.forecast_current_time)
        val temperature: TextView = itemView.findViewById(R.id.forecast_current_temperature);

        constructor(parentView: ViewGroup) : this(
            LayoutInflater.from(parentView.context).inflate(R.layout.three_hour_forecast_item,
                                                            parentView, false))
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    class HourlyForecastAdapter(val hourlyWeatherForecast: HourlyWeatherForecast, val context: Context,
                                val weatherUnit: Temperature.Unit) :
            RecyclerView.Adapter<ThreeHourForecastViewHolder>() {
        private val iconProvider by lazy { WeatherIconProvider(context) }
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ThreeHourForecastViewHolder {
            return ThreeHourForecastViewHolder(parent)
        }

        override fun getItemCount(): Int {
            return hourlyWeatherForecast.dataList!!.size
        }

        @SuppressLint("SetTextI18n") override fun onBindViewHolder(
            holder: ThreeHourForecastViewHolder, position: Int) {
            val currentWeather = hourlyWeatherForecast.dataList!!.get(position)
            var zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(currentWeather!!.dateTime!!.time / 1000), ZoneId.of("UTC"))
            zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(TimeZone.getDefault().rawOffset / 1000)))
            holder.icon.setImageBitmap(iconProvider.getIcon(currentWeather.weatherList!!.get(0)!!.iconCode))
            holder.time.text = "${if (zonedDateTime.hour < 10)  "0" + zonedDateTime.hour else "" + zonedDateTime.hour}:${if (zonedDateTime.minute < 10)  "0" + zonedDateTime.minute else zonedDateTime.minute}"
            holder.temperature.text =
                    "${currentWeather.mainData?.temp?.roundToInt()}${weatherUnit.suffix.capitalize()}"
        }

    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    class DailyForecastAdapter(val dailyWeatherForcast: DailyWeatherForecast, val context: Context,
                              val weatherUnit: Temperature.Unit) :
            RecyclerView.Adapter<ThreeHourForecastViewHolder>() {
        private val iconProvider by lazy { WeatherIconProvider(context) }
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ThreeHourForecastViewHolder {
            return ThreeHourForecastViewHolder(parent)
        }

        override fun getItemCount(): Int {
            return dailyWeatherForcast.dataList!!.size
        }

        @SuppressLint("SetTextI18n") override fun onBindViewHolder(
            holder: ThreeHourForecastViewHolder, position: Int) {
            val currentWeather = dailyWeatherForcast.dataList!!.get(position)
            var zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(currentWeather!!.dateTime!!.time / 1000), ZoneId.of("UTC"))
            zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(TimeZone.getDefault().rawOffset / 1000)))
            holder.icon.setImageBitmap(iconProvider.getIcon(currentWeather.weatherList!!.get(0)!!.iconCode))
            holder.time.text = "${zonedDateTime.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())} ${zonedDateTime.month.value}/${zonedDateTime.dayOfMonth}"
            holder.temperature.text =
                    "${currentWeather.tempData?.tempMin}${weatherUnit.suffix.capitalize()} –– ${currentWeather.tempData?.tempMax}${weatherUnit.suffix.capitalize()}"
        }
    }
}
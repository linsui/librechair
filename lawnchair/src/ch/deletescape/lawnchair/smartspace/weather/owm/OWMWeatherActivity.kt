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
import com.kwabenaberko.openweathermaplib.constants.Units
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper
import com.kwabenaberko.openweathermaplib.implementation.callbacks.ThreeHourForecastCallback
import com.kwabenaberko.openweathermaplib.models.threehourforecast.ThreeHourForecast
import java.util.*
import kotlin.math.roundToInt

class OWMWeatherActivity : SettingsBaseActivity(), ThreeHourForecastCallback {
    private var iconView: ImageView? = null
    private var weatherTitleText: TextView? = null
    private var weatherHelpfulTip: TextView? = null
    private var threeHourForecastRecyclerView: RecyclerView? = null;
    private var twentyFourHourForecastRecyclerView: RecyclerView? = null;
    private var icon: Bitmap? = null;
    private var threeHourAdapter: ThreeHourForecastAdapter? = null
    private var cityId: String? = null
    private val owm by lazy { OpenWeatherMapHelper(prefs.weatherApiKey) }
    private val prefs = Utilities.getLawnchairPrefs(this)

    override fun onSuccess(threeHourForecast: ThreeHourForecast?) {
        threeHourAdapter =
                ThreeHourForecastAdapter(threeHourForecast!!, this, prefs!!.weatherUnit)
        threeHourForecastRecyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        threeHourForecastRecyclerView!!.adapter = threeHourAdapter!!
    }

    override fun onFailure(throwable: Throwable?) {
        /*
        * For now we fail silently
        * TODO implement fail & retry logic
        */
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owmweather)
        iconView = findViewById(R.id.current_weather_icon)
        weatherTitleText = findViewById(R.id.current_weather_text)
        weatherHelpfulTip = findViewById(R.id.weather_helpful_tip)

        cityId = intent!!.extras!!.getString("city_id")
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
        owm.setUnits(when (prefs.weatherUnit) {
                         Temperature.Unit.Celsius -> Units.METRIC
                         Temperature.Unit.Fahrenheit -> Units.IMPERIAL
                         else -> Units.METRIC
                     })
        owm.getThreeHourForecastByCityID(cityId!!, this)
    }

    class ThreeHourForecastAdapter(val threeHourForecast: ThreeHourForecast, val context: Context,
                                   val weatherUnit: Temperature.Unit) :
            RecyclerView.Adapter<ThreeHourForecastAdapter.ThreeHourForecastViewHolder>() {
        private val iconProvider by lazy { WeatherIconProvider(context) }
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ThreeHourForecastViewHolder {
            return ThreeHourForecastViewHolder(parent)
        }

        override fun getItemCount(): Int {
            return 3;
        }

        @SuppressLint("SetTextI18n") override fun onBindViewHolder(
            holder: ThreeHourForecastViewHolder, position: Int) {
            val threeHourWeather = threeHourForecast.list.get(position)
            val time = GregorianCalendar()
            time.timeInMillis = threeHourWeather.dt
            val weather = threeHourWeather.weatherArray.get(0)
            holder.icon.setImageBitmap(iconProvider.getIcon(weather.icon))
            holder.time.text = "${time.get(Calendar.HOUR_OF_DAY)}:${time.get(Calendar.MINUTE)}"
            holder.temperature.text =
                    "${threeHourWeather.main.temp.roundToInt()}${weatherUnit.suffix.capitalize()}"
        }

        class ThreeHourForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val icon: ImageView = itemView.findViewById(R.id.forecast_weather_icon)
            val time: TextView = itemView.findViewById(R.id.forecast_current_time)
            val temperature: TextView = itemView.findViewById(R.id.forecast_current_temperature);

            constructor(parentView: ViewGroup) : this(
                LayoutInflater.from(parentView.context).inflate(R.layout.three_hour_forecast_item,
                                                                parentView, false))
        }
    }
}
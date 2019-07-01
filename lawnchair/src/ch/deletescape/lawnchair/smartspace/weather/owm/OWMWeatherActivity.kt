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

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import ch.deletescape.lawnchair.settings.ui.SettingsBaseActivity
import ch.deletescape.lawnchair.smartspace.WeatherIconProvider
import com.android.launcher3.R

class OWMWeatherActivity : SettingsBaseActivity() {

    var iconView: ImageView? = null
    var weatherTitleText: TextView? = null
    var weatherHelpfulTip: TextView? = null
    var icon: Bitmap? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owmweather)
        iconView = findViewById(R.id.current_weather_icon)
        weatherTitleText = findViewById(R.id.current_weather_text)
        weatherHelpfulTip = findViewById(R.id.weather_helpful_tip)

        icon = WeatherIconProvider(this).getIcon(intent!!.extras!!.getString("weather_icon"))
        weatherTitleText!!.text = intent!!.extras!!.getString("weather_text")
        iconView!!.setImageDrawable(BitmapDrawable(resources, icon!!))

        var resId: Int = R.string.helpful_tip_non_available
        when (intent!!.extras!!.getString("weather_icon"))
        {
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
    }
}

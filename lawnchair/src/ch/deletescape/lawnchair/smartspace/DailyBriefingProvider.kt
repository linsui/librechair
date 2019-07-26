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

package ch.deletescape.lawnchair.smartspace

import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import java.util.*
import java.util.concurrent.TimeUnit

class DailyBriefingProvider(controller: LawnchairSmartspaceController) :
        LawnchairSmartspaceController.PeriodicDataProvider(controller) {
    override val timeout = TimeUnit.SECONDS.toMillis(10)

    override fun updateData() {
        val currentTime = GregorianCalendar()
        val (hour, minute) = context.lawnchairPrefs.wakeUpCallTime.split(":").map { it.toInt() }
        d("updateData: briefing is scheduled for $hour:$minute and current time is ${currentTime.hourOfDay}:${currentTime.minuteOfHour}")
        if (currentTime.hourOfDay == hour && currentTime.minuteOfHour >= minute - 10 && currentTime.minuteOfHour <= minute + 10) {
            d("updateData: updating daily brief")
            runOnNewThread {
                try {
                    val (lat, lon) = context.forecastProvider.getGeolocation(
                            context.lawnchairPrefs.weatherCity)
                    d("updateData: retrieved location: $lat, $lon")
                    val currentWeather = context.forecastProvider.getCurrentWeather(lat, lon)
                    val dailyForecast = context.forecastProvider.getDailyForecast(lat, lon)
                    val hourlyForecast = context.forecastProvider.getHourlyForecast(lat, lon)

                    val lines = mutableListOf(LawnchairSmartspaceController.Line(
                            context.getString(
                                    R.string.daily_briefing_current_conditions_prefix) + " " + currentWeather.temperature.toString(
                                    context.lawnchairPrefs.weatherUnit)),
                                              LawnchairSmartspaceController.Line(
                                                      context.getString(
                                                              R.string.daily_breifing_today_prefix) + " " + run {
                                                          val (clear, clouds, rain, snow, thunder) = WeatherTypes.getStatistics(
                                                                  run {
                                                                      val list = newList<Int>()
                                                                      hourlyForecast.data.filter {
                                                                          it.date.before(tomorrow())
                                                                      }
                                                                              .forEach {
                                                                                  list += it.condCode?.toList()
                                                                                          ?: listOf(
                                                                                                  1)
                                                                              }
                                                                      list.toTypedArray()
                                                                  })
                                                          WeatherTypes.getStringResource(
                                                                  WeatherTypes.getWeatherTypeFromStatistics(
                                                                          clear, clouds, rain, snow,
                                                                          thunder))
                                                                  .fromStringRes(context)
                                                                  .toLowerCase()
                                                      }))
                    runOnMainThread {
                        updateData(null, LawnchairSmartspaceController.CardData(null, lines,
                                                                                forceSingleLine = true))
                    }
                } catch (e: ForecastProvider.ForecastException) {
                    e.printStackTrace()
                }
            }
        }
    }
}

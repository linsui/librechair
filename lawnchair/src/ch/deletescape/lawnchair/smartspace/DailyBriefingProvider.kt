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

import android.database.Cursor
import android.provider.CalendarContract
import android.text.TextUtils
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.awareness.WeatherManager
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import java.util.*
import java.util.concurrent.TimeUnit

class DailyBriefingProvider(controller: LawnchairSmartspaceController) :
        LawnchairSmartspaceController.PeriodicDataProvider(controller) {
    override val timeout = TimeUnit.SECONDS.toMillis(10)
    private var briefingShown = false to 0L
    private lateinit var forecast: ForecastProvider.Forecast
    private lateinit var currentWeather: ForecastProvider.CurrentWeather

    init {
        WeatherManager.subscribeHourly { forecast = it }
        WeatherManager.subscribeWeather { currentWeather = it }
    }

    override fun updateData() {
        var currentTime = GregorianCalendar()
        val (hour, minute) = context.lawnchairPrefs.wakeUpCallTime.split(":").map { it.toInt() }
        d("updateData: briefing is scheduled for $hour:$minute and current time is ${currentTime.hourOfDay}:${currentTime.minuteOfHour}")
        if (currentTime.hourOfDay == hour && currentTime.minuteOfHour >= minute - 10 && currentTime.minuteOfHour <= minute + 10) {
            d("updateData: updating daily brief")
            runOnUiWorkerThread {
                if (::forecast.isInitialized && ::currentWeather.isInitialized) {
                    if (!briefingShown.first || briefingShown.second < System.currentTimeMillis()) {
                        try {
                            val lines = mutableListOf(LawnchairSmartspaceController.Line(
                                    context.getString(
                                            R.string.daily_briefing_current_conditions_prefix) + " " + currentWeather.temperature.toString(
                                            context.lawnchairPrefs.weatherUnit),
                                    TextUtils.TruncateAt.MARQUEE),
                                    LawnchairSmartspaceController.Line(
                                            context.getString(
                                                    R.string.daily_breifing_today_prefix) + " " + run {
                                                val (clear, clouds, rain, snow, thunder) = WeatherTypes.getStatistics(
                                                        run {
                                                            val list = newList<Int>()
                                                            forecast.data.filter {
                                                                it.date.before(tomorrow())
                                                            }.forEach {
                                                                list += it.condCode?.toList()
                                                                        ?: listOf(1)
                                                            }
                                                            d("updateData: parsed condition codes $list")
                                                            list.toTypedArray()
                                                        })
                                                d("updateData: parsed condition type ${WeatherTypes.getStringResource(
                                                        WeatherTypes.getWeatherTypeFromStatistics(
                                                                clear, clouds,
                                                                rain, snow,
                                                                thunder)).fromStringRes(
                                                        context).toLowerCase()}")
                                                WeatherTypes.getStringResource(
                                                        WeatherTypes.getWeatherTypeFromStatistics(
                                                                clear, clouds,
                                                                rain, snow,
                                                                thunder))
                                                        .fromStringRes(context).toLowerCase()
                                            }, TextUtils.TruncateAt.MARQUEE))
                            val query =
                                    "(( " + CalendarContract.Events.DTSTART + " >= " + currentTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + tomorrow().time + " ))"
                            var hasCalendar = false
                            try {
                                val eventCursor: Cursor =
                                        context.contentResolver.query(
                                                CalendarContract.Events.CONTENT_URI,
                                                arrayOf(CalendarContract.Instances.TITLE,
                                                        CalendarContract.Instances.DTSTART,
                                                        CalendarContract.Instances.DTEND,
                                                        CalendarContract.Instances.DESCRIPTION,
                                                        CalendarContract.Events._ID,
                                                        CalendarContract.Instances.CUSTOM_APP_PACKAGE,
                                                        CalendarContract.Events.EVENT_LOCATION),
                                                query, null,
                                                CalendarContract.Instances.DTSTART + " ASC")!!

                                if (eventCursor.count >= 1) {
                                    hasCalendar = true
                                    lines += LawnchairSmartspaceController
                                            .Line(R.plurals.title_daily_briefing_calendar_events.fromPluralRes(
                                                    context, eventCursor.count))
                                }
                                eventCursor.close()
                            } catch (e: SecurityException) {
                                e.printStackTrace()
                            }

                            d("updateData: formatted information into $lines")

                            runOnMainThread {
                                var updateDataWithEvents: () -> Unit = {}
                                val updateDataNoEvents: () -> Unit = {
                                    currentTime = GregorianCalendar()
                                    if (currentTime.hourOfDay == hour && currentTime.minuteOfHour >= minute - 10 && currentTime.minuteOfHour <= minute + 10) {
                                        updateData(null,
                                                LawnchairSmartspaceController.CardData(null, lines,
                                                        forceSingleLine = false))
                                        if (hasCalendar) {
                                            mainHandler.postDelayed(updateDataWithEvents, 1000 * 2)
                                        }
                                    } else {
                                        updateData(null, null)
                                    }
                                }
                                updateDataWithEvents = {
                                    if (currentTime.hourOfDay == hour && currentTime.minuteOfHour >= minute - 10 && currentTime.minuteOfHour <= minute + 10) {
                                        updateData(null,
                                                LawnchairSmartspaceController.CardData(null,
                                                        listOf(lines[1]),
                                                        forceSingleLine = false))
                                        mainHandler.postDelayed(updateDataNoEvents, 1000 * 2)
                                    } else {
                                        updateData(null, null)
                                    }
                                }
                                updateDataNoEvents()
                                briefingShown = true to System.currentTimeMillis() + 1000 * 60 * 10
                            }
                        } catch (e: ForecastProvider.ForecastException) {
                            e.printStackTrace()
                        } catch (e: NullPointerException) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            updateData(null, null)
        }
    }
}

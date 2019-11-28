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

package ch.deletescape.lawnchair.feed.chips.calendar

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.feed.chips.ChipProvider
import ch.deletescape.lawnchair.feed.maps.MapScreen
import ch.deletescape.lawnchair.feed.maps.locationsearch.LocationSearchManager
import ch.deletescape.lawnchair.feed.util.FeedUtil
import ch.deletescape.lawnchair.fromDrawableRes
import ch.deletescape.lawnchair.getPostionOnScreen
import ch.deletescape.lawnchair.lawnchairPrefs
import com.android.launcher3.R
import com.google.android.apps.nexuslauncher.graphics.IcuDateTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*
import java.util.function.Consumer

class UpcomingEventsProvider(val context: Context) : ChipProvider() {
    @SuppressLint("Recycle")
    override fun getItems(context: Context):
            List<Item> {
        val chips = mutableListOf<Item>()
        val currentTime = GregorianCalendar()
        val endTime = GregorianCalendar().apply {
            add(Calendar.DAY_OF_MONTH, context.lawnchairPrefs.feedCalendarEventThreshold)
        }
        val query =
                "(( " + CalendarContract.Events.DTSTART + " >= " + currentTime.timeInMillis + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.timeInMillis + " ))"
        try {
            val eventCursor: Cursor = context.contentResolver
                    .query(CalendarContract.Events.CONTENT_URI,
                            arrayOf(CalendarContract.Instances.TITLE,
                                    CalendarContract.Instances.DTSTART,
                                    CalendarContract.Instances.DTEND,
                                    CalendarContract.Instances.DESCRIPTION,
                                    CalendarContract.Events._ID,
                                    CalendarContract.Instances.CUSTOM_APP_PACKAGE,
                                    CalendarContract.Events.EVENT_LOCATION,
                                    CalendarContract.Calendars.CALENDAR_COLOR), query, null,
                            CalendarContract.Instances.DTSTART + " ASC")!!
            eventCursor.moveToFirst()
            while (eventCursor.isAfterLast.not()) {
                val startTime = GregorianCalendar()
                startTime.timeInMillis = eventCursor.getLong(1)
                val eventEndTime = GregorianCalendar()
                eventEndTime.timeInMillis = eventCursor.getLong(2)
                val text: String
                val diff = startTime.timeInMillis - currentTime.timeInMillis
                val diffMinutes = diff / (60 * 1000)
                val diffHours = diff / (60 * 60 * 1000)
                val diffDays = diff / (24 * 60 * 60 * 1000)
                when {
                    diffDays > 20 -> text = IcuDateTextView.getDateFormat(context, true, null, false)
                            .format(Date.from(Instant.ofEpochMilli(startTime.timeInMillis)))
                    diffDays >= 1 -> text = if (diffDays < 1 || diffDays > 1) context.getString(
                            R.string.title_text_calendar_feed_provider_in_d_days,
                            diffDays) else context.getString(R.string.tomorrow)
                    diffHours > 4 -> text = context
                            .getString(R.string.title_text_calendar_feed_in_d_hours, diffHours)
                    else -> text = if (diffMinutes <= 0) context.getString(
                            R.string.reusable_str_now) else context.getString(
                            if (diffMinutes < 1 || diffMinutes > 1) R.string.subtitle_smartspace_in_minutes else R.string.subtitle_smartspace_in_minute,
                            diffMinutes)
                }
                var intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri
                        .parse("content://com.android.calendar/events/" + eventCursor.getLong(
                                4).toString())
                if (eventCursor.getString(5) != null) {
                    if (context.packageManager.getApplicationEnabledSetting(
                                    eventCursor.getString(
                                            5)!!) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                        intent.`package` = eventCursor.getString(5)!!
                    }
                }
                val address = eventCursor.getString(6)
                intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NEW_TASK
                if (address?.trim()?.isEmpty() == false) {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$address"))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                chips += Item().apply {
                    title = "${eventCursor.getString(0)} ($text)"
                    icon = R.drawable.ic_event_black_24dp.fromDrawableRes(context)
                    viewClickListener = Consumer {
                        if (address?.trim()?.isEmpty() == false) {
                            FeedScope.launch {
                                val loc = LocationSearchManager.getInstance(context).get(address)
                                if (loc != null) {
                                    FeedScope.launch(Dispatchers.Main) {
                                        MapScreen(context, launcherFeed, loc.first, loc.second,
                                                13.0).display(launcherFeed,
                                                it.getPostionOnScreen().first + it.measuredWidth / 2,
                                                it.getPostionOnScreen().second + it.measuredHeight / 2, it)
                                    }
                                } else {
                                    FeedScope.launch(Dispatchers.Main) {
                                        try {
                                            FeedUtil.startActivity(context, intent, it)
                                        } catch (e: ActivityNotFoundException) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                eventCursor.moveToNext()
            }
            eventCursor.close()
        } catch (e: SecurityException) {
            e.printStackTrace()
            return listOf(Item().apply {
                title = context.getString(R.string.title_chip_need_calendar_permissions)
            })
        }
        return chips
    }

}
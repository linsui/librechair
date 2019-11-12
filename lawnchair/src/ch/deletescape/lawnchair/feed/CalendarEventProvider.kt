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
 *     but WITHOUT ANY WARRANTY without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.Typeface
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.view.ViewGroup
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.awareness.CalendarManager
import ch.deletescape.lawnchair.awareness.TickManager
import ch.deletescape.lawnchair.feed.util.FeedUtil
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import com.google.android.apps.nexuslauncher.graphics.IcuDateTextView
import kotlinx.android.synthetic.main.calendar_event.view.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NAME_SHADOWING")
class CalendarEventProvider(context: Context) : FeedProvider(context) {
    private val calendarDrawable by lazy {
        context.getDrawable(R.drawable.ic_event_black_24dp)!!.tint(
                if (useWhiteText(backgroundColor, context)) Color.WHITE else Color.DKGRAY)
    }
    private val calendarDrawableColoured by lazy {
        context.getDrawable(R.drawable.ic_event_black_24dp)!!
                .tint(FeedAdapter.getOverrideColor(context))
    }
    private val events = mutableListOf<CalendarManager.CalendarEvent>()
    private val ongoingEvents = mutableListOf<CalendarManager.CalendarEvent>()

    init {
        CalendarManager.subscribe {
            events.clear()
            events += it.filter {
                it.startTime >= LocalDateTime.now() &&
                        it.startTime <= LocalDateTime.now()
                        .plusDays(context.lawnchairPrefs.feedCalendarEventThreshold.toLong())
            }
            d("init: events are $events")
            ongoingEvents.clear()
            ongoingEvents += it.filter {
                it.startTime <= LocalDateTime.now() &&
                        it.endTime >= LocalDateTime.now()
            }
        }

        TickManager.subscribe {
            CalendarScope.launch {
                events.clear()
                events += events.filter {
                    it.startTime >= LocalDateTime.now() &&
                            it.startTime <= LocalDateTime.now()
                            .plusDays(context.lawnchairPrefs.feedCalendarEventThreshold.toLong())
                }
                d("init: events are $events")
                ongoingEvents.clear()
                ongoingEvents += ongoingEvents.filter {
                    it.startTime <= LocalDateTime.now() &&
                            it.endTime >= LocalDateTime.now()
                }
            }
        }
    }

    override fun isVolatile(): Boolean {
        return true
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
        d("getCards: retrieving calendar cards...")
        if (context.checkSelfPermission(
                        android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return emptyList()
        }
        val cards = ArrayList<Card>()
        run {
            cards.addAll(events.map {
                Card(
                        null, null,
                        object : Card.Companion.InflateHelper {
                            override fun inflate(parent: ViewGroup): View {
                                return if (it.address?.isNotEmpty() != false || it.description?.isNotEmpty() != false) getCalendarFeedView(
                                        it.description, it.address, parent.context, parent,
                                        this@CalendarEventProvider).apply {
                                    calendar_event_title.text =
                                            (if (it.title.trim().isEmpty()) context.getString(
                                                    R.string.placeholder_empty_title) else it.title)
                                    if (context.lawnchairPrefs.feedShowCalendarColour && it.colour != null && it.colour != 0) {
                                        calendar_event_title.setTextColor(it.colour)
                                    }
                                    calendar_event_title.setTypeface(Typeface.DEFAULT_BOLD)
                                    TickManager.subscribe {
                                        val diff = it.startTime.toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(Instant.now())) * 1000 - System.currentTimeMillis()
                                        val diffSeconds = diff / 1000
                                        val diffMinutes = diff / (60 * 1000)
                                        val diffHours = diff / (60 * 60 * 1000)
                                        val diffDays = diff / (24 * 60 * 60 * 1000)
                                        val text: String
                                        if (diffDays > 20) {
                                            text = IcuDateTextView.getDateFormat(context, true,
                                                    null, false)
                                                    .format(Date.from(Instant.ofEpochMilli(
                                                            it.startTime.toEpochSecond(
                                                                    ZoneOffset.ofHours(0)) * 1000)))
                                        } else if (diffDays >= 1) {
                                            text =
                                                    if (diffDays < 1 || diffDays > 1) context.getString(
                                                            R.string.title_text_calendar_feed_provider_in_d_days,
                                                            diffDays) else context.getString(
                                                            R.string.tomorrow)
                                        } else if (diffHours > 4) {
                                            text = context
                                                    .getString(
                                                            R.string.title_text_calendar_feed_in_d_hours,
                                                            diffHours)
                                        } else {
                                            text = if (diffMinutes <= 0) context.getString(
                                                    R.string.reusable_str_now) else context.getString(
                                                    if (diffMinutes < 1 || diffMinutes > 1) R.string.subtitle_smartspace_in_minutes else R.string.subtitle_smartspace_in_minute,
                                                    diffMinutes)
                                        }
                                        calendar_event_time_remaining.text = text
                                    }
                                } else View(
                                        parent.getContext())
                            }
                        },
                        if (it.address?.isNotEmpty() != false || it.description?.isNotEmpty() != false) Card.RAISE or
                                Card.NO_HEADER else Card.RAISE or Card.TEXT_ONLY,
                        if ((it.startTime.toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(Instant.now())) - System.currentTimeMillis() / 1000) / 60 < 120) "nosort,top" else "").apply {
                    globalClickListener = { v ->
                        FeedUtil.startActivity(context, it.intent, v)
                    }
                }
            })
        }
        run {
            val currentTime = GregorianCalendar()
            Log.v(javaClass.name,
                    "getCards: searching for events that are active at ${currentTime}")
            val query =
                    "(( " + CalendarContract.Events.DTSTART + " <= " + currentTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTEND + " >= " + currentTime.getTimeInMillis() + " ))"
            val eventCursorNullable: Cursor? = context.contentResolver
                    .query(CalendarContract.Events.CONTENT_URI,
                            arrayOf(CalendarContract.Instances.TITLE,
                                    CalendarContract.Instances.DTSTART,
                                    CalendarContract.Instances.DTEND,
                                    CalendarContract.Instances.DESCRIPTION,
                                    CalendarContract.Instances.ALL_DAY,
                                    CalendarContract.Events._ID),
                            query, null, CalendarContract.Instances.DTSTART + " ASC")
            if (eventCursorNullable == null) {
                Log.v(javaClass.name,
                        "getCards: query is null, probably since there are no events that meet the specified criteria")
                return cards
            }
            cards.addAll(ongoingEvents.map {
                Card(calendarDrawable, it.title.take(25), object : Card.Companion.InflateHelper {
                    override fun inflate(parent: ViewGroup): View {
                        return View(parent.context)
                    }
                }, Card.TEXT_ONLY, "nosort,top")
            })
        }
        return cards
    }

    override fun getActions(exclusive: Boolean): List<Action> {
        return listOf(
                Action((if (exclusive) R.drawable.ic_add else R.drawable.ic_event_black_24dp).fromDrawableRes(
                        context),
                        context.getString(
                                R.string.title_action_new_calendar_event), Runnable {
                    val intent = Intent(Intent.ACTION_INSERT)
                    intent.setPackage("com.android.calendar")
                    intent.data = CalendarContract.Events.CONTENT_URI
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        val intent = Intent.makeMainSelectorActivity(Intent.ACTION_INSERT,
                                Intent.CATEGORY_APP_CALENDAR)
                        intent.data = CalendarContract.Events.CONTENT_URI
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {

                        }
                    }
                }))
    }
}
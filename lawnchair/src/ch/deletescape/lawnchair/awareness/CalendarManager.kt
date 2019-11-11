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

package ch.deletescape.lawnchair.awareness

import android.annotation.AnyThread
import android.annotation.MainThread
import android.annotation.WorkerThread
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CalendarContract
import ch.deletescape.lawnchair.feed.CalendarScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicReference

@SuppressWarnings("Recycle")
object CalendarManager : BroadcastReceiver() {
    private val context = AtomicReference<Context>()
    private var events = listOf<CalendarEvent>()
        set(value) {
            synchronized(eventChangeListeners) {
                eventChangeListeners.forEach { it(value) }
            }
            field = value
        }
    private val eventChangeListeners = mutableListOf<(events: List<CalendarEvent>) -> Unit>()

    @AnyThread
    fun subscribe(@WorkerThread listener: (events: List<CalendarEvent>) -> Unit) {
        TickManager.subscribe {
            CalendarScope.launch {
                listener.invoke(events)
            }
        }
        synchronized(eventChangeListeners) {
            eventChangeListeners += listener
        }
    }

    @MainThread
    fun attachToContext(context: Context) {
        this.context.set(context)
        context.registerReceiver(this, IntentFilter().apply {
            addAction(Intent.ACTION_PROVIDER_CHANGED)
            addDataScheme("content")
            addDataAuthority("com.android.calendar", null)
        })

        CalendarScope.launch {
            if (context.checkSelfPermission(
                            android.Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                val cursor = context.contentResolver.query(CalendarContract.Events.CONTENT_URI,
                        arrayOf(CalendarContract.Events.TITLE,
                                CalendarContract.Events.EVENT_LOCATION,
                                CalendarContract.Events.DESCRIPTION,
                                CalendarContract.Events.DTSTART,
                                CalendarContract.Events.DTEND,
                                CalendarContract.Events.EVENT_COLOR,
                                CalendarContract.Events._ID), null, null,
                        CalendarContract.Instances.DTSTART + " ASC")!!
                val events = mutableListOf<CalendarEvent>()
                while (cursor.moveToNext()) {
                    events.add(CalendarEvent(cursor.getString(0), cursor.getString(1),
                            cursor.getString(2), LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(cursor.getLong(3)), ZoneId.systemDefault()),
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(cursor.getLong(4)),
                                    ZoneId.systemDefault()),
                            cursor.getInt(5), Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("content://com.android.calendar/events/" + cursor.getLong(
                                6))
                    }))
                }
                this@CalendarManager.events = events
                cursor.close()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        CalendarScope.launch {
            if (context.checkSelfPermission(
                            android.Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                val cursor = context.contentResolver.query(CalendarContract.Events.CONTENT_URI,
                        arrayOf(CalendarContract.Events.TITLE,
                                CalendarContract.Events.EVENT_LOCATION,
                                CalendarContract.Events.DESCRIPTION,
                                CalendarContract.Events.DTSTART,
                                CalendarContract.Events.DTEND,
                                CalendarContract.Events.EVENT_COLOR,
                                CalendarContract.Events._ID), null, null,
                        CalendarContract.Instances.DTSTART + " ASC")!!
                val events = mutableListOf<CalendarEvent>()
                while (cursor.moveToNext()) {
                    events.add(CalendarEvent(cursor.getString(0), cursor.getString(1),
                            cursor.getString(2), LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(cursor.getLong(3)), ZoneId.systemDefault()),
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(cursor.getLong(4)),
                                    ZoneId.systemDefault()),
                            cursor.getInt(5), Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("content://com.android.calendar/events/" + cursor.getLong(
                                6))
                    }))
                }
                this@CalendarManager.events = events
                cursor.close()
            }
        }
    }

    data class CalendarEvent(val title: String, val address: String?, val description: String?,
                             val startTime: LocalDateTime, val endTime: LocalDateTime,
                             val colour: Int?, val intent: Intent)
}
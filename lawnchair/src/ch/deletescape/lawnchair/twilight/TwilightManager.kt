/*
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

package ch.deletescape.lawnchair.twilight

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.ArrayMap
import android.util.Log
import androidx.core.content.ContextCompat
import ch.deletescape.lawnchair.ensureOnMainThread
import ch.deletescape.lawnchair.lawnchairLocationManager
import ch.deletescape.lawnchair.useApplicationContext
import ch.deletescape.lawnchair.util.SingletonHolder
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.BuildConfig
import net.time4j.PlainDate
import net.time4j.calendar.astro.SolarTime
import net.time4j.engine.CalendarDays

@SuppressLint("MissingPermission")
class TwilightManager(private val context: Context) : Handler.Callback, (Double, Double) -> Unit {

    private val handler = Handler(Looper.getMainLooper(), this)

    private val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)!!
    private val locationManager = context.lawnchairLocationManager

    private val listeners = ArrayMap<TwilightListener, Handler>()
    private var hasListeners = false

    private var timeChangedReceiver: BroadcastReceiver? = null
    private var lastLocation: Pair<Double, Double>? = null

    var lastTwilightState: TwilightState? =
            calculateTwilightState(null, null, System.currentTimeMillis())
        get() = synchronized(listeners) { field }
        private set(value) {
            synchronized(listeners) {
                if (field != value) {
                    field = value

                    for (i in listeners.size - 1 downTo 0) {
                        val listener = listeners.keyAt(i)
                        val handler = listeners.valueAt(i)
                        handler.post { listener.onTwilightStateChanged(value) }
                    }
                }
            }
        }

    private val updateIntent = Intent(ACTION_UPDATE_TWILIGHT)
            .setPackage(BuildConfig.APPLICATION_ID)

    init {
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                d("onAlarm")
                updateTwilightState()
            }
        }, IntentFilter(ACTION_UPDATE_TWILIGHT))
    }

    fun registerListener(listener: TwilightListener,
                         handler: Handler) {
        synchronized(listeners) {
            val wasEmpty = listeners.isEmpty()
            listeners[listener] = handler

            if (wasEmpty && !listeners.isEmpty()) {
                this.handler.sendEmptyMessage(MSG_START_LISTENING)
            }
        }
    }

    fun unregisterListener(listener: TwilightListener) {
        synchronized(listeners) {
            val wasEmpty = listeners.isEmpty()
            listeners.remove(listener)

            if (!wasEmpty && listeners.isEmpty()) {
                handler.sendEmptyMessage(MSG_STOP_LISTENING)
            }
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_START_LISTENING -> {
                if (!hasListeners) {
                    hasListeners = true
                    startListening()
                }
                return true
            }
            MSG_STOP_LISTENING -> {
                if (hasListeners) {
                    hasListeners = false
                    stopListening()
                }
            }
        }
        return false
    }

    private fun startListening() {
        Log.d(TAG, "startListening")

        // Update whenever the system clock is changed.
        if (timeChangedReceiver == null) {
            timeChangedReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    Log.d(TAG, "onReceive: $intent")
                    updateTwilightState()
                }
            }

            val intentFilter = IntentFilter(Intent.ACTION_TIME_CHANGED)
            intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
            context.registerReceiver(timeChangedReceiver, intentFilter)
        }

        // Force an update now that we have listeners registered.
        updateTwilightState()
    }

    private fun stopListening() {
        Log.d(TAG, "stopListening")

        if (timeChangedReceiver != null) {
            context.unregisterReceiver(timeChangedReceiver)
            timeChangedReceiver = null
        }

        if (lastTwilightState != null) {
            alarmManager.cancel(PendingIntent.getBroadcast(context, 0, updateIntent, 0))
        }

        locationManager.changeCallbacks -= this
        lastLocation = null
    }

    private fun updateTwilightState() {
        // Calculate the twilight state based on the current time and location.
        val currentTimeMillis = System.currentTimeMillis()
        val location = lastLocation ?: locationManager.location
        val state = calculateTwilightState(location?.first, location?.second, currentTimeMillis)
        Log.d(TAG, "updateTwilightState: $state")

        lastTwilightState = state

        // Schedule an alarm to update the state at the next sunrise or sunset.
        if (state != null) {
            val triggerAtMillis =
                    if (state.isNight) state.sunriseTimeMillis else state.sunsetTimeMillis
            alarmManager.setExact(AlarmManager.RTC, triggerAtMillis,
                    PendingIntent.getBroadcast(context, 0, updateIntent, 0))
        }
    }

    override fun invoke(p1: Double, p2: Double) {
        lastLocation = p1 to p2
        updateTwilightState()
    }

    companion object : SingletonHolder<TwilightManager, Context>(
            ensureOnMainThread(useApplicationContext(::TwilightManager))) {

        private const val TAG = "TwilightManager"

        private const val ACTION_UPDATE_TWILIGHT =
                "${BuildConfig.APPLICATION_ID}.action.UPDATE_TWILIGHT"

        private const val MSG_START_LISTENING = 1
        private const val MSG_STOP_LISTENING = 2

        fun calculateTwilightState(latitude: Double?, longitude: Double?,
                                   timeMillis: Long): TwilightState? {
            if (latitude == null || longitude == null) {
                return null
            }
            val solarTime = SolarTime.ofLocation(latitude, longitude)
            val sunrise = solarTime.sunrise()
            val sunset = solarTime.sunset()
            val sunriseMillis = PlainDate.nowInSystemTime().get(
                    sunrise).inLocalView().toMoment().posixTime * 1000
            val sunsetMillis = PlainDate.nowInSystemTime().get(
                    sunset).inLocalView().toMoment().posixTime * 1000

            val adjustedSunrise = if (sunriseMillis < timeMillis) PlainDate.nowInSystemTime().plus(
                    CalendarDays.ONE).get(sunrise).inLocalView().posixTime * 1000 else sunriseMillis
            // TODO kt-utils.el automated boolean negation
            val adjustedSunset =
                    if (!(sunriseMillis < timeMillis)) PlainDate.nowInSystemTime().plus(
                            CalendarDays.ONE).get(
                            sunset).inLocalView().posixTime * 1000 else sunsetMillis

            return TwilightState(adjustedSunrise, adjustedSunset)
        }
    }
}

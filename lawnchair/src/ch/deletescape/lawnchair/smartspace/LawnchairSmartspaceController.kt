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

package ch.deletescape.lawnchair.smartspace

import android.app.Notification
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.Keep
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.settings.ui.SettingsActivity
import ch.deletescape.lawnchair.settings.ui.SettingsActivity.NOTIFICATION_BADGING
import ch.deletescape.lawnchair.settings.ui.SettingsActivity.SubSettingsFragment.CONTENT_RES_ID
import ch.deletescape.lawnchair.settings.ui.SettingsActivity.SubSettingsFragment.TITLE
import ch.deletescape.lawnchair.shade.CompatSmartspaceProvider
import ch.deletescape.lawnchair.smartspace.weather.WeatherbitDataProvider
import ch.deletescape.lawnchair.smartspace.weather.forecast.SmartspaceForecastProvider
import ch.deletescape.lawnchair.smartspace.weather.owm.OWMWeatherDataProvider
import ch.deletescape.lawnchair.smartspace.weather.weathercom.WeatherChannelWeatherProvider
import ch.deletescape.lawnchair.util.Temperature
import ch.deletescape.lawnchair.util.hasFlag
import com.android.launcher3.Launcher
import com.android.launcher3.R
import com.android.launcher3.Utilities
import com.android.launcher3.notification.NotificationListener
import java.util.concurrent.TimeUnit
import kotlin.collections.set

class LawnchairSmartspaceController(val context: Context) {

    var weatherData: WeatherData? = null
    var cardData: CardData? = null
    private val listeners = ArrayList<Listener>()
    private val weatherProviderPref = Utilities.getLawnchairPrefs(context)::weatherProvider
    private val eventProvidersPref = context.lawnchairPrefs.eventProviders
    private var weatherDataProvider = BlankDataProvider(this) as DataProvider
    private val eventDataProviders = mutableListOf<DataProvider>()
    private val eventDataMap = mutableMapOf<DataProvider, CardData?>()

    private val stockProviderClasses = listOf(OnboardingProvider::class.java)
    private val stockProviders = mutableListOf<DataProvider>()

    var requiresSetup = false

    init {
        onProviderChanged()
        initStockProviders()
    }

    fun startSetup(onFinish: () -> Unit) {
        if (!requiresSetup) {
            onFinish()
            return
        }

        val provider = (listOf(weatherDataProvider) + eventDataProviders)
                .firstOrNull { !it.listening && it.requiresSetup() }

        if (provider != null) {
            provider.startSetup { success ->
                if (success) {
                    provider.startListening()
                    provider.forceUpdate()
                    forceUpdate()
                } else {
                    if (weatherDataProvider == provider) {
                        weatherProviderPref.set(BlankDataProvider::class.java.name)
                    }
                    if (eventDataProviders.contains(provider)) {
                        eventProvidersPref.setAll(eventDataProviders
                                                          .filter { it != provider }
                                                          .map { it::class.java.name })
                    }
                    onProviderChanged()
                }
            }
        } else {
            requiresSetup = false
            onFinish()
        }
    }

    private fun updateWeatherData(weather: WeatherData?) {
        updateData(weather, cardData)
    }

    private fun updateCardData(provider: DataProvider, card: CardData?) {
        eventDataMap[provider] = card
        forceUpdate()
    }

    private fun updateData(weather: WeatherData?, card: CardData?) {
        weatherData = weather
        cardData = card
        notifyListeners()
    }

    fun forceUpdate() {
        val allProviders = stockProviders.asSequence() + eventDataProviders.asSequence()
        val eventData = allProviders.mapNotNull { eventDataMap[it] }.firstOrNull()
        updateData(weatherData, eventData)
    }

    fun forceUpdateWeather() {
        weatherDataProvider.forceUpdate()
    }

    private fun notifyListeners() {
        listeners.forEach { it.onDataUpdated(weatherData, cardData) }
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
        listener.onDataUpdated(weatherData, cardData)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun onProviderChanged() {
        runOnUiWorkerThread {
            val weatherClass = weatherProviderPref.get()
            val eventClasses = eventProvidersPref.getAll()
            if (weatherClass == weatherDataProvider::class.java.name && eventClasses == eventDataProviders.map {
                        it::class.java.name
                    }) {
                runOnMainThread(::forceUpdate)
                return@runOnUiWorkerThread
            }

            val activeProviders = eventDataProviders + weatherDataProvider
            val providerCache =
                    activeProviders.associateByTo(mutableMapOf()) { it::class.java.name }
            val getProvider = { name: String ->
                providerCache.getOrPut(name) { createDataProvider(name) }
            }

            // Load all providers
            weatherDataProvider = getProvider(weatherClass)
            weatherDataProvider.weatherUpdateListener = ::updateWeatherData
            eventDataProviders.clear()
            eventClasses.map { getProvider(it) }
                    .filterTo(eventDataProviders) { it !is BlankDataProvider }
                    .forEach { it.cardUpdateListener = ::updateCardData }

            val allProviders = providerCache.values.toSet()
            val newProviders = setOf(weatherDataProvider) + eventDataProviders
            val needsDestroy = allProviders - newProviders
            val needsUpdate = newProviders - activeProviders

            needsDestroy.forEach {
                eventDataMap.remove(it)
                if (it.listening) {
                    it.stopListening()
                }
            }

            weatherProviderPref.set(weatherDataProvider::class.java.name)
            eventProvidersPref.setAll(eventDataProviders.map { it::class.java.name })

            needsUpdate.forEach {
                if (!it.requiresSetup()) {
                    it.startListening()
                    it.forceUpdate()
                }
            }
            requiresSetup = newProviders.any { !it.listening && it.requiresSetup() }
            runOnMainThread { forceUpdate() }
        }
    }

    private fun initStockProviders() {
        val providers = mutableListOf<DataProvider>()
        stockProviderClasses
                .map { createDataProvider(it.name) }
                .filterTo(providers) { it !is BlankDataProvider }
                .forEach { it.cardUpdateListener = ::updateCardData }

        stockProviders.addAll(providers)
        providers.forEach { it.forceUpdate() }
        forceUpdate()
    }

    fun updateWeatherData() {
        weatherDataProvider.forceUpdate()
    }

    fun openWeather(v: View) {
        val data = weatherData ?: return
        val launcher = Launcher.getLauncher(v.context)
        if (weatherData!!.forecastIntent != null) {
            launcher.startActivitySafely(v, weatherData!!.forecastIntent, null)
        } else {
            Utilities.openURLinBrowser(launcher, weatherData!!.forecastUrl,
                                       launcher.getViewBounds(v),
                                       launcher.getActivityLaunchOptions(v).toBundle())
        }
    }

    private fun createDataProvider(className: String): DataProvider {
        return try {
            (Class.forName(className).getConstructor(LawnchairSmartspaceController::class.java)
                    .newInstance(this) as DataProvider)
        } catch (t: Throwable) {
            Log.d("LSC", "couldn't create provider", t)
            BlankDataProvider(this)
        }
    }

    abstract class DataProvider(val controller: LawnchairSmartspaceController) {
        var listening = false
            private set

        var weatherUpdateListener: ((WeatherData?) -> Unit)? = null
        var cardUpdateListener: ((DataProvider, CardData?) -> Unit)? = null

        private var currentWeather: WeatherData? = null
        private var currentCard: CardData? = null

        protected val context = controller.context
        protected val resources = context.resources

        open fun requiresSetup() = false

        open fun startSetup(onFinish: (Boolean) -> Unit) {
            onFinish(true)
        }

        open fun startListening() {
            listening = true
        }

        open fun stopListening() {
            listening = false
            weatherUpdateListener = null
            cardUpdateListener = null
        }

        fun updateData(weather: WeatherData?, card: CardData?) = runOnMainThread {
            currentWeather = weather
            currentCard = card
            weatherUpdateListener?.invoke(weather)
            cardUpdateListener?.invoke(this, card)
        }

        open fun forceUpdate() {
            if (currentWeather != null || currentCard != null) {
                updateData(currentWeather, currentCard)
            }
        }

        protected fun getApp(name: String): CharSequence {
            val pm = controller.context.packageManager
            try {
                return pm.getApplicationLabel(
                        pm.getApplicationInfo(name, PackageManager.GET_META_DATA))
            } catch (ignored: PackageManager.NameNotFoundException) {
            }

            return name
        }

        protected fun getApp(sbn: StatusBarNotification): CharSequence {
            val context = controller.context
            val subName = sbn.notification.extras.getString(EXTRA_SUBSTITUTE_APP_NAME)
            if (subName != null) {
                if (context.checkPackagePermission(sbn.packageName, PERM_SUBSTITUTE_APP_NAME)) {
                    return subName
                }
            }
            return getApp(sbn.packageName)
        }

        companion object {

            private const val PERM_SUBSTITUTE_APP_NAME =
                    "android.permission.SUBSTITUTE_NOTIFICATION_APP_NAME"
            private const val EXTRA_SUBSTITUTE_APP_NAME = "android.substName"
        }
    }

    abstract class PeriodicDataProvider(controller: LawnchairSmartspaceController) :
            DataProvider(controller) {

        private val handlerThread =
                HandlerThread(this::class.java.simpleName).apply { if (!isAlive) start() }
        private val handler = Handler(handlerThread.looper)
        private val update = ::periodicUpdate

        open val timeout = TimeUnit.MINUTES.toMillis(30)

        override fun startListening() {
            super.startListening()
            handler.post(update)
        }

        private fun periodicUpdate() {
            try {
                updateData()
            } catch (e: Exception) {
                Log.d("PeriodicDataProvider", "failed to update data", e)
            }
            handler.postDelayed(update, timeout)
        }

        override fun stopListening() {
            super.stopListening()
            handlerThread.quit()
        }

        protected fun updateNow() {
            handler.removeCallbacks(update)
            handler.post(update)
        }

        open fun updateData() {
            updateData(queryWeatherData(), queryCardData())
        }

        open fun queryWeatherData(): WeatherData? {
            return null
        }

        open fun queryCardData(): CardData? {
            return null
        }

        override fun forceUpdate() {
            super.forceUpdate()
            updateNow()
        }
    }

    abstract class NotificationBasedDataProvider(controller: LawnchairSmartspaceController) :
            DataProvider(controller) {

        override fun startSetup(onFinish: (Boolean) -> Unit) {
            if (checkNotificationAccess()) {
                onFinish(true)
                return
            }

            val context = controller.context
            val providerName = getDisplayName(this::class.java.name)
            val intent: Intent
            val msg: String
            if (Utilities.ATLEAST_OREO) {
                intent = Intent(context, SettingsActivity::class.java)
                        .putExtra(SettingsActivity.EXTRA_FRAGMENT_ARG_KEY, "pref_icon_badging")
                        .putExtra(TITLE, context.getString(R.string.general_pref_title))
                        .putExtra(CONTENT_RES_ID, R.xml.lawnchair_desktop_preferences)
                msg = context.getString(R.string.event_provider_missing_notification_dots,
                                        context.getString(providerName))
            } else {
                val cn = ComponentName(context, NotificationListener::class.java)
                intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(":settings:fragment_args_key", cn.flattenToString())
                msg = context.getString(R.string.event_provider_missing_notification_access,
                                        context.getString(providerName),
                                        context.getString(R.string.derived_app_name))
            }
            BlankActivity.startActivityWithDialog(
                    context, intent, 1030,
                    context.getString(R.string.title_missing_notification_access),
                    msg,
                    context.getString(R.string.title_change_settings)) {
                onFinish(checkNotificationAccess())
            }
        }

        private fun checkNotificationAccess(): Boolean {
            val context = controller.context
            val enabledListeners = Settings.Secure
                    .getString(context.contentResolver, "enabled_notification_listeners")
            val myListener = ComponentName(context, NotificationListener::class.java)
            val listenerEnabled = enabledListeners?.let {
                it.contains(myListener.flattenToString()) || it.contains(
                        myListener.flattenToString())
            } ?: false
            val badgingEnabled =
                    Settings.Secure.getInt(context.contentResolver, NOTIFICATION_BADGING, 1) == 1
            return listenerEnabled && badgingEnabled
        }

        override fun requiresSetup() = !checkNotificationAccess()
    }

    data class WeatherData(val icon: Bitmap, val temperature: Temperature,
                           val forecastUrl: String? = "https://duckduckgo.com/search?q=weather",
                           val forecastIntent: Intent? = null,
                           val pendingIntent: PendingIntent? = null, val coordLat: Double? = null,
                           val coordLon: Double? = null, val iconType: String) {

        fun getTitle(unit: Temperature.Unit): String {
            return "${temperature.inUnit(unit)} ${unit.suffix}"
        }
    }

    data class CardData(val icon: Bitmap? = null, val lines: List<Line>,
                        val onClickListener: View.OnClickListener? = null,
                        val forceSingleLine: Boolean = false) {

        constructor(icon: Bitmap? = null, lines: List<Line>, intent: PendingIntent? = null,
                    forceSingleLine: Boolean = false) : this(icon, lines, intent?.let {
            PendingIntentClickListener(it)
        }, forceSingleLine)

        constructor(icon: Bitmap? = null, lines: List<Line>,
                    forceSingleLine: Boolean = false) : this(icon, lines,
                                                             null as View.OnClickListener?,
                                                             forceSingleLine)

        constructor(icon: Bitmap?, title: CharSequence,
                    titleEllipsize: TextUtils.TruncateAt? = TextUtils.TruncateAt.END,
                    subtitle: CharSequence,
                    subtitleEllipsize: TextUtils.TruncateAt? = TextUtils.TruncateAt.END,
                    pendingIntent: PendingIntent? = null) : this(icon,
                                                                 listOf(Line(title, titleEllipsize),
                                                                        Line(subtitle,
                                                                             subtitleEllipsize)),
                                                                 pendingIntent)

        val isDoubleLine = !forceSingleLine && lines.size >= 2

        val title: CharSequence?
        val titleEllipsize: TextUtils.TruncateAt?

        val subtitle: CharSequence?
        val subtitleEllipsize: TextUtils.TruncateAt?

        init {
            if (lines.isEmpty()) {
                error("Can't create card with zero lines")
            }
            if (forceSingleLine) {
                title = TextUtils.join(" – ", lines.map { it.text })!!
                titleEllipsize =
                        if (lines.size == 1) lines.first().ellipsize else TextUtils.TruncateAt.END
                subtitle = null
                subtitleEllipsize = null
            } else {
                title = lines.first().text
                titleEllipsize = lines.first().ellipsize
                subtitle = TextUtils.join(" – ", lines.subList(1, lines.size).map { it.text })!!
                subtitleEllipsize =
                        if (lines.size == 2) lines[1].ellipsize else TextUtils.TruncateAt.END
            }
        }
    }

    open class PendingIntentClickListener(private val pendingIntent: PendingIntent) :
            View.OnClickListener {

        override fun onClick(v: View) {
            val launcher = Launcher.getLauncher(v.context)
            val opts = launcher.getActivityLaunchOptionsAsBundle(v)
            try {
                launcher.startIntentSender(pendingIntent.intentSender, null,
                                           Intent.FLAG_ACTIVITY_NEW_TASK,
                                           Intent.FLAG_ACTIVITY_NEW_TASK, 0, opts)
            } catch (e: ActivityNotFoundException) {
                // ignored
            }
        }
    }

    class NotificationClickListener(sbn: StatusBarNotification) :
            PendingIntentClickListener(sbn.notification.contentIntent) {

        private val key = sbn.key
        private val autoCancel = sbn.notification.flags.hasFlag(Notification.FLAG_AUTO_CANCEL)

        override fun onClick(v: View) {
            super.onClick(v)
            if (autoCancel) {
                Launcher.getLauncher(v.context).popupDataProvider.cancelNotification(key)
            }
        }
    }

    data class Line @JvmOverloads constructor(
            val text: CharSequence,
            val ellipsize: TextUtils.TruncateAt? = TextUtils.TruncateAt.END) {

        constructor(context: Context, textRes: Int) : this(context.getString(textRes))
    }

    interface Listener {

        fun onDataUpdated(weather: WeatherData?, card: CardData?)
    }

    companion object {

        private val displayNames =
                mapOf(BlankDataProvider::class.java.name to R.string.weather_provider_disabled,
                      OWMWeatherDataProvider::class.java.name to R.string.weather_provider_owm,
                      AccuWeatherDataProvider::class.java.name to R.string.weather_provider_accu,
                      PEWeatherDataProvider::class.java.name to R.string.weather_provider_pe,
                      NowPlayingProvider::class.java.name to R.string.event_provider_now_playing,
                      NotificationUnreadProvider::class.java.name to R.string.event_provider_unread_notifications,
                      BatteryStatusProvider::class.java.name to R.string.battery_status,
                      AlarmEventProvider::class.java.name to R.string.name_provider_alarm_events,
                      BuiltInCalendarProvider::class.java.name to R.string.provider_built_in_calendar_title,
                      FakeDataProvider::class.java.name to R.string.weather_provider_testing,
                      PersonalityProvider::class.java.name to R.string.personality_provider,
                      OnboardingProvider::class.java.name to R.string.onbording,
                      SmartspaceForecastProvider::class.java.name to R.string.title_smartspace_provider_forecast,
                      WeatherChannelWeatherProvider::class.java.name to R.string.title_weather_provider_weather_com,
                      WeatherbitDataProvider::class.java.name to R.string.title_weather_provider_weatherbit_data,
                      UnifiedWeatherDataProvider::class.java.name to R.string.title_weather_provider_unified,
                      DailyBriefingProvider::class.java.name to R.string.title_smartspace_provider_daily_briefing,
                      OngoingEventsProvider::class.java.name to R.string.title_smartspace_provider_ongoing_events,
                      CompatSmartspaceProvider::class.java.name to R.string.title_smartspace_provider_shade_compat)

        fun getDisplayName(providerName: String): Int {
            return displayNames[providerName] ?: error("No display name for provider $providerName")
        }

        fun getDisplayName(context: Context, providerName: String): String {
            return context.getString(getDisplayName(providerName))
        }
    }
}

@Keep
class BlankDataProvider(controller: LawnchairSmartspaceController) :
        LawnchairSmartspaceController.DataProvider(controller) {

    override fun startListening() {
        super.startListening()

        updateData(null, null)
    }
}

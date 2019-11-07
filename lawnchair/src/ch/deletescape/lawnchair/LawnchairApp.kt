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

package ch.deletescape.lawnchair

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.Settings
import android.webkit.WebView
import androidx.annotation.Keep
import ch.deletescape.lawnchair.awareness.TickManager
import ch.deletescape.lawnchair.awareness.VolumeManager
import ch.deletescape.lawnchair.awareness.WeatherManager
import ch.deletescape.lawnchair.blur.BlurWallpaperProvider
import ch.deletescape.lawnchair.bugreport.BugReportClient
import ch.deletescape.lawnchair.bugreport.BugReportService
import ch.deletescape.lawnchair.clipart.ClipartCache
import ch.deletescape.lawnchair.clipart.FancyClipartResolver
import ch.deletescape.lawnchair.clipart.ResourceClipartResolver
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.feed.adblock.WebSafety
import ch.deletescape.lawnchair.feed.chips.ChipStyleRegistry
import ch.deletescape.lawnchair.feed.dynamic.DynamicProviderController
import ch.deletescape.lawnchair.feed.getFeedController
import ch.deletescape.lawnchair.feed.shape.CardStyleRegistry
import ch.deletescape.lawnchair.feed.widgets.OverlayWidgetHost
import ch.deletescape.lawnchair.flowerpot.Flowerpot
import ch.deletescape.lawnchair.persistence.feedPrefs
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController
import ch.deletescape.lawnchair.theme.ThemeManager
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.BuildConfig
import com.android.launcher3.Utilities
import com.android.quickstep.RecentsActivity
import com.squareup.leakcanary.LeakCanary
import geocode.GeocoderCompat
import kg.net.bazi.gsb4j.Gsb4j
import kotlinx.coroutines.launch
import me.weishu.reflection.Reflection
import net.time4j.android.ApplicationStarter
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.HashSet

class LawnchairApp : Application() {
    val weatherLooper = Handler(HandlerThread("weather-1", Thread.NORM_PRIORITY).also { it.start() }.looper)
    val activityHandler = ActivityHandler()
    val smartspace by lazy { LawnchairSmartspaceController(this) }
    val bugReporter = LawnchairBugReporter(this, Thread.getDefaultUncaughtExceptionHandler())
    val recentsEnabled by lazy { checkRecentsComponent() }
    var accessibilityService: LawnchairAccessibilityService? = null
    val feedController by lazy { getFeedController(this) }
    val geocoder by lazy { GeocoderCompat(this, true) }
    lateinit var gsb4j: Gsb4j

    lateinit var overlayWidgetHost: OverlayWidgetHost

    init {
        d("Hidden APIs allowed: ${Utilities.HIDDEN_APIS_ALLOWED}")
    }

    fun isGsb4jAvailable() = ::gsb4j.isInitialized && feedPrefs.enableGsb

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Reflection.unseal(base)
        Utilities.HIDDEN_APIS_ALLOWED = !Utilities.ATLEAST_P || HiddenApiCompat.checkIfAllowed()
        d("Hidden APIs allowed after unseal: ${Utilities.HIDDEN_APIS_ALLOWED}")
    }

    override fun onCreate() {
        super.onCreate()
        ApplicationStarter.initialize(this, true)
        FeedScope.launch {
            if (Utilities.ATLEAST_P && feedPrefs.enableGsb) {
                try {
                    gsb4j = Gsb4j.bootstrap(Properties().apply {
                        put("api.key", WebSafety.GSB_API_KEY)
                        put("data.dir", cacheDir.absolutePath)
                    })
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
            }
        }
        localizationContext = this
        ch.deletescape.lawnchair.location.LocationManager.location
        d("Current process: " + getCurrentProcessName(this))
        if (getCurrentProcessName(this).contains("overlay")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WebView.setDataDirectorySuffix("_overlay")
            }
            Thread.setDefaultUncaughtExceptionHandler(bugReporter)
        }
        if (lawnchairPrefs.leakCanary) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return
            }
            LeakCanary.install(this)
        }
        overlayWidgetHost = OverlayWidgetHost(this, 1027)
                .also { it.startListening() }
        ClipartCache.providers += ResourceClipartResolver(this)
        ClipartCache.providers += FancyClipartResolver(this)

        org.osmdroid.config.Configuration.getInstance().osmdroidBasePath =
                File(filesDir, "osmdroid")
        org.osmdroid.config.Configuration.getInstance().userAgentValue =
                "Librechair-" + BuildConfig.VERSION_CODE
        if (getCurrentProcessName(this).contains("overlay")) {
            WebSafety.context = this
            FeedScope.launch {
                try {
                    WebSafety.initialize()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        DynamicProviderController.attachContext(this)
        ChipStyleRegistry.populateWithContext(this)
        WeatherManager.attachToApplication(this)
        TickManager.bindToContext(this)
        CardStyleRegistry.populateWithContext(this)
        VolumeManager.attachToContext(this)
        VolumeManager.subscribe { d("onCreate: volume changed to $it") }
    }

    fun onLauncherAppStateCreated() {
        Thread.setDefaultUncaughtExceptionHandler(bugReporter)
        registerActivityLifecycleCallbacks(activityHandler)

        ThemeManager.getInstance(this).registerColorListener()
        BlurWallpaperProvider.getInstance(this)
        Flowerpot.Manager.getInstance(this)
        if (lawnchairPrefs.showCrashNotifications) {
            BugReportClient.getInstance(this)
            BugReportService.registerNotificationChannel(this)
        }
    }

    fun restart(recreateLauncher: Boolean = true) {
        if (recreateLauncher) {
            activityHandler.finishAll(recreateLauncher)
        } else {
            Utilities.restartLauncher(this)
        }
    }

    fun performGlobalAction(action: Int): Boolean {
        return if (accessibilityService != null) {
            accessibilityService!!.performGlobalAction(action)
        } else {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK))
            false
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ThemeManager.getInstance(this).updateNightMode(newConfig)
    }

    class ActivityHandler : ActivityLifecycleCallbacks {

        val activities = HashSet<Activity>()
        var foregroundActivity: Activity? = null

        fun finishAll(recreateLauncher: Boolean = true) {
            HashSet(activities)
                    .forEach { if (recreateLauncher && it is LawnchairLauncher) it.recreate() else it.finish() }
        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityResumed(activity: Activity) {
            foregroundActivity = activity
        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            if (activity == foregroundActivity) foregroundActivity = null
            activities.remove(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activities.add(activity)
        }
    }

    @Keep
    fun checkRecentsComponent(): Boolean {
        if (!Utilities.ATLEAST_P) {
            d("API < P, disabling recents")
            return false
        }
        if (!Utilities.HIDDEN_APIS_ALLOWED) {
            d("Hidden APIs not allowed, disabling recents")
            return false
        }

        val resId = resources.getIdentifier("config_recentsComponentName", "string", "android")
        if (resId == 0) {
            d("config_recentsComponentName not found, disabling recents")
            return false
        }
        val recentsComponent = ComponentName.unflattenFromString(resources.getString(resId))
        if (recentsComponent == null) {
            d("config_recentsComponentName is  empty, disabling recents")
            return false
        }
        val isRecentsComponent =
                recentsComponent.packageName == packageName && recentsComponent.className == RecentsActivity::class.java.name
        if (!isRecentsComponent) {
            d("config_recentsComponentName ($recentsComponent) is not Lawnchair, disabling recents")
            return false
        }
        return true
    }

    companion object {
        lateinit var localizationContext: Context
    }
}

val Context.lawnchairApp get() = applicationContext as LawnchairApp

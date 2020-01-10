/*     Copyright © 2018-2019 the Lawnchair team
 *     Copyright © 2019 oldosfan (Would on Telegram)
 *
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

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import ch.deletescape.lawnchair.bugreport.BugReportClient
import ch.deletescape.lawnchair.colors.ColorEngine
import ch.deletescape.lawnchair.colors.resolvers.OLEDBlackColorResolver
import ch.deletescape.lawnchair.feed.*
import ch.deletescape.lawnchair.feed.AlarmEventProvider
import ch.deletescape.lawnchair.feed.anim.DefaultFeedTransitionDelegate
import ch.deletescape.lawnchair.feed.images.providers.ImageProvider
import ch.deletescape.lawnchair.feed.images.providers.ImageProviderContainer
import ch.deletescape.lawnchair.feed.tabs.CustomTab
import ch.deletescape.lawnchair.feed.tabs.TabController
import ch.deletescape.lawnchair.feed.tabs.colors.ColorProvider
import ch.deletescape.lawnchair.feed.tabs.indicator.TabIndicatorProvider
import ch.deletescape.lawnchair.feed.widgets.FeedWidgetsProvider
import ch.deletescape.lawnchair.gestures.BlankGestureHandler
import ch.deletescape.lawnchair.gestures.handlers.*
import ch.deletescape.lawnchair.globalsearch.SearchProviderController
import ch.deletescape.lawnchair.groups.AppGroupsManager
import ch.deletescape.lawnchair.groups.DrawerTabs
import ch.deletescape.lawnchair.iconpack.IconPackManager
import ch.deletescape.lawnchair.preferences.DockStyle
import ch.deletescape.lawnchair.preferences.TitleAlignmentPreference
import ch.deletescape.lawnchair.settings.GridSize
import ch.deletescape.lawnchair.settings.GridSize2D
import ch.deletescape.lawnchair.settings.ui.SettingsActivity
import ch.deletescape.lawnchair.smartspace.*
import ch.deletescape.lawnchair.theme.ThemeManager
import ch.deletescape.lawnchair.util.Temperature
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.*
import com.android.launcher3.allapps.search.DefaultAppSearchAlgorithm
import com.android.launcher3.util.ComponentKey
import com.android.quickstep.OverviewInteractionState
import com.google.android.apps.nexuslauncher.allapps.PredictionsFloatingHeader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.roundToInt
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSuperclassOf

class LawnchairPreferences(val context: Context) :
        SharedPreferences.OnSharedPreferenceChangeListener {

    private val onChangeMap: MutableMap<String, () -> Unit> = HashMap()
    private val onChangeListeners: MutableMap<String, MutableSet<OnPreferenceChangeListener>> =
            HashMap()
    private var onChangeCallback: LawnchairPreferencesChangeCallback? = null
    var sharedPrefs = migratePrefs()

    fun migratePrefs(): SharedPreferences {
        val dir = context.cacheDir.parent
        val oldFile = File(dir, "shared_prefs/" + LauncherFiles.OLD_SHARED_PREFERENCES_KEY + ".xml")
        val newFile = File(dir, "shared_prefs/" + LauncherFiles.SHARED_PREFERENCES_KEY + ".xml")
        if (oldFile.exists() && !newFile.exists()) {
            oldFile.renameTo(newFile)
            oldFile.delete()
        }
        return context.applicationContext.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY,
                Context.MODE_MULTI_PROCESS)
                .apply {
                    migrateConfig(this)
                }
    }

    fun lbcMigratePrefs() {
        if (sharedPrefs.all["pref_oled_feed_cards"] == true) {
            sharedPrefs.edit().putString(ColorEngine.Resolvers.FEED_CARD,
                    OLEDBlackColorResolver::class.qualifiedName)
                    .putBoolean("pref_oled_feed_cards", false).commit();
        }
        if (sharedPrefs.all["pref_feed_decoration_margin"] is Float) {
            sharedPrefs.edit().putFloat("pref_feed_decoration_margin_vertical",
                    sharedPrefs.all["pref_feed_decoration_margin"] as Float)
                    .putFloat("pref_feed_decoration_margin_horizontal",
                            sharedPrefs.all["pref_feed_decoration_margin"] as Float)
                    .remove("pref_feed_decoration_margin").commit()
        }
    }

    init {
        lbcMigratePrefs()
    }

    val doNothing = { }
    val recreate = { recreate() }
    private val reloadApps = { reloadApps() }
    private val reloadAll = { reloadAll() }
    val restart = { restart() }
    private val refreshGrid = { refreshGrid() }
    private val updateBlur = { updateBlur() }
    private val resetAllApps = { onChangeCallback?.resetAllApps() ?: Unit }
    private val updateSmartspace = { updateSmartspace() }
    private val updateWeatherData = { onChangeCallback?.updateWeatherData() ?: Unit }
    private val reloadIcons = { reloadIcons() }
    private val reloadIconPacks = { IconPackManager.getInstance(context).packList.reloadPacks() }
    private val reloadDockStyle = {
        LauncherAppState.getIDP(context).onDockStyleChanged(this)
        recreate()
    }

    private val lawnchairConfig = LawnchairConfig.getInstance(context)

    var restoreSuccess by BooleanPref("pref_restoreSuccess", false)
    var configVersion by IntPref(VERSION_KEY, if (restoreSuccess) 0 else CURRENT_VERSION)
    // Show a short onboarding "welcome to your new home"
    var migratedFrom1 by BooleanPref("pref_legacyUpgrade", false)

    // Blur
    var enableBlur by BooleanPref("pref_enableBlur", lawnchairConfig.defaultEnableBlur, updateBlur)
    val enableVibrancy = true
    val blurRadius by FloatPref("pref_blurRadius", lawnchairConfig.defaultBlurStrength, updateBlur)

    // Theme
    private var iconPack by StringPref("pref_icon_pack", "", reloadIconPacks)
    val iconPacks = object : MutableListPref<String>("pref_iconPacks", reloadIconPacks,
            if (!TextUtils.isEmpty(iconPack)) listOf(
                    iconPack) else lawnchairConfig.defaultIconPacks.asList()) {

        override fun unflattenValue(value: String) = value
    }
    var launcherTheme by StringIntPref("pref_launcherTheme", 1) {
        ThemeManager.getInstance(context).updateTheme()
    }

    var leakCanary by BooleanPref("pref_initLeakCanary", BuildConfig.HAS_LEAKCANARY, restart);

    val enableLegacyTreatment by BooleanPref("pref_enableLegacyTreatment",
            lawnchairConfig.enableLegacyTreatment, reloadIcons)
    val colorizedLegacyTreatment by BooleanPref("pref_colorizeGeneratedBackgrounds",
            lawnchairConfig.enableColorizedLegacyTreatment,
            reloadIcons)
    var chromiumPackageName by StringPref("pref_chromiumPackageName", "org.chromium.chrome")
    val enableWhiteOnlyTreatment by BooleanPref("pref_enableWhiteOnlyTreatment",
            lawnchairConfig.enableWhiteOnlyTreatment,
            reloadIcons)
    val hideStatusBar by BooleanPref("pref_hideStatusBar", lawnchairConfig.hideStatusBar, doNothing)
    val iconPackMasking by BooleanPref("pref_iconPackMasking", true, reloadIcons)
    val adaptifyIconPacks by BooleanPref("pref_generateAdaptiveForIconPack", false, reloadIcons)
    val displayNotificationCount by BooleanPref("pref_displayNotificationCount", false, reloadAll)
    val showSecretOptions by BooleanPref("pref_showSecretOptions", BuildConfig.DEBUG)

    // Desktop
    val allowFullWidthWidgets by BooleanPref("pref_fullWidthWidgets", false, restart)
    private var gridSizeDelegate = ResettableLazy {
        GridSize2D(this, "numRows", "numColumns", LauncherAppState.getIDP(context), restart)
    }
    val gridSize by gridSizeDelegate
    val hideAppLabels by BooleanPref("pref_hideAppLabels", false, recreate)
    val showTopShadow by BooleanPref("pref_showTopShadow", true,
            recreate) // TODO: update the scrim instead of doing this
    var autoAddInstalled by BooleanPref("pref_add_icon_to_home", true, doNothing)
    private val homeMultilineLabel by BooleanPref("pref_homeIconLabelsInTwoLines", false, recreate)
    val homeLabelRows get() = if (homeMultilineLabel) 2 else 1
    val allowOverlap by BooleanPref(SettingsActivity.ALLOW_OVERLAP_PREF, false, reloadAll)
    val desktopTextScale by FloatPref("pref_iconTextScaleSB", 1f, reloadAll)
    val centerWallpaper by BooleanPref("pref_centerWallpaper")
    val lockDesktop by BooleanPref("pref_lockDesktop", false, reloadAll)
    val usePopupMenuView by BooleanPref("pref_desktopUsePopupMenuView", true, doNothing)

    // Smartspace
    val enableSmartspace by BooleanPref("pref_smartspace", lawnchairConfig.enableSmartspace)
    val smartspaceTime by BooleanPref("pref_smartspace_time", false, refreshGrid)
    val smartspaceTimeAbove by BooleanPref("pref_smartspace_time_above", false, refreshGrid)
    val smartspaceTime24H by BooleanPref("pref_smartspace_time_24_h", false, refreshGrid)
    val smartspaceDate by BooleanPref("pref_smartspace_date", true, refreshGrid)
    var smartspaceWidgetId by IntPref("smartspace_widget_id", -1, doNothing)
    var weatherProvider by StringPref("pref_smartspace_widget_provider",
            UnifiedWeatherDataProvider::class.java.name,
            ::updateSmartspaceProvider)
    var eventProvider by StringPref("pref_smartspace_event_provider",
            BuiltInCalendarProvider::class.java.name,
            ::updateSmartspaceProvider)
    var eventProviders =
            StringListPref("pref_smartspace_event_providers", ::updateSmartspaceProvider,
                    listOf(eventProvider, NotificationUnreadProvider::class.java.name,
                            NowPlayingProvider::class.java.name,
                            BatteryStatusProvider::class.java.name,
                            PersonalityProvider::class.java.name))
    var feedProvidersLegacy = StringListPref("pref_feed_providers", ::restartOverlay, emptyList())
    var feedBlur by BooleanPref("pref_feed_blur", true, ::restartOverlay)
    var feedProvidersLegacy2 = object :
            MutableListPref<FeedProviderContainer>(sharedPrefs, "pref_feed_provider_containers",
                    ::restartOverlay,
                    listOf()) {
        override fun unflattenValue(value: String) = Gson().fromJson(value,
                FeedProviderContainer::class.java).apply {
            if (arguments == null) {
                arguments = Collections.emptyMap();
            }
        }

        override fun flattenValue(value: FeedProviderContainer) = Gson().toJson(value)
    }

    var feedDisabledCards =
            object : MutableListPref<Int>("pref_feed_hidden_cards", ::restart, emptyList()) {
                override fun unflattenValue(value: String): Int {
                    return Integer.valueOf(value)
                }
            }
    val feedCategorizeWidgetsAsSeparateTab by BooleanPref("pref_feed_widgets_tab", true,
            ::restartOverlay)
    val feedOLEDCards by BooleanPref("pref_oled_feed_cards", false, ::restartOverlay)
    var feedRSSSources = StringListPref("pref_rss_sources", ::restartOverlay, emptyList())
    var feedBackgroundOpacity by FloatPref("pref_feed_opacity", 0f, ::restartOverlay)
    var feedCardOpacity by FloatPref("pref_feed_card_opacity", 255f, ::restartOverlay)
    var feedCardElevation by FloatPref("pref_feed_card_elevation", 16f, ::restartOverlay)

    var feedPresenterAlgorithm by StringPref("pref_feed_sorting_algorithm",
            MixerSortingAlgorithm::class.java.name,
            ::restartOverlay)

    var feedCustomBackground by NullableStringPref("pref_feed_custom_background", null,
            ::restartOverlay)
    var remoteFeedProviders by StringSetPref("pref_remote_feed_providers", setOf(),
            ::restartOverlay)

    var feedShowInfobox by BooleanPref("pref_feed_infobox", true, ::restartOverlay)
    var feedCardBlur by BooleanPref("pref_blur_feed_cards", false, ::restartOverlay);
    var swipeForFeed by BooleanPref("pref_swipe_feed", false, ::restart);

    var weatherApiKey by StringPref("pref_weatherApiKey",
            context.getString(R.string.default_owm_key))
    var weatherCity by StringPref("pref_weather_city", context.getString(R.string.default_city))
    val weatherUnit by StringBasedPref("pref_weather_units", Temperature.Unit.Celsius,
            ::updateSmartspaceProvider,
            Temperature.Companion::unitFromString,
            Temperature.Companion::unitToString) { }
    var usePillQsb by BooleanPref("pref_use_pill_qsb", false, recreate)
    var weatherIconPack by StringPref("pref_weatherIcons", "", updateWeatherData)

    // Dock
    val dockStyles = DockStyle.StyleManager(this, reloadDockStyle, resetAllApps)
    val dockColoredGoogle by BooleanPref("pref_dockColoredGoogle", false, doNothing)
    var dockSearchBarPref by BooleanPref("pref_dockSearchBar", Utilities.ATLEAST_MARSHMALLOW,
            recreate)
    inline val dockSearchBar get() = !dockHide && dockSearchBarPref
    val dockRadius get() = dockStyles.currentStyle.radius
    val dockShadow get() = dockStyles.currentStyle.enableShadow
    val dockShowArrow get() = dockStyles.currentStyle.enableArrow
    val dockOpacity get() = dockStyles.currentStyle.opacity
    val dockShowPageIndicator by BooleanPref("pref_hotseatShowPageIndicator", true,
            { onChangeCallback?.updatePageIndicator() })
    val dockGradientStyle get() = dockStyles.currentStyle.enableGradient
    val dockHide get() = dockStyles.currentStyle.hide
    private val dockGridSizeDelegate = ResettableLazy {
        GridSize(this, "numHotseatIcons", LauncherAppState.getIDP(context), restart)
    }
    val dockGridSize by dockGridSizeDelegate
    val twoRowDock by BooleanPref("pref_twoRowDock", false, restart)
    val dockRowsCount get() = if (twoRowDock) 2 else 1
    var dockScale by FloatPref("pref_dockScale", -1f, recreate)
    val hideDockLabels by BooleanPref("pref_hideDockLabels", true, restart)
    val dockTextScale by FloatPref("pref_dockTextScale", -1f, restart)
    private val dockMultilineLabel by BooleanPref("pref_dockIconLabelsInTwoLines", false, recreate)
    val dockLabelRows get() = if (dockMultilineLabel) 2 else 1

    // Drawer
    val hideAllAppsAppLabels by BooleanPref("pref_hideAllAppsAppLabels", false, recreate)
    val allAppsOpacity by AlphaPref("pref_allAppsOpacitySB", -1, recreate)
    val allAppsStartAlpha get() = dockStyles.currentStyle.opacity
    val allAppsEndAlpha get() = allAppsOpacity
    val allAppsSearch by BooleanPref("pref_allAppsSearch", true, recreate)
    var allAppsGlobalSearch by BooleanPref("pref_allAppsGoogleSearch", true, doNothing)
    val showAllAppsLabel by BooleanPref("pref_showAllAppsLabel", false) {
        val header =
                onChangeCallback?.launcher?.appsView?.floatingHeaderView as? PredictionsFloatingHeader
        header?.updateShowAllAppsLabel()
    }
    val separateWorkApps by BooleanPref("pref_separateWorkApps", true, recreate)
    val saveScrollPosition by BooleanPref("pref_keepScrollState", false, doNothing)
    var shadespacePlugin by NullableStringPref("pref_shadespace_plugins", null)
    var shortcutPlugins by StringSetPref("pref_shortcut_plugins", emptySet())
    var buttonPlugins by StringSetPref("pref_button_plugins", emptySet())
    var activityPlugins by StringSetPref("pref_activity_plugins", emptySet())

    private val drawerGridSizeDelegate = ResettableLazy {
        GridSize(this, "numColsDrawer", LauncherAppState.getIDP(context), recreate)
    }
    val drawerGridSize by drawerGridSizeDelegate
    private val predictionGridSizeDelegate = ResettableLazy {
        GridSize(this, "numPredictions", LauncherAppState.getIDP(context), recreate)
    }
    val predictionGridSize by predictionGridSizeDelegate
    val drawerPaddingScale by FloatPref("pref_allAppsPaddingScale", 1.0f, recreate)
    val showPredictions by BooleanPref("pref_show_predictions", true, recreate)
    private val drawerMultilineLabel by BooleanPref("pref_iconLabelsInTwoLines", false, recreate)
    val drawerLabelRows get() = if (drawerMultilineLabel) 2 else 1
    val appGroupsManager by lazy { AppGroupsManager(this) }
    val drawerTabs get() = appGroupsManager.drawerTabs
    val currentTabsModel
        get() = appGroupsManager.getEnabledModel() as? DrawerTabs ?: appGroupsManager.drawerTabs
    val showActions by BooleanPref("pref_show_suggested_actions", true, doNothing)
    val sortDrawerByColors by BooleanPref("pref_allAppsColorSorted", false, reloadAll)
    val drawerTextScale by FloatPref("pref_allAppsIconTextScale", 1f, recreate)
    val searchHiddenApps by BooleanPref(DefaultAppSearchAlgorithm.SEARCH_HIDDEN_APPS, false)

    var noteVerticalDisplay by BooleanPref("pref_vertical_note_adapter", false);

    // Dev
    var developerOptionsEnabled by BooleanPref("pref_showDevOptions", false, doNothing)
    private var debugMenuKey by StringPref("pref_debugMenuKey", "", doNothing)
    var debugMenuEnabled
        get() = debugMenuKey == Settings.Secure.ANDROID_ID
        set(value) {
            debugMenuKey = if (value) Settings.Secure.ANDROID_ID else ""
        }
    val showDebugInfo by BooleanPref("pref_showDebugInfo", false, doNothing)
    val alwaysClearIconCache by BooleanPref("pref_alwaysClearIconCache", false, restart)
    val debugLegacyTreatment by BooleanPref("pref_debugLegacyTreatment", false, restart)
    val lowPerformanceMode by BooleanPref("pref_lowPerformanceMode",
            BuildConfig.FLAVOR_go == "l3go", restart)
    val enablePhysics get() = !lowPerformanceMode
    val backupScreenshot by BooleanPref("pref_backupScreenshot", false, doNothing)
    var useScaleAnim by BooleanPref("pref_useScaleAnim", false, doNothing)
    val useWindowToIcon by BooleanPref("pref_useWindowToIcon", true, doNothing)
    val dismissTasksOnKill by BooleanPref("pref_dismissTasksOnKill", true, doNothing)
    var customFontName by StringPref("pref_customFontName", "Google Sans", doNothing)
    var forceEnableFools by BooleanPref("pref_forceEnableFools", false, restart)
    val visualizeOccupied by BooleanPref("pref_debugVisualizeOccupied")
    val scaleAdaptiveBg by BooleanPref("pref_scaleAdaptiveBg", false)
    val folderBgColored by BooleanPref("pref_folderBgColorGen", false)
    val brightnessTheme by BooleanPref("pref_brightnessTheme", false, restart)
    val debugOkHttp by BooleanPref("pref_debugOkhttp", onChange = restart)
    val initLeakCanary by BooleanPref("pref_initLeakCanary", true, restart)
    val showCrashNotifications by BooleanPref("pref_showCrashNotifications", true, restart)
    val autoUploadBugReport by BooleanPref("pref_autoUploadBugReport", false) {
        if (showCrashNotifications) {
            BugReportClient.getInstance(context).setAutoUploadEnabled()
        }
    }
    val overrideLocale by StringPref("pref_override_locale", "", ::restartOverlay)
    val forceFakePieAnims by BooleanPref("pref_forceFakePieAnims", false)
    val displayDebugOverlay by BooleanPref("pref_debugDisplayState", false)
    val swipeHome by BooleanPref("pref_swipeHome", false, recreate)

    // Search
    var searchProvider by StringPref("pref_globalSearchProvider",
            lawnchairConfig.defaultSearchProvider) {
        SearchProviderController.getInstance(context).onSearchProviderChanged()
    }
    val dualBubbleSearch by BooleanPref("pref_bubbleSearchStyle", false, doNothing)
    var searchBarRadius by DimensionPref("pref_searchbarRadius", -1f)

    // Quickstep
    var quickstep by StringPref("pref_quickstep", BuildConfig.APPLICATION_ID, doNothing)
    var swipeUpToSwitchApps by BooleanPref("pref_swipe_up_to_switch_apps_enabled", true, doNothing)
    val recentsRadius by DimensionPref("pref_recents_radius", context.resources.getInteger(
            R.integer.task_corner_radius).toFloat(), doNothing)
    val swipeLeftToGoBack by BooleanPref("pref_swipe_left_to_go_back", false) {
        OverviewInteractionState.getInstance(context).setBackButtonAlpha(1f, true)
    }
    val recentsBlurredBackground by BooleanPref("pref_recents_blur_background", true) {
        onChangeCallback?.launcher?.background?.onEnabledChanged()
    }

    // Misc
    var autoLaunchRoot by BooleanPref("internal_auto_launch_root")
    var noFools by BooleanPref("pref_noFools2019", false) { Utilities.restartLauncher(context) }
    val enableFools get() = forceEnableFools || is1stApril()
    val showFools get() = !noFools && enableFools

    val immersiveDesktop by BooleanPref("pref_immersive_desktop", false)

    var wakeUpCallTime by StringPref("pref_daily_brief", "7:30")
    var iconContrast by FloatPref("pref_icon_contrast", 1f, reloadIcons)
    val iconBrightness by FloatPref("pref_icon_brightness", 1f, reloadIcons)
    var feedForecastItemCount by FloatPref("pref_forecast_item_count", 6f, ::restartOverlay)
    var feedDailyForecastItemCount by FloatPref("pref_daily_forecast_item_count", 4f,
            ::restartOverlay)
    var feedProviderPackage by StringPref("pref_feed_provider_package", BuildConfig.APPLICATION_ID,
            restart)
    val feedAnimationDelegate by StringPref("pref_feed_animation",
            DefaultFeedTransitionDelegate::class.qualifiedName!!
            , ::restartOverlay)
    var feedTabController by StringPref("pref_feed_tab_controller", TabController::class.java.name,
            ::restartOverlay)
    var feedCalendarEventThreshold by IntPref("pref_feed_calendar_days", 30, ::restartOverlay)
    var feedWebApplications by WebApplicationListPref("pref_feed_web_applications",
            ::restartOverlay, listOf(), sharedPrefs)
    val feedHighContrastToolbar by BooleanPref("pref_high_contrast_toolbar", false,
            ::restartOverlay)
    val feedBackToTop by BooleanPref("pref_feed_display_back_to_top_button", true, ::restartOverlay)
    var feedBackground by ImageProviderPref("pref_feed_background_2",
            ImageProviderContainer(ImageProvider::class, Collections.emptyMap()), ::restartOverlay)
    val feedSearchUrl by StringPref("pref_feed_search_url_template",
            "https://example.com/search?q=%s", ::restartOverlay)
    val feedShowOtherTab by BooleanPref("pref_show_other_tab", true, ::restartOverlay)
    val feedBlurStrength by FloatPref("pref_feed_blur_strength", 255f, ::restartOverlay)
    val feedIndicatorProvider by StringPref("pref_feed_indicator_provider",
            TabIndicatorProvider::class.qualifiedName!!, ::restartOverlay)
    val feedColorProvider by StringPref("pref_feed_color_provider",
            ColorProvider::class.qualifiedName!!,
            ::restartOverlay)
    var feedCustomTabs = object :
            MutableListPref<CustomTab>(sharedPrefs, "pref_feed_custom_tabs", ::restartOverlay, run {
                val providerList = MainFeedController.getFeedProviders(context, true)
                return@run listOf(CustomTab().apply {
                    iconToken = "resource_utility"
                    name = R.string.category_tools.fromStringRes(context)
                    providers = providerList.filter {
                        it.clazz.let {
                            it == FeedWeatherStatsProvider::class.qualifiedName
                                    || it == FeedForecastProvider::class.qualifiedName
                                    || it == FeedDailyForecastProvider::class.qualifiedName
                                    || it == DailySummaryFeedProvider::class.qualifiedName
                                    || it == WikipediaFunFactsProvider::class.qualifiedName
                                    || it == NoteListProvider::class.qualifiedName
                                    || it == WebApplicationsProvider::class.qualifiedName
                                    || it == FeedJoinedWeatherProvider::class.qualifiedName
                                    || it == WeatherBarFeedProvider::class.qualifiedName
                                    || ImageProvider::class.isSuperclassOf(Class.forName(it).kotlin)
                        } || it.arguments.get(RemoteFeedProvider.COMPONENT_CATEGORY) == "tools"
                    }.toTypedArray()
                }, CustomTab().apply {
                    iconToken = "resource_news"
                    name = R.string.category_news.fromStringRes(context)
                    providers = providerList.filter {
                        it.clazz.let {
                            it == WikipediaNewsProvider::class.qualifiedName || AbstractRSSFeedProvider::class.isSuperclassOf(
                                    Class.forName(it).kotlin)
                        } || it.arguments.get(RemoteFeedProvider.COMPONENT_CATEGORY) == "news"
                    }.toTypedArray()
                }, CustomTab().apply {
                    iconToken = "resource_events"
                    name = R.string.feed_category_events.fromStringRes(context)
                    providers = providerList.filter {
                        it.clazz.let {
                            it == CalendarEventProvider::class.qualifiedName || it == AlarmEventProvider::class.qualifiedName
                        } || it.arguments.get(RemoteFeedProvider.COMPONENT_CATEGORY) == "events"
                    }.toTypedArray()
                }, CustomTab().apply {
                    iconToken = "resource_widgets"
                    name = R.string.widget_button_text.fromStringRes(context)
                    providers = providerList.filter {
                        it.clazz == FeedWidgetsProvider::class.qualifiedName
                    }.toTypedArray()
                })
            }) {
        override fun unflattenValue(value: String): CustomTab {
            return Gson().fromJson(value, CustomTab::class.java)
        }

        override fun flattenValue(value: CustomTab): String {
            return Gson().toJson(value)
        }
    }
    val feedShowCalendarColour by BooleanPref("pref_feed_show_event_color", true, ::restartOverlay)
    var cardDecorationMargin by FloatPref("pref_feed_decoration_margin", -1f, ::restartOverlay)
    var cardDecorationMarginVertical by FloatPref("pref_feed_decoration_margin_vertical", 16f,
            ::restartOverlay)
    var cardDecorationMarginHorizontal by FloatPref("pref_feed_decoration_margin_horizontal", 16f,
            ::restartOverlay)

    var showVerticalDailyForecast by BooleanPref("pref_show_vertical_daily_forecast", false,
            ::restartOverlay)
    var showVerticalHourlyForecast by BooleanPref("pref_show_vertical_hourly_forecast", false,
            ::restartOverlay)
    var elevateWeatherCard by BooleanPref("pref_elevate_weather_card", false, ::restartOverlay)
    var feedHorizontalTabs by BooleanPref("pref_display_feed_tabs_as_single_line", false,
            ::restartOverlay)
    var feedHideTabText by BooleanPref("pref_feed_hide_tab_text", false, ::restartOverlay)
    val feedTabsOnBottom by BooleanPref("pref_feed_tabs_on_bottom", false, ::restartOverlay)
    var feedToolbarWidget by IntPref("pref_feed_toolbar_custom_widget", -1)
    var feedToolbarWidgetForceStyle by BooleanPref("pref_feed_toolbar_widget_force_style",
            false, ::restartOverlay)
    val feedRaisedCardTitleAlignment by StringPref("pref_feed_card_title_alignment",
            TitleAlignmentPreference.ALIGNMENT_CENTER, ::restartOverlay)
    val feedRaisedHeaderOnlyCardTitleAlignment by StringPref(
            "pref_feed_header_only_card_title_alignment",
            TitleAlignmentPreference.ALIGNMENT_END, ::restartOverlay)
    var lastKnownLocation by StringPref("pref_last_known_location", "")
    val feedAutoHideToolbar by BooleanPref("pref_feed_hide_toolbar", true, ::restartOverlay)
    val feedHideUnusedTabs by BooleanPref("pref_feed_hide_unused_tabs", true, ::restartOverlay)
    private val was1stApril = is1stApril()

    fun checkFools() {
        if (was1stApril != is1stApril()) {
            restart()
        }
    }

    private fun is1stApril(): Boolean {
        val now = GregorianCalendar.getInstance()
        val date = now.get(Calendar.DAY_OF_MONTH)
        val month = now.get(Calendar.MONTH)
        return date == 1 && month == Calendar.APRIL && now.get(Calendar.YEAR) == 2369 /* DS9 */
    }

    var hiddenAppSet by StringSetPref("hidden-app-set", Collections.emptySet(), reloadApps)
    var hiddenPredictionAppSet by StringSetPref("pref_hidden_prediction_set",
            Collections.emptySet(), doNothing)
    var hiddenPredictionActionSet by StringSetPref("pref_hidden_prediction_set",
            Collections.emptySet(), doNothing)
    val customAppName =
            object : MutableMapPref<ComponentKey, String>("pref_appNameMap", reloadAll) {
                override fun flattenKey(key: ComponentKey) = key.toString()
                override fun unflattenKey(key: String) = ComponentKey(context, key)
                override fun flattenValue(value: String) = value
                override fun unflattenValue(value: String) = value
            }
    val customAppIcon = object :
            MutableMapPref<ComponentKey, IconPackManager.CustomIconEntry>("pref_appIconMap",
                    reloadAll) {
        override fun flattenKey(key: ComponentKey) = key.toString()
        override fun unflattenKey(key: String) = ComponentKey(context, key)
        override fun flattenValue(value: IconPackManager.CustomIconEntry) = value.toString()
        override fun unflattenValue(value: String) = IconPackManager.CustomIconEntry.fromString(
                value)
    }
    val recentBackups =
            object : MutableListPref<Uri>(Utilities.getDevicePrefs(context), "pref_recentBackups") {
                override fun unflattenValue(value: String) = Uri.parse(value)
            }

    inline fun withChangeCallback(
            crossinline callback: (LawnchairPreferencesChangeCallback) -> Unit): () -> Unit {
        return { getOnChangeCallback()?.let { callback(it) } }
    }

    fun getOnChangeCallback() = onChangeCallback

    private fun recreate() {
        onChangeCallback?.recreate()
    }

    private fun reloadApps() {
        onChangeCallback?.reloadApps()
    }

    private fun reloadAll() {
        onChangeCallback?.reloadAll()
    }

    fun restart() {
        onChangeCallback?.restart()
    }

    fun restartOverlay() {
        if (getCurrentProcessName(context) != ":overlay") {
            LawnchairLauncher.getLauncher(context).overlay?.client?.restartProcess()
        }

        sharedPrefs.edit().putString("pref_feed_preview", UUID.randomUUID().toString()).apply()
    }

    fun refreshGrid() {
        onChangeCallback?.refreshGrid()
    }

    private fun updateBlur() {
        onChangeCallback?.updateBlur()
    }

    private fun updateSmartspaceProvider() {
        onChangeCallback?.updateSmartspaceProvider()
    }

    private fun updateSmartspace() {
        onChangeCallback?.updateSmartspace()
    }

    fun reloadIcons() {
        LauncherAppState.getInstance(context).reloadIconCache()
        runOnMainThread {
            onChangeCallback?.recreate()
        }
    }

    fun addOnPreferenceChangeListener(listener: OnPreferenceChangeListener, vararg keys: String) {
        keys.forEach { addOnPreferenceChangeListener(it, listener) }
    }

    fun addOnPreferenceChangeListener(key: String, listener: OnPreferenceChangeListener) {
        if (onChangeListeners[key] == null) {
            onChangeListeners[key] = HashSet()
        }
        onChangeListeners[key]?.add(listener)
        listener.onValueChanged(key, this, true)
    }

    fun removeOnPreferenceChangeListener(listener: OnPreferenceChangeListener,
                                         vararg keys: String) {
        keys.forEach { removeOnPreferenceChangeListener(it, listener) }
    }

    fun removeOnPreferenceChangeListener(key: String, listener: OnPreferenceChangeListener) {
        onChangeListeners[key]?.remove(listener)
    }

    inner class WebApplicationListPref(prefKey: String, onChange: () -> Unit = doNothing,
                                       default: List<WebApplication> = emptyList(),
                                       prefs: SharedPreferences) :
            MutableListPref<WebApplication>(prefs, prefKey, onChange, default) {
        override fun flattenValue(value: WebApplication): String {
            return Gson().toJson(value)
        }

        override fun unflattenValue(value: String): WebApplication {
            Log.d(javaClass.name, "unflattenValue: value is $value")
            return Gson().fromJson(value, WebApplication::class.java)
        }
    }

    inner class StringListPref(prefKey: String, onChange: () -> Unit = doNothing,
                               default: List<String> = emptyList()) :
            MutableListPref<String>(prefKey, onChange, default) {

        override fun unflattenValue(value: String) = value
        override fun flattenValue(value: String) = value
    }

    abstract inner class MutableListPref<T>(private val prefs: SharedPreferences,
                                            private val prefKey: String,
                                            onChange: () -> Unit = doNothing,
                                            default: List<T> = emptyList()) :
            PrefDelegate<List<T>>(prefKey, default, onChange) {

        constructor(prefKey: String, onChange: () -> Unit = doNothing,
                    default: List<T> = emptyList()) : this(sharedPrefs, prefKey, onChange, default)

        private val valueList = ArrayList<T>()
        private val listeners: MutableSet<MutableListPrefChangeListener> =
                Collections.newSetFromMap(WeakHashMap())

        init {
            var arr: JSONArray
            try {
                arr = JSONArray(prefs.getString(prefKey, getJsonString(default)))
            } catch (e: ClassCastException) {
                e.printStackTrace()
                arr = JSONArray()
            }
            (0 until arr.length()).mapTo(valueList) { unflattenValue(arr.getString(it)) }
            if (onChange != doNothing) {
                onChangeMap[prefKey] = onChange
            }
        }

        fun toList() = ArrayList<T>(valueList)

        open fun flattenValue(value: T) = value.toString()
        open fun customAdder(value: T): Unit = error("not implemented in base class")

        abstract fun unflattenValue(value: String): T

        operator fun get(position: Int): T {
            return valueList[position]
        }

        operator fun set(position: Int, value: T) {
            valueList[position] = value
            saveChanges()
        }

        fun getAll(): List<T> = valueList

        fun setAll(value: List<T>) {
            valueList.clear()
            valueList.addAll(value)
            saveChanges()
        }

        fun add(value: T) {
            valueList.add(value)
            saveChanges()
        }

        fun add(position: Int, value: T) {
            valueList.add(position, value)
            saveChanges()
        }

        fun remove(value: T) {
            valueList.remove(value)
            saveChanges()
        }

        fun removeAt(position: Int) {
            valueList.removeAt(position)
            saveChanges()
        }

        fun contains(value: T): Boolean {
            return valueList.contains(value)
        }

        fun replaceWith(newList: List<T>) {
            valueList.clear()
            valueList.addAll(newList)
            saveChanges()
        }

        fun getList() = valueList

        fun addListener(listener: MutableListPrefChangeListener) {
            listeners.add(listener)
        }

        fun removeListener(listener: MutableListPrefChangeListener) {
            listeners.remove(listener)
        }

        private fun saveChanges() {
            @SuppressLint("CommitPrefEdits") val editor = prefs.edit()
            editor.putString(prefKey, getJsonString(valueList))
            if (!bulkEditing) commitOrApply(editor, blockingEditing)
            listeners.forEach { it.onListPrefChanged(prefKey) }
        }

        private fun getJsonString(list: List<T>): String {
            val arr = JSONArray()
            list.forEach { arr.put(flattenValue(it)) }
            return arr.toString()
        }

        override fun onGetValue(): List<T> {
            return getAll()
        }

        override fun onSetValue(value: List<T>) {
            setAll(value)
        }
    }

    interface MutableListPrefChangeListener {

        fun onListPrefChanged(key: String)
    }

    abstract inner class MutableMapPref<K, V>(private val prefKey: String,
                                              onChange: () -> Unit = doNothing) {
        private val valueMap = HashMap<K, V>()

        init {
            val obj = JSONObject(sharedPrefs.getString(prefKey, "{}"))
            obj.keys().forEach {
                valueMap[unflattenKey(it)] = unflattenValue(obj.getString(it))
            }
            if (onChange !== doNothing) {
                onChangeMap[prefKey] = onChange
            }
        }

        fun toMap() = HashMap<K, V>(valueMap)

        open fun flattenKey(key: K) = key.toString()
        abstract fun unflattenKey(key: String): K

        open fun flattenValue(value: V) = value.toString()
        abstract fun unflattenValue(value: String): V

        operator fun set(key: K, value: V?) {
            if (value != null) {
                valueMap[key] = value
            } else {
                valueMap.remove(key)
            }
            saveChanges()
        }

        private fun saveChanges() {
            val obj = JSONObject()
            valueMap.entries.forEach { obj.put(flattenKey(it.key), flattenValue(it.value)) }
            @SuppressLint("CommitPrefEdits") val editor =
                    if (bulkEditing) editor!! else sharedPrefs.edit()
            editor.putString(prefKey, obj.toString())
            if (!bulkEditing) commitOrApply(editor, blockingEditing)
        }

        operator fun get(key: K): V? {
            return valueMap[key]
        }

        fun clear() {
            valueMap.clear()
            saveChanges()
        }
    }

    inline fun <reified T : Enum<T>> EnumPref(key: String, defaultValue: T,
                                              noinline onChange: () -> Unit = doNothing): PrefDelegate<T> {
        return IntBasedPref(key, defaultValue, onChange, { value ->
            enumValues<T>().firstOrNull { item -> item.ordinal == value } ?: defaultValue
        }, { it.ordinal }, { })
    }

    open inner class IntBasedPref<T : Any>(key: String, defaultValue: T,
                                           onChange: () -> Unit = doNothing,
                                           private val fromInt: (Int) -> T,
                                           private val toInt: (T) -> Int,
                                           private val dispose: (T) -> Unit) :
            PrefDelegate<T>(key, defaultValue, onChange) {
        override fun onGetValue(): T {
            return if (sharedPrefs.contains(key)) {
                fromInt(sharedPrefs.getInt(getKey(), toInt(defaultValue)))
            } else defaultValue
        }

        override fun onSetValue(value: T) {
            edit { putInt(getKey(), toInt(value)) }
        }

        override fun disposeOldValue(oldValue: T) {
            dispose(oldValue)
        }
    }

    open inner class StringBasedPref<T : Any>(key: String, defaultValue: T,
                                              onChange: () -> Unit = doNothing,
                                              private val fromString: (String) -> T,
                                              private val toString: (T) -> String,
                                              private val dispose: (T) -> Unit) :
            PrefDelegate<T>(key, defaultValue, onChange) {
        override fun onGetValue(): T = sharedPrefs.getString(getKey(), null)?.run(fromString)
                ?: defaultValue

        override fun onSetValue(value: T) {
            edit { putString(getKey(), toString(value)) }
        }

        override fun disposeOldValue(oldValue: T) {
            dispose(oldValue)
        }
    }

    open inner class StringPref(key: String, defaultValue: String = "",
                                onChange: () -> Unit = doNothing) :
            PrefDelegate<String>(key, defaultValue, onChange) {
        override fun onGetValue(): String = sharedPrefs.getString(getKey(), defaultValue)!!

        override fun onSetValue(value: String) {
            edit { putString(getKey(), value) }
        }
    }

    open inner class NullableStringPref(key: String, defaultValue: String? = null,
                                        onChange: () -> Unit = doNothing) :
            PrefDelegate<String?>(key, defaultValue, onChange) {
        override fun onGetValue(): String? = sharedPrefs.getString(getKey(), defaultValue)

        override fun onSetValue(value: String?) {
            edit { putString(getKey(), value) }
        }
    }

    open inner class StringSetPref(key: String, defaultValue: Set<String>,
                                   onChange: () -> Unit = doNothing) :
            SetPref<String>(key, defaultValue, onChange) {
        override fun serialize(value: String) = value
        override fun unserialize(value: String) = value
    }

    abstract inner class SetPref<T>(key: String, defaultValue: Set<T>,
                                    onChange: () -> Unit = doNothing) :
            PrefDelegate<Set<T>>(key, defaultValue, onChange) {
        override fun onGetValue(): Set<T> {
            return sharedPrefs.getStringSet(key, emptySet())?.map { unserialize(it) }?.toSet()
                    ?: emptySet()
        }

        override fun onSetValue(value: Set<T>) {
            edit {
                putStringSet(key, value.map { serialize(it) }.toSet())
            }
        }

        abstract fun serialize(value: T): String
        abstract fun unserialize(value: String): T
    }

    open inner class StringIntPref(key: String, defaultValue: Int = 0,
                                   onChange: () -> Unit = doNothing) :
            PrefDelegate<Int>(key, defaultValue, onChange) {
        override fun onGetValue(): Int = try {
            sharedPrefs.getString(getKey(), "$defaultValue").toInt()
        } catch (e: Exception) {
            sharedPrefs.getInt(getKey(), defaultValue)
        }

        override fun onSetValue(value: Int) {
            edit { putString(getKey(), "$value") }
        }
    }

    // This properly migrates v1 string int prefs including those with "default" value
    open inner class StringIntMigrationPref(key: String, defaultValue: Int = 0,
                                            onChange: () -> Unit = doNothing) :
            PrefDelegate<Int>(key, defaultValue, onChange) {
        override fun onGetValue(): Int = try {
            sharedPrefs.getInt(getKey(), defaultValue)
        } catch (e: Exception) {
            toInt(sharedPrefs.getString(getKey(), "$defaultValue")!!).apply {
                edit { putInt(getKey(), this@apply) }
            }
        }

        override fun onSetValue(value: Int) {
            edit { putInt(getKey(), value) }
        }

        private fun toInt(string: String) = if (string == "default") 0 else string.toInt()
    }

    open inner class IntPref(key: String, defaultValue: Int = 0, onChange: () -> Unit = doNothing) :
            PrefDelegate<Int>(key, defaultValue, onChange) {
        override fun onGetValue(): Int = sharedPrefs.getInt(getKey(), defaultValue)

        override fun onSetValue(value: Int) {
            edit { putInt(getKey(), value) }
        }
    }

    open inner class AlphaPref(key: String, defaultValue: Int = 0,
                               onChange: () -> Unit = doNothing) :
            PrefDelegate<Int>(key, defaultValue, onChange) {
        override fun onGetValue(): Int = (sharedPrefs.getFloat(getKey(),
                defaultValue.toFloat() / 255) * 255).roundToInt()

        override fun onSetValue(value: Int) {
            edit { putFloat(getKey(), value.toFloat() / 255) }
        }
    }

    open inner class DimensionPref(key: String, defaultValue: Float = 0f,
                                   onChange: () -> Unit = doNothing) :
            PrefDelegate<Float>(key, defaultValue, onChange) {

        override fun onGetValue(): Float = dpToPx(sharedPrefs.getFloat(getKey(), defaultValue))

        override fun onSetValue(value: Float) {
            edit { putFloat(getKey(), pxToDp(value.toFloat())) }
        }
    }

    open inner class FloatPref(key: String, defaultValue: Float = 0f,
                               onChange: () -> Unit = doNothing) :
            PrefDelegate<Float>(key, defaultValue, onChange) {
        override fun onGetValue(): Float = sharedPrefs.getFloat(getKey(), defaultValue)

        override fun onSetValue(value: Float) {
            edit { putFloat(getKey(), value) }
        }
    }

    open inner class BooleanPref(key: String, defaultValue: Boolean = false,
                                 onChange: () -> Unit = doNothing) :
            PrefDelegate<Boolean>(key, defaultValue, onChange) {
        override fun onGetValue(): Boolean = sharedPrefs.getBoolean(getKey(), defaultValue)

        override fun onSetValue(value: Boolean) {
            edit { putBoolean(getKey(), value) }
        }
    }

    open inner class ImageProviderPref(key: String, defaultValue: ImageProviderContainer,
                                       onChange: () -> Unit = doNothing) :
            PrefDelegate<ImageProviderContainer>(key, defaultValue, onChange) {
        override fun onGetValue(): ImageProviderContainer =
                if (sharedPrefs.getString(key + "clazs",
                                null) == null) defaultValue else
                    ImageProviderContainer(Class.forName(sharedPrefs.getString(key + "clazs",
                            null)!!).kotlin as KClass<out ImageProvider>,
                            Gson().fromJson(sharedPrefs.getString(key + "mta", null), object :
                                    TypeToken<Map<String, String>>() {}.type) as Map<String, String>)

        override fun onSetValue(value: ImageProviderContainer) = edit {
            putString(key + "clazs", value.clazz.qualifiedName)
            putString(key + "mta", Gson().toJson(value.meta))
        }
    }

    // ----------------
    // Helper functions and class
    // ----------------

    fun getPrefKey(key: String) = "pref_$key"

    fun commitOrApply(editor: SharedPreferences.Editor, commit: Boolean) {
        if (commit) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

    var blockingEditing = false
    var bulkEditing = false
    var editor: SharedPreferences.Editor? = null
    val bulkEditCount = AtomicInteger(0)

    fun beginBlockingEdit() {
        blockingEditing = true
    }

    fun endBlockingEdit() {
        blockingEditing = false
    }

    @SuppressLint("CommitPrefEdits")
    fun beginBulkEdit() {
        synchronized(bulkEditCount) {
            if (bulkEditCount.getAndIncrement() == 0) {
                bulkEditing = true
                editor = sharedPrefs.edit()
            }
        }
    }

    fun endBulkEdit() {
        synchronized(bulkEditCount) {
            if (bulkEditCount.decrementAndGet() == 0) {
                bulkEditing = false
                commitOrApply(editor!!, blockingEditing)
                editor = null
            }
        }
    }

    inline fun blockingEdit(body: LawnchairPreferences.() -> Unit) {
        beginBlockingEdit()
        body(this)
        endBlockingEdit()
    }

    inline fun bulkEdit(body: LawnchairPreferences.() -> Unit) {
        beginBulkEdit()
        body(this)
        endBulkEdit()
    }

    abstract inner class PrefDelegate<T : Any?>(val key: String, val defaultValue: T,
                                                private val onChange: () -> Unit) {

        private var cached = false
        protected var value: T = defaultValue

        init {
            onChangeMap[key] = { onValueChanged() }
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            if (!cached) {
                value = onGetValue()
                cached = true
            }
            return value
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            discardCachedValue()
            onSetValue(value)
        }

        abstract fun onGetValue(): T

        abstract fun onSetValue(value: T)

        protected inline fun edit(body: SharedPreferences.Editor.() -> Unit) {
            @SuppressLint("CommitPrefEdits") val editor =
                    if (bulkEditing) editor!! else sharedPrefs.edit()
            body(editor)
            if (!bulkEditing) commitOrApply(editor, blockingEditing)
        }

        internal fun getKey() = key

        private fun onValueChanged() {
            discardCachedValue()
            onChange.invoke()
        }

        private fun discardCachedValue() {
            if (cached) {
                cached = false
                value.let(::disposeOldValue)
            }
        }

        open fun disposeOldValue(oldValue: T) {

        }
    }

    inner class ResettableLazy<out T : Any>(private val create: () -> T) {

        private var initialized = false
        private var currentValue: T? = null

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            if (!initialized) {
                currentValue = create()
                initialized = true
            }
            return currentValue!!
        }

        fun resetValue() {
            initialized = false
            currentValue = null
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String) {
        onChangeMap[key]?.invoke()
        onChangeListeners[key]?.forEach { it.onValueChanged(key, this, false) }
    }

    fun registerCallback(callback: LawnchairPreferencesChangeCallback) {
        sharedPrefs.registerOnSharedPreferenceChangeListener(this)
        onChangeCallback = callback
    }

    fun unregisterCallback() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
        onChangeCallback = null
    }

    init {
        d("Initializing", Throwable())
    }

    fun migrateConfig(prefs: SharedPreferences) {
        val version = prefs.getInt(VERSION_KEY, CURRENT_VERSION)
        if (version != CURRENT_VERSION) {
            with(prefs.edit()) {
                // Migration codes here

                if (version == 100) {
                    migrateFromV1(this, prefs)
                }

                putInt(VERSION_KEY, CURRENT_VERSION)
                commit()
            }
        }
    }

    private fun migrateFromV1(editor: SharedPreferences.Editor, prefs: SharedPreferences) = with(
            editor) {
        // Set flags
        putBoolean("pref_legacyUpgrade", true)
        putBoolean("pref_restoreSuccess", false)
        // Reset icon shape to system shape
        // TODO: possibly create some sort of migration shape which uses a path stored in another pref
        // TODO: where we would move the current value of this to
        putString("pref_iconShape", "")

        // Dt2s
        putString("pref_gesture_double_tap", when (prefs.getString("pref_dt2sHandler", "")) {
            "" -> BlankGestureHandler(context, null)
            "ch.deletescape.lawnchair.gestures.dt2s.DoubleTapGesture\$SleepGestureHandlerTimeout" -> SleepGestureHandlerTimeout(
                    context, null)
            else -> SleepGestureHandler(context, null)
        }.toString())

        // Dock
        putString("pref_dockPreset", "0")
        putBoolean("pref_dockShadow", false)
        putBoolean("pref_hotseatShowArrow", prefs.getBoolean("pref_hotseatShowArrow", true))
        putFloat("pref_dockRadius", 0f)
        putBoolean("pref_dockGradient", prefs.getBoolean("pref_isHotseatTransparent", false))
        if (!prefs.getBoolean("pref_hotseatShouldUseCustomOpacity", false)) {
            putFloat("pref_hotseatCustomOpacity", -1f / 255)
        }
        putFloat("pref_dockScale", prefs.getFloat("pref_hotseatHeightScale", 1f))

        // Home widget
        val pillQsb = prefs.getBoolean("pref_showPixelBar", true)
                // The new dock qsb should be close enough I guess
                && !prefs.getBoolean("pref_fullWidthSearchbar", false);
        putBoolean("pref_use_pill_qsb", pillQsb)
        if (pillQsb) {
            putBoolean("pref_dockSearchBar", false)
        }
        if (!prefs.getBoolean("pref_showDateOrWeather", true)) {
            putString("pref_smartspace_widget_provider", BlankDataProvider::class.java.name)
        }

        // Colors
        if (prefs.contains("pref_workspaceLabelColor")) {
            val color = prefs.getInt("pref_workspaceLabelColor", Color.WHITE)
            ColorEngine.setColor(this, ColorEngine.Resolvers.WORKSPACE_ICON_LABEL, color)
            ColorEngine.setColor(this, ColorEngine.Resolvers.ALLAPPS_ICON_LABEL, color)
        }

        // Theme
        putString("pref_launcherTheme", when (prefs.getString("pref_theme", "0")) {
            "1" -> ThemeManager.THEME_DARK
            "2" -> ThemeManager.THEME_USE_BLACK or ThemeManager.THEME_DARK
            else -> 0
        }.toString())
        putString("pref_icon_pack", prefs.getString("pref_iconPackPackage", ""))

        // Gestures
        putString("pref_gesture_swipe_down",
                when (Integer.parseInt(prefs.getString("pref_pulldownAction", "1"))) {
                    1 -> NotificationsOpenGestureHandler(context, null)
                    2 -> StartGlobalSearchGestureHandler(context, null)
                    3 -> StartAppSearchGestureHandler(context, null)
                    else -> BlankGestureHandler(context, null)
                }.toString())
        if (prefs.getBoolean("pref_homeOpensDrawer", false)) {
            putString("pref_gesture_press_home", OpenDrawerGestureHandler(context, null).toString())
        }

        // misc
        putBoolean("pref_add_icon_to_home", prefs.getBoolean("pref_autoAddShortcuts", true))

        // Disable some newer features per default
        putBoolean("pref_allAppsGoogleSearch", false)
    }

    interface OnPreferenceChangeListener {

        fun onValueChanged(key: String, prefs: LawnchairPreferences, force: Boolean)
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: LawnchairPreferences? = null

        const val CURRENT_VERSION = 200
        const val VERSION_KEY = "config_version"

        fun getInstance(context: Context): LawnchairPreferences {
            if (INSTANCE == null) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    INSTANCE = LawnchairPreferences(context.applicationContext)
                } else {
                    try {
                        return MainThreadExecutor()
                                .submit(Callable { LawnchairPreferences.getInstance(context) })
                                .get()
                    } catch (e: InterruptedException) {
                        throw RuntimeException(e)
                    } catch (e: ExecutionException) {
                        throw RuntimeException(e)
                    }

                }
            }
            return INSTANCE!!
        }

        fun getInstanceNoCreate(): LawnchairPreferences {
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE?.apply {
                onChangeListeners.clear()
                onChangeCallback = null
                gridSizeDelegate.resetValue()
                dockGridSizeDelegate.resetValue()
                drawerGridSizeDelegate.resetValue()
                predictionGridSizeDelegate.resetValue()
            }
        }
    }
}

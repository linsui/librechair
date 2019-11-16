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

package ch.deletescape.lawnchair.persistence

import android.content.Context
import ch.deletescape.lawnchair.feed.*
import ch.deletescape.lawnchair.feed.maps.MapProvider
import ch.deletescape.lawnchair.feed.widgets.FeedWidgetsProvider
import ch.deletescape.lawnchair.util.SingletonHolder
import com.android.launcher3.BuildConfig
import com.google.gson.Gson

class FeedPersistence private constructor(val context: Context) {
    val useBackgroundImageAsScreenBackground
            by BooleanDelegate(context, "feed_background_as_screen_background", true)
    val useBoxBackgroundBlur
            by BooleanDelegate(context, "feed_boxed_background_blur", false)
    val useJavascriptInSearchScreen
            by BooleanDelegate(context, "feed_javascript_in_search_screen", false)
    val displayActionsAsMenu
            by BooleanDelegate(context, "feed_actions_as_menu", false)
    val mapProvider by DefValueStringDelegate(context, "feed_map_provider",
            MapProvider::class.qualifiedName!!)
    val enableHostsFilteringInWebView
            by BooleanDelegate(context, "feed_hosts_filtering", true)
    val directlyOpenLinksInBrowser
            by BooleanDelegate(context, "feed_directly_open_links", false)
    val conservativeRefreshes
            by BooleanDelegate(context, "feed_conservative_refreshes", true)
    val pullDownToRefresh
            by BooleanDelegate(context, "feed_pull_down_to_refresh", true)
    val useGecko
            by BooleanDelegate(context, "feed_gecko", BuildConfig.GECKO)
    val notifyUsersAboutNewArticlesOnFirstRun
            by BooleanDelegate(context, "feed_notify_articles_on_first_run", false)
    val notificationCount
            by NumberDelegate(context, "feed_synd_notification_count", 5.0)
    val toolbarOpacity
            by NumberDelegate(context, "feed_toolbar_opacity", 0.5)
    val useRSSMinicard
            by BooleanDelegate(context, "feed_rss_minicard", false)
    val hideActions
            by BooleanDelegate(context, "feed_hide_toolbar_actions2", false)
    val flatCardVerticalPadding
            by DipDimenDelegate(context, "feed_flat_card_vertical_margin", 16.0)
    val flatCardHorizontalPadding
            by DipDimenDelegate(context, "feed_flat_card_horizontal_margin", 16.0)
    val hideToolbar
            by BooleanDelegate(context, "feed_hide_title_bar", false)
    val cardCornerRadius
            by DipDimenDelegate(context, "feed_card_corner_radius", 0.0)
    val openingAnimationSpeed
            by NumberDelegate(context, "feed_opening_animation_speed", 0.5)
    val toolbarElevation
            by DipDimenDelegate(context, "feed_toolbar_elevation", 8.0)
    val articleNotifications
            by BooleanDelegate(context, "feed_article_notifications", false)
    val clockTimeZones
            by ListDelegate(context, "feed_clock_time_zones", listOf())
    val displayAnalogClock
            by BooleanDelegate(context, "feed_use_analog_clock", false)
    val cardCornerTreatment
            by DefValueStringDelegate(context, "feed_card_corner_treatment", "rnd")
    val chipCompactCard
            by BooleanDelegate(context, "feed_chip_provider_compact_card", false)
    val hideImageOperatorCards
            by BooleanDelegate(context, "feed_hide_image_operator_cards", false)
    val feedAnimationInterpolator
            by DefValueStringDelegate(context, "feed_animation_interpolator", "linear")
    val enableGsb
            by BooleanDelegate(context, "feed_enable_gsb", false)
    val feedProviders
            by ContainerListDelegate(context,
                    "feed_providers", listOf(
                    FeedWeatherStatsProvider::class.java.name,
                    FeedForecastProvider::class.java.name,
                    DeviceStateProvider::class.java.name,
                    CalendarEventProvider::class.java.name,
                    WikipediaNewsProvider::class.java.name,
                    WikinewsFeedProvider::class.java.name,
                    WikipediaNewsProvider::class.qualifiedName,
                    FeedWidgetsProvider::class.qualifiedName).map {
                FeedProviderContainer(it, null)
            })
    val disableSpringAnimation
            by BooleanDelegate(context, "feed_disable_rv_springs", false)

    companion object : SingletonHolder<FeedPersistence, Context>(::FeedPersistence)
}

class ContainerListDelegate(context: Context, key: String, defValue: List<FeedProviderContainer>) :
        SerializableListDelegate<FeedPersistence, FeedProviderContainer>(context, key, defValue) {
    override fun serialize(t: FeedProviderContainer): String = Gson().toJson(t)
    override fun deserialize(s: String) = Gson().fromJson(s,
            FeedProviderContainer::class.java)
}

val Context.feedPrefs get() = FeedPersistence.getInstance(this)
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

package ch.deletescape.lawnchair.feed

import android.annotation.SuppressLint
import android.content.Context
import ch.deletescape.lawnchair.LawnchairApp
import ch.deletescape.lawnchair.feed.chips.ChipCardProvider
import ch.deletescape.lawnchair.feed.contacts.FeedContactsProvider
import ch.deletescape.lawnchair.feed.dt.I18nDtClocksProvider
import ch.deletescape.lawnchair.feed.dynamic.DynamicProviderController
import ch.deletescape.lawnchair.feed.images.ImageProvider
import ch.deletescape.lawnchair.feed.images.bing.BingDailyImageProvider
import ch.deletescape.lawnchair.feed.images.nasa.ApodDailyImageProvider
import ch.deletescape.lawnchair.feed.images.ng.NgDailyImageProvider
import ch.deletescape.lawnchair.feed.maps.FeedLocationSearchProvider
import ch.deletescape.lawnchair.feed.news.NPRFeedProvider
import ch.deletescape.lawnchair.feed.notifications.MediaNotificationProvider
import ch.deletescape.lawnchair.feed.notifications.NotificationFeedProvider
import ch.deletescape.lawnchair.feed.widgets.FeedWidgetsProvider
import ch.deletescape.lawnchair.feed.wikipedia.news.ItnSyndicationProvider
import ch.deletescape.lawnchair.fromStringRes
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.persistence.feedPrefs
import com.android.launcher3.R

@SuppressLint("StaticFieldLeak")
private var theController: MainFeedController? = null
const val METADATA_CONTROLLER_PACKAGE = "special::controller_package";
fun getFeedController(c: Context): MainFeedController {
    if (theController == null) {
        theController = MainFeedController(c)
    }
    return theController!!;
}

class MainFeedController(val context: Context) {
    fun getProviders(): List<FeedProvider> {
        migrateToContainerSystem(context)
        migrateToPersistenceSystem(context)

        return (context.applicationContext as LawnchairApp).feedPrefs.feedProviders
                .map { it.instantiate(context) }
    }

    companion object {
        val substitutions =
                mapOf("ch.deletescape.lawnchair.feed.FeedWeatherProvider" to WeatherBarFeedProvider::class.qualifiedName)

        fun getDisplayName(provider: FeedProviderContainer, context: Context): String {
            return when {
                provider.name != null -> provider.name!!
                else -> when (provider.clazz) {
                    CalendarEventProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_calendar)
                    FeedWeatherStatsProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_weather_stats)
                    FeedForecastProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_forecast)
                    FeedDailyForecastProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_daily_forecast)
                    RemoteFeedProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_remote_feeds)
                    WikipediaNewsProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_wikipedia_news)
                    MediaNotificationProvider::class.qualifiedName -> context.getString(
                            R.string.event_provider_now_playing)
                    ItnSyndicationProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_wikipedia_news_synd)
                    WikipediaFunFactsProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_wikipedia_fun_facts)
                    WikinewsFeedProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_wikinews)
                    TheGuardianFeedProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_the_guardian)
                    BBCFeedProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_bbc)
                    I18nDtClocksProvider::class.qualifiedName -> R.string.title_pref_feed_world_clocks.fromStringRes(
                            context)
                    GSyndicationFeedProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_google_news)
                    CustomizableRSSProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_customizable_rss)
                    FeedLocationSearchProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_location_search)
                    DeviceStateProvider::class.qualifiedName -> R.string.title_feed_provider_device_state.fromStringRes(
                            context)
                    FeedSearchboxProvider::class.qualifiedName -> R.string.search.fromStringRes(
                            context)
                    FeedWidgetsProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_widgets)
                    DailySummaryFeedProvider::class.qualifiedName -> R.string.title_feed_provider_daily_summary.fromStringRes(
                            context)
                    PredictedAppsProvider::class.qualifiedName -> R.string.title_card_suggested_apps.fromStringRes(
                            context)
                    NotificationFeedProvider::class.qualifiedName -> R.string.event_provider_unread_notifications.fromStringRes(
                            context)
                    WebApplicationsProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_web_applications)
                    NoteListProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_note_list)
                    AlarmEventProvider::class.qualifiedName -> R.string.resuable_text_alarm.fromStringRes(
                            context)
                    FeedJoinedWeatherProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_joined_weather)
                    WeatherBarFeedProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_weather)
                    TheVergeFeedProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_the_verge)
                    ImageProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_images)
                    BingDailyImageProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_bing_daily)
                    ApodDailyImageProvider::class.qualifiedName -> R.string.title_image_provider_apod.fromStringRes(
                            context)
                    NgDailyImageProvider::class.qualifiedName -> R.string.title_image_provider_national_geographic.fromStringRes(
                            context)
                    FeedContactsProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_feed_contacts)
                    ChipCardProvider::class.qualifiedName -> R.string.pref_category_chips.fromStringRes(
                            context)
                    NPRFeedProvider::class.qualifiedName -> context.getString(
                            R.string.title_feed_provider_npr)
                    else -> error("no default or override name for provider ${provider.clazz}")
                }
            }
        }

        fun migrateToContainerSystem(context: Context) {
            context.lawnchairPrefs.beginBlockingEdit()
            if (context.lawnchairPrefs.feedProvidersLegacy.getAll().isNotEmpty()) {
                context.lawnchairPrefs.feedProvidersLegacy2
                        .setAll(context.lawnchairPrefs.feedProvidersLegacy.getAll().mapNotNull {
                            if (it != RemoteFeedProvider::class.qualifiedName) substitutions[it]
                                    ?: it else null
                        }.map { FeedProviderContainer(it, null) })
                context.lawnchairPrefs.feedProvidersLegacy.setAll(emptyList())
            }
        }

        fun migrateToPersistenceSystem(context: Context) {
            if (context.lawnchairPrefs.feedProvidersLegacy2.getAll().isNotEmpty()) {
                context.feedPrefs.feedProviders.clear()
                context.feedPrefs.feedProviders.addAll(
                        context.lawnchairPrefs.feedProvidersLegacy2.getAll())
                context.lawnchairPrefs.feedProvidersLegacy2.setAll(listOf())
            }
        }

        fun getFeedProviders(context: Context,
                             calledFromPrefs: Boolean = false): List<FeedProviderContainer> {
            if (!calledFromPrefs) {
                migrateToContainerSystem(context)
                migrateToPersistenceSystem(context)
            }
            return DynamicProviderController.getProviders()
        }
    }
}
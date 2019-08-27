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
import android.content.res.Resources
import ch.deletescape.lawnchair.LawnchairApp
import ch.deletescape.lawnchair.feed.RemoteFeedProvider.METADATA_CATEGORY
import ch.deletescape.lawnchair.feed.widgets.FeedWidgetsProvider
import ch.deletescape.lawnchair.fromStringRes
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R

@SuppressLint("StaticFieldLeak")
private var theController: MainFeedController? = null
public var METADATA_CONTROLLER_PACKAGE = "special::controller_package";
fun getFeedController(c: Context): MainFeedController {
    if (theController == null) {
        theController = MainFeedController(c)
    }
    return theController!!;
}

class MainFeedController(val context: Context) {
    fun getProviders(): List<FeedProvider> {
        migrateToContainerSystem(context);
        return (context.applicationContext as LawnchairApp).lawnchairPrefs.feedProviders.getList()
                .map { it.instantiate(context) }
    }

    companion object {
        val substitutions =
                mapOf("ch.deletescape.lawnchair.feed.FeedWeatherProvider" to FeedWeatherStatsProvider::class.java.name)

        fun getDisplayName(provider: FeedProviderContainer, context: Context): String {
            return when {
                provider.name != null -> provider.name!!
                else -> when (provider.clazz) {
                    CalendarEventProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_calendar)
                    FeedWeatherStatsProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_weather_stats)
                    FeedForecastProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_forecast)
                    FeedDailyForecastProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_daily_forecast)
                    RemoteFeedProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_remote_feeds)
                    WikipediaNewsProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_wikipedia_news)
                    WikipediaFunFactsProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_wikipedia_fun_facts)
                    WikinewsFeedProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_wikinews)
                    TheGuardianFeedProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_the_guardian)
                    BBCFeedProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_bbc)
                    GSyndicationFeedProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_google_news)
                    CustomizableRSSProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_customizable_rss)
                    DeviceStateProvider::class.java.name -> R.string.title_feed_provider_device_state.fromStringRes(
                            context)
                    FeedWidgetsProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_widgets)
                    DailySummaryFeedProvider::class.java.name -> R.string.title_feed_provider_daily_summary.fromStringRes(
                            context)
                    PredictedAppsProvider::class.java.name -> R.string.title_card_suggested_apps.fromStringRes(
                            context)
                    WebApplicationsProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_web_applications)
                    NoteListProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_note_list)
                    AlarmEventProvider::class.java.name -> R.string.resuable_text_alarm.fromStringRes(
                            context)
                    FeedJoinedWeatherProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_joined_weather)
                    WeatherBarFeedProvider::class.java.name -> context.getString(
                            R.string.title_feed_provider_weather)
                    else -> error("no default or override name for provider ${provider.clazz}")
                }
            }
        }

        fun migrateToContainerSystem(context: Context) {
            context.lawnchairPrefs.beginBlockingEdit()
            if (context.lawnchairPrefs.feedProvidersLegacy.getAll().isNotEmpty()) {
                context.lawnchairPrefs.feedProviders
                        .setAll(context.lawnchairPrefs.feedProvidersLegacy.getAll().mapNotNull {
                            if (it != RemoteFeedProvider::class.qualifiedName) substitutions[it]
                                                                               ?: it else null
                        }.map { FeedProviderContainer(it, null) })
                context.lawnchairPrefs.feedProvidersLegacy.setAll(emptyList())
            }
        }

        fun getFeedProviders(context: Context,
                             calledFromPrefs: Boolean = false): List<FeedProviderContainer> {
            if (!calledFromPrefs) {
                migrateToContainerSystem(context)
            }
            return listOf(CalendarEventProvider::class.java.name,
                          FeedWeatherStatsProvider::class.java.name,
                          FeedJoinedWeatherProvider::class.qualifiedName,
                          FeedDailyForecastProvider::class.java.name,
                          FeedForecastProvider::class.java.name,
                          NoteListProvider::class.java.name,
                          WeatherBarFeedProvider::class.java.name,
                          WikipediaNewsProvider::class.java.name,
                          WikipediaFunFactsProvider::class.java.name,
                          WikinewsFeedProvider::class.java.name,
                          TheGuardianFeedProvider::class.java.name,
                          BBCFeedProvider::class.java.name,
                          CustomizableRSSProvider::class.java.name,
                          GSyndicationFeedProvider::class.java.name,
                          DeviceStateProvider::class.java.name,
                          FeedWidgetsProvider::class.java.name,
                          DailySummaryFeedProvider::class.java.name,
                          PredictedAppsProvider::class.java.name,
                          WebApplicationsProvider::class.java.name,
                          AlarmEventProvider::class.java.name).map {
                FeedProviderContainer(it, null)
            } + RemoteFeedProvider.allProviders(context).map {
                FeedProviderContainer(RemoteFeedProvider::class.qualifiedName,
                                      mapOf(RemoteFeedProvider.COMPONENT_KEY to it.flattenToString(),
                                            METADATA_CONTROLLER_PACKAGE to it.packageName,
                                            RemoteFeedProvider.COMPONENT_CATEGORY to run {
                                                try {
                                                    return@run context.packageManager.getServiceInfo(
                                                            it, 0)?.metaData?.getString(
                                                            METADATA_CATEGORY) ?: "other"
                                                } catch (e: Resources.NotFoundException) {
                                                    return@run "other"
                                                } catch (e: ClassCastException) {
                                                    e.printStackTrace()
                                                    return@run "other"
                                                } catch (e: NullPointerException) {
                                                    e.printStackTrace()
                                                    return@run "other"
                                                }
                                            }),
                                      context.packageManager.getServiceInfo(it, 0).loadLabel(
                                              context.packageManager).toString())
            }.also { d("getFeedProvidersLegacy: feed providers are $it ") }
        }
    }
}
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

package ch.deletescape.lawnchair.feed.dynamic

import android.content.Context
import ch.deletescape.lawnchair.feed.*
import ch.deletescape.lawnchair.feed.chips.ChipCardProvider
import ch.deletescape.lawnchair.feed.contacts.FeedContactsProvider
import ch.deletescape.lawnchair.feed.images.ImageProvider
import ch.deletescape.lawnchair.feed.images.bing.BingDailyImageProvider
import ch.deletescape.lawnchair.feed.images.nasa.ApodDailyImageProvider
import ch.deletescape.lawnchair.feed.images.ng.NgDailyImageProvider
import ch.deletescape.lawnchair.feed.maps.FeedLocationSearchProvider
import ch.deletescape.lawnchair.feed.notifications.NotificationFeedProvider
import ch.deletescape.lawnchair.feed.widgets.FeedWidgetsProvider
import ch.deletescape.lawnchair.feed.wikipedia.news.ItnSyndicationProvider

class ProviderProviderDelegate(context: Context) : DynamicProviderDelegate(context) {
    override fun getAvailableContainers() = listOf(CalendarEventProvider::class.qualifiedName,
            FeedWeatherStatsProvider::class.qualifiedName,
            FeedJoinedWeatherProvider::class.qualifiedName,
            FeedDailyForecastProvider::class.qualifiedName,
            FeedForecastProvider::class.qualifiedName,
            FeedSearchboxProvider::class.qualifiedName,
            FeedContactsProvider::class.qualifiedName,
            ImageProvider::class.qualifiedName,
            NotificationFeedProvider::class.qualifiedName,
            ChipCardProvider::class.qualifiedName,
            BingDailyImageProvider::class.qualifiedName,
            ApodDailyImageProvider::class.qualifiedName,
            NgDailyImageProvider::class.qualifiedName,
            NoteListProvider::class.qualifiedName,
            WeatherBarFeedProvider::class.qualifiedName,
            WikipediaNewsProvider::class.qualifiedName,
            ItnSyndicationProvider::class.qualifiedName,
            WikipediaFunFactsProvider::class.qualifiedName,
            WikinewsFeedProvider::class.qualifiedName,
            TheGuardianFeedProvider::class.qualifiedName,
            BBCFeedProvider::class.qualifiedName,
            TheVergeFeedProvider::class.qualifiedName,
            CustomizableRSSProvider::class.qualifiedName,
            GSyndicationFeedProvider::class.qualifiedName,
            DeviceStateProvider::class.qualifiedName,
            FeedWidgetsProvider::class.qualifiedName,
            DailySummaryFeedProvider::class.qualifiedName,
            FeedLocationSearchProvider::class.qualifiedName,
            PredictedAppsProvider::class.qualifiedName,
            WebApplicationsProvider::class.qualifiedName,
            AlarmEventProvider::class.qualifiedName).map {
        FeedProviderContainer(it, null)
    }
}
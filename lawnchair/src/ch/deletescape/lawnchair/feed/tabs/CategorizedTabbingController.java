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

package ch.deletescape.lawnchair.feed.tabs;

import android.content.Context;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.feed.AbstractRSSFeedProvider;
import ch.deletescape.lawnchair.feed.AlarmEventProvider;
import ch.deletescape.lawnchair.feed.CalendarEventProvider;
import ch.deletescape.lawnchair.feed.DailySummaryFeedProvider;
import ch.deletescape.lawnchair.feed.FeedDailyForecastProvider;
import ch.deletescape.lawnchair.feed.FeedForecastProvider;
import ch.deletescape.lawnchair.feed.FeedJoinedWeatherProvider;
import ch.deletescape.lawnchair.feed.FeedProvider;
import ch.deletescape.lawnchair.feed.FeedSearchboxProvider;
import ch.deletescape.lawnchair.feed.FeedWeatherStatsProvider;
import ch.deletescape.lawnchair.feed.NoteListProvider;
import ch.deletescape.lawnchair.feed.RemoteFeedProvider;
import ch.deletescape.lawnchair.feed.WeatherBarFeedProvider;
import ch.deletescape.lawnchair.feed.WebApplicationsProvider;
import ch.deletescape.lawnchair.feed.WikipediaFunFactsProvider;
import ch.deletescape.lawnchair.feed.WikipediaNewsProvider;
import ch.deletescape.lawnchair.feed.chips.ChipCardProvider;
import ch.deletescape.lawnchair.feed.images.AbstractImageProvider;
import ch.deletescape.lawnchair.feed.widgets.FeedWidgetsProvider;

public class CategorizedTabbingController extends TabController {

    public Item TOOLS_TAB;
    public Item NEWS_TAB;
    public Item EVENTS_TAB;
    public Item MISC_TAB;
    public Item WIDGETS_TAB;

    public CategorizedTabbingController(
            @NotNull Context context) {
        super(context);
        TOOLS_TAB = new Item(context.getDrawable(R.drawable.ic_tooltip_image_outline_24dp),
                context.getString(R.string.category_tools));
        NEWS_TAB = new Item(context.getDrawable(R.drawable.ic_newspaper_24dp),
                context.getString(R.string.category_news));
        EVENTS_TAB = new Item(context.getDrawable(R.drawable.ic_event_black_24dp),
                context.getString(R.string.feed_category_events));
        MISC_TAB = new Item(context.getDrawable(R.drawable.ic_spa_black_24dp),
                context.getString(R.string.pref_category_misc));
        WIDGETS_TAB = new Item(context.getDrawable(R.drawable.ic_widget),
                context.getString(R.string.widget_button_text));
        WIDGETS_TAB.isWidgetTab = true;
    }

    @Override
    public List<Item> getAllTabs() {
        if (!FeatureFlags.GO_DISABLE_WIDGETS) {
            return Utilities.getLawnchairPrefs(getContext()).getFeedShowOtherTab() ?
                    Utilities.getLawnchairPrefs(getContext())
                            .getFeedCategorizeWidgetsAsSeparateTab()
                            ? Arrays
                            .asList(TOOLS_TAB, NEWS_TAB, EVENTS_TAB, WIDGETS_TAB, MISC_TAB)
                            : Arrays.asList(TOOLS_TAB, NEWS_TAB, EVENTS_TAB, MISC_TAB)
                    : Utilities.getLawnchairPrefs(getContext())
                            .getFeedCategorizeWidgetsAsSeparateTab()
                            ? Arrays.asList(TOOLS_TAB, NEWS_TAB, EVENTS_TAB, WIDGETS_TAB)
                            : Arrays.asList(TOOLS_TAB, NEWS_TAB, EVENTS_TAB);
        } else {
            return Utilities.getLawnchairPrefs(getContext()).getFeedShowOtherTab()
                    ? Arrays.asList(TOOLS_TAB, NEWS_TAB, EVENTS_TAB, MISC_TAB)
                    : Arrays.asList(TOOLS_TAB, NEWS_TAB, EVENTS_TAB);
        }
    }

    @Override
    public Map<Item, List<FeedProvider>> sortFeedProviders(List<FeedProvider> providers) {
        List<FeedProvider> tools, news, events, misc, widgets;
        tools = providers.stream().filter(it -> it instanceof FeedWeatherStatsProvider
                || it instanceof FeedForecastProvider
                || it instanceof FeedDailyForecastProvider
                || it instanceof DailySummaryFeedProvider
                || it instanceof WikipediaFunFactsProvider
                || it instanceof NoteListProvider
                || it instanceof WebApplicationsProvider
                || it instanceof FeedJoinedWeatherProvider
                || it instanceof WeatherBarFeedProvider
                || it instanceof AbstractImageProvider
                || it instanceof FeedSearchboxProvider
                || it instanceof ChipCardProvider
                || Objects
                .equals(it.getContainer().arguments.get(RemoteFeedProvider.COMPONENT_CATEGORY),
                        "tools")).collect(Collectors.toList());
        news = providers.stream().filter(it -> it instanceof AbstractRSSFeedProvider
                || it instanceof WikipediaNewsProvider
                || Objects
                .equals(it.getContainer().arguments.get(RemoteFeedProvider.COMPONENT_CATEGORY),
                        "news")).collect(
                Collectors.toList());
        events = providers.stream().filter(it -> it instanceof CalendarEventProvider
                || it instanceof AlarmEventProvider
                || Objects
                .equals(it.getContainer().arguments.get(RemoteFeedProvider.COMPONENT_CATEGORY),
                        "events")).collect(
                Collectors.toList());
        widgets = providers.stream().filter(it -> it instanceof FeedWidgetsProvider).collect(
                Collectors.toList());
        misc = providers.stream()
                .filter(it -> !(tools.contains(it) || news.contains(it) || events.contains(it)
                        || widgets.contains(it)))
                .collect(
                        Collectors.toList());
        HashMap<Item, List<FeedProvider>> result = new HashMap<>();
        result.put(TOOLS_TAB, tools);
        result.put(NEWS_TAB, news);
        result.put(EVENTS_TAB, events);
        if (Utilities.getLawnchairPrefs(getContext()).getFeedCategorizeWidgetsAsSeparateTab()
                && !FeatureFlags.GO_DISABLE_WIDGETS) {
            result.put(WIDGETS_TAB, widgets);
        }
        result.put(MISC_TAB, misc);
        return result;
    }
}

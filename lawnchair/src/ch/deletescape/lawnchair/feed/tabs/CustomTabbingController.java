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
import ch.deletescape.lawnchair.feed.FeedProvider;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class CustomTabbingController extends TabController {

    public Item MISC_TAB;

    public CustomTabbingController(@NotNull Context context) {
        super(context);
        MISC_TAB = new Item(null, context.getString(R.string.pref_category_misc));
    }

    @Override
    public List<Item> getAllTabs() {
        List<Item> tabs = Utilities.getLawnchairPrefs(getContext()).getFeedCustomTabs().stream()
                .map(it -> new Item(null, it.name)).collect(
                        Collectors.toList());
        if (Utilities.getLawnchairPrefs(getContext()).getFeedShowOtherTab()) {
            tabs.add(MISC_TAB);
        }
        return tabs;
    }

    @Override
    public Map<Item, List<FeedProvider>> sortFeedProviders(List<FeedProvider> providers) {
        if (getAllTabs().isEmpty()) {
            return Collections.singletonMap(null, providers);
        } else {
            Map<Item, List<FeedProvider>> result = new IdentityHashMap<>();
            for (CustomTab tab : Utilities.getLawnchairPrefs(getContext()).getFeedCustomTabs()) {
                List<FeedProvider> sorted = providers.stream()
                        .filter(it -> tab.providers.contains(it.getClass().getName())).collect(
                                Collectors.toList());
                result.put(new Item(null, tab.name), sorted);
            }
            if (Utilities.getLawnchairPrefs(getContext()).getFeedShowOtherTab()) {
                result.put(MISC_TAB, providers.stream().filter(provider -> !result.values().stream()
                        .anyMatch(it -> provider.equals(it))).collect(Collectors.toList()));
            }
            return result;
        }
    }
}

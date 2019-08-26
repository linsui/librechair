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
import ch.deletescape.lawnchair.feed.FeedProviderContainer;
import ch.deletescape.lawnchair.feed.MainFeedController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class ProviderTabbingController extends TabController {

    public Map<FeedProviderContainer, Item> providerItemMap;

    public ProviderTabbingController(@NotNull Context context) {
        super(context);
        this.providerItemMap = new LinkedHashMap<>();
        for (FeedProviderContainer provider : MainFeedController.Companion
                .getFeedProviders(context, false)) {
            providerItemMap.put(provider,
                    new Item(null, MainFeedController.Companion.getDisplayName(provider, context)));
        }
    }

    @Override
    public List<Item> getAllTabs() {
        return new ArrayList<>(providerItemMap.values());
    }

    @Override
    public Map<Item, List<FeedProvider>> sortFeedProviders(List<FeedProvider> providers) {
        Map<Item, List<FeedProvider>> result = new LinkedHashMap<>();
        for (FeedProviderContainer provider : providerItemMap.keySet()) {
            result.put(Objects.requireNonNull(providerItemMap.get(provider)),
                    providers.stream().filter(it -> Objects.equals(it.getContainer(), provider))
                            .collect(
                                    Collectors.toList()));
        }
        if (result.isEmpty()) {
            result.put(null, Collections.emptyList());
        }
        return result;
    }
}

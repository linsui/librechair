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

package ch.deletescape.lawnchair.feed.dynamic;

import android.content.Context;

import com.android.launcher3.Utilities;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.feed.FeedProviderContainer;
import ch.deletescape.lawnchair.feed.news.CustomSyndicationProvider;

public class CustomFeedsDelegate extends DynamicProviderDelegate {
    public CustomFeedsDelegate(Context context) {
        super(context);
    }

    private static FeedProviderContainer createContainer(String url) {
        return new FeedProviderContainer(CustomSyndicationProvider.class.getName(),
                Collections.singletonMap(CustomSyndicationProvider.URL, url), url);
    }

    @Override
    public List<FeedProviderContainer> getAvailableContainers() {
        return Utilities.getLawnchairPrefs(getContext()).getFeedRSSSources().getList()
                .stream()
                .map(CustomFeedsDelegate::createContainer)
                .collect(Collectors.toList());
    }
}

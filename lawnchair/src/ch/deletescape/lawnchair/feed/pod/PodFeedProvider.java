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

package ch.deletescape.lawnchair.feed.pod;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import ch.deletescape.lawnchair.feed.Card;
import ch.deletescape.lawnchair.feed.FeedProvider;

@SuppressWarnings("WeakerAccess")
public abstract class PodFeedProvider extends FeedProvider {
    private FeedPod activePod;
    private Supplier<FeedPod> defaultPod;

    private Consumer<Consumer<FeedPod>> changeCallback;

    public PodFeedProvider(Context c,
                           Supplier<FeedPod> defaultPod1) {
        this(c, new HashMap<>(), defaultPod1);
    }

    protected PodFeedProvider(Context c, Map<String, String> arguments,
                              Supplier<FeedPod> defaultPod) {
        super(c, arguments);
        this.defaultPod = defaultPod;
    }

    public void setChangeCallback(
            Consumer<Consumer<FeedPod>> changeCallback) {
        this.changeCallback = changeCallback;
    }

    public void setDefaultPod(Supplier<FeedPod> dp) {
        this.defaultPod = dp;
    }

    @Override
    public void onFeedShown() {

    }

    @Override
    public void onFeedHidden() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public final List<Card> getCards() {
        if (activePod == null) {
            activePod = defaultPod.get();
        }
        if (changeCallback != null) {
            changeCallback.accept(pod -> activePod = pod);
        }
        return activePod.getCards();
    }
}

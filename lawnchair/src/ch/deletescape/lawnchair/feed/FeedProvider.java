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

package ch.deletescape.lawnchair.feed;

import android.content.Context;
import ch.deletescape.lawnchair.feed.impl.LauncherFeed;
import java.util.List;

public abstract class FeedProvider {

    private Context context;
    private FeedAdapter adapter;
    private LauncherFeed feed;
    private boolean requestedRefresh;

    public FeedProvider(Context c) {
        context = c;
    }

    public Context getContext() {
        return context;
    }


    public abstract void onFeedShown();


    public abstract void onFeedHidden();


    public abstract void onCreate();


    public abstract void onDestroy();


    public abstract List<Card> getCards();

    protected void onAttachedToAdapter(FeedAdapter adapter) {
        this.adapter = adapter;
    }

    @SuppressWarnings("WeakerAccess")
    protected void requestRefresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public int getBackgroundColor() {
        return adapter == null ? 0 : adapter.getBackgroundColor();
    }

    public LauncherFeed getFeed() {
        return feed;
    }

    public void setFeed(LauncherFeed feed) {
        this.feed = feed;
    }
}

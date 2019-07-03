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
import java.util.List;

public abstract class FeedProvider {

    private Context context;
    private boolean shouldRefresh;
    private long refreshIntervalMillis;

    public FeedProvider(Context c) {
        context = c;
    }

    public Context getContext() {
        return context;
    }

    /*
     * This function will be called periodically once per refreshIntervalMillis if shouldRefresh
     */
    public abstract void refresh();
    /*
     * This function is called whenever the feed adapter is shown
     */
    public abstract void onFeedShown();
    /*
     * This function may be called when the feed adapter is destroyed (this is not guaranteed)
     */
    public abstract void onFeedHidden();
    /*
     * All initialization work should be done here; this is the entry point in the lifecycle
     */
    public abstract void onCreate();
    /*
     * All finalization work should be done here; this is the exit point in the lifecycle
     */
    public abstract void onDestroy();
    /*
     * Get a list of cards that should be displayed in the feed
     */
    public abstract List<Card> getCards();

    public long getRefreshIntervalMillis() {
        return refreshIntervalMillis;
    }

    public void setRefreshIntervalMillis(long refreshIntervalMillis) {
        this.refreshIntervalMillis = refreshIntervalMillis;
    }

    public boolean isShouldRefresh() {
        return shouldRefresh;
    }

    public void setShouldRefresh(boolean shouldRefresh) {
        this.shouldRefresh = shouldRefresh;
    }
}

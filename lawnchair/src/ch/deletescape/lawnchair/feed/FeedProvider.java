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
import android.graphics.drawable.Drawable;
import android.view.WindowManager;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import ch.deletescape.lawnchair.feed.impl.FeedController;
import ch.deletescape.lawnchair.feed.impl.LauncherFeed;

@SuppressWarnings("unchecked")
public abstract class FeedProvider {

    private Context context;
    private Map<String, String> arguments;
    private FeedAdapter adapter;
    private LauncherFeed feed;
    private FeedProviderContainer container;
    private WindowManager windowService;
    private boolean hasUnread;


    public FeedProvider(Context c) {
        this(c, new HashMap<>());
    }

    public FeedProvider(Context c, Map<String, String> arguments) {
        this.context = c;
        this.arguments = arguments;
        this.windowService = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
    }

    public Context getContext() {
        return context;
    }

    public boolean isVolatile() {
        return false;
    }

    public boolean isActionFree() {
        return false;
    }

    public boolean isSearchable() {
        return false;
    }

    public FeedAdapter getAdapter() {
        return adapter;
    }

    public abstract List<Card> getCards();

    protected void onAttachedToAdapter(FeedAdapter adapter) {
        this.adapter = adapter;
    }

    public int getBackgroundColor() {
        return feed != null ? feed.getBackgroundColor() :
                adapter == null ? 0 : adapter.getBackgroundColor();
    }

    public LauncherFeed getFeed() {
        return feed;
    }

    void setContext(Context context) {
        this.context = context;
    }

    public void setFeed(LauncherFeed feed) {
        this.feed = feed;
    }

    public List<Card> getPreviewItems() {
        return Collections.EMPTY_LIST;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    public FeedProviderContainer getContainer() {
        return container;
    }

    public void setContainer(FeedProviderContainer container) {
        this.container = container;
    }

    public List<Action> getActions(boolean exclusive) {
        return Collections.EMPTY_LIST;
    }

    public void markRead() {
        this.hasUnread = false;
        if (feed != null) {
            feed.onUnreadStateChanged();
        }
    }

    public void markUnread() {
        this.hasUnread = true;
        if (feed != null) {
            feed.onUnreadStateChanged();
        }
    }

    public boolean hasUnread() {
        return hasUnread;
    }

    protected void requestRefreshFeed() {
        if (feed != null) {
            Executors.newSingleThreadExecutor().submit(() -> feed.refresh(0, 0, true, true));
        } else if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public WindowManager getWindowService() {
        return windowService;
    }

    @Nullable
    public FeedController getControllerView() {
        if (getFeed() != null) {
            return getFeed().getFeedController();
        } else {
            return null;
        }
    }

    public static class Action {
        public Action(Drawable item, String name, Runnable onClick) {
            this.icon = item;
            this.name = name;
            this.onClick = onClick;
        }

        public String name;
        public Runnable onClick;
        public Drawable icon;
    }

}

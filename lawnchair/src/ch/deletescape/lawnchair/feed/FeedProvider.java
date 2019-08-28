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

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION;
import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import ch.deletescape.lawnchair.LawnchairLauncher;
import ch.deletescape.lawnchair.feed.impl.LauncherFeed;
import ch.deletescape.lawnchair.theme.ThemeOverride;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public abstract class FeedProvider {

    private Context context;
    private Map<String, String> arguments;
    private FeedAdapter adapter;
    private LauncherFeed feed;
    private boolean requestedRefresh;
    private FeedProviderContainer container;
    private WindowManager windowService;

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

    protected void displayView(Function1<? super ViewGroup, ? extends View> inflateHelper, float x,
            float y) {
        if (feed != null) {
            feed.displayView(inflateHelper, x, y);
        } else if (Launcher.getLauncherOrNull(context) != null) {
            Launcher.getLauncherOrNull(context).getStateManager().goToState(LauncherState.OPTIONS);
            LayoutParams params = new WindowManager.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT,
                    TYPE_APPLICATION_ATTACHED_DIALOG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
            params.type = TYPE_APPLICATION;
            View overlayView = inflateHelper.invoke(new LinearLayout(new ContextThemeWrapper(context,
                    new ThemeOverride.AlertDialog().getTheme(context))));
            windowService
                    .addView(overlayView, params);
            ((LawnchairLauncher) Launcher.getLauncherOrNull(context)).setBackPressedCallback(() -> {
                windowService.removeView(overlayView);
                ((LawnchairLauncher) Launcher.getLauncherOrNull(context)).setBackPressedCallback(null);
                return Unit.INSTANCE;
            });
        } else {
            new AlertDialog.Builder(context, new ThemeOverride.AlertDialog().getTheme(context))
                    .setView(inflateHelper.invoke(new LinearLayout(new ContextThemeWrapper(context,
                            new ThemeOverride.AlertDialog().getTheme(context))))).show();
        }
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
}

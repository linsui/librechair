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

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;

import java.util.ArrayList;
import java.util.List;

import ch.deletescape.lawnchair.LawnchairLauncher;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.impl.LauncherFeed;
import ch.deletescape.lawnchair.theme.ThemeOverride;
import kotlin.Pair;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION;
import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

public abstract class ProviderScreen extends ContextWrapper {

    private FeedProvider provider;
    private LauncherFeed feed;

    private List<FeedProvider.Action> actions;

    public ProviderScreen(Context base) {
        super(base);
        actions = new ArrayList<>();
    }

    protected abstract View getView(ViewGroup parent);

    protected abstract void bindView(View view);

    protected void addAction(FeedProvider.Action action) {
        if (feed != null) {
            actions.add(action);
            feed.getScreenActions().put(this, actions);
            feed.updateActions();
        } else {
            actions.add(action);
        }
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public final void display(LauncherFeed feed, int tX, int tY) {
        this.feed = feed;
        feed.displayProviderScreen(this, tX, tY, viewGroup -> {
            View v = getView(viewGroup);
            bindView(v);
            return v;
        });
        feed.getScreenActions().put(this, actions);
        feed.updateActions();
    }

    public final void display(ProviderScreen screen, int tX, int tY) {
        if (feed != null && provider == null) {
            display(feed, tX, tY);
        } else if (screen.provider != null) {
            display(screen.provider, tX, tY);
        } else {
            throw new IllegalStateException("The provider you were trying to display" +
                    " is not bound to a valid container object!");
        }
    }

    public final void display(FeedProvider provider, int touchX, int touchY) {
        this.provider = provider;
        if (provider.getFeed() != null) {
            display(provider.getFeed(), touchX, touchY);
        } else if (Launcher.getLauncherOrNull(provider.getContext()) != null) {
            Launcher.getLauncher(provider.getContext()).getStateManager()
                    .goToState(LauncherState.NEWS_OVERLAY, false);
            LayoutParams params = new WindowManager.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT,
                    TYPE_APPLICATION_OVERLAY,
                    LayoutParams.FLAG_NOT_FOCUSABLE
                            | LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | LayoutParams.FLAG_LAYOUT_IN_OVERSCAN
                            | LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
            params.type = TYPE_APPLICATION;
            View overlayView = getView(new LinearLayout(new ContextThemeWrapper(this,
                    new ThemeOverride.Settings().getTheme(this))));
            bindView(overlayView);
            overlayView.setVisibility(View.INVISIBLE);
            overlayView.setBackground(new ColorDrawable(
                    LawnchairUtilsKt.setAlpha(provider.getBackgroundColor(), Math.max(175,
                            LawnchairUtilsKt.getAlpha(provider.getBackgroundColor())))));
            overlayView.setOnApplyWindowInsetsListener((v, insets) -> {
                overlayView.setPadding(overlayView.getPaddingLeft(),
                        overlayView.getPaddingTop() + insets.getStableInsetTop(),
                        overlayView.getPaddingRight(),
                        insets.getStableInsetBottom() + overlayView.getPaddingBottom());
                return insets;
            });
            overlayView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    /*
                     * There must be a better, cleaner way of doing this!
                     */
                    int radius = (int) Math.hypot(Integer.MAX_VALUE / 32,
                            Integer.MAX_VALUE / 32);
                    overlayView.getViewTreeObserver().removeOnPreDrawListener(this);
                    Animator animator = ViewAnimationUtils
                            .createCircularReveal(overlayView, (int) touchX, (int) touchY, 0,
                                    radius);
                    animator.setDuration(50000);
                    overlayView.setVisibility(View.VISIBLE);
                    animator.start();
                    return true;
                }
            });
            provider.getWindowService().addView(overlayView, params);
            ((LawnchairLauncher) Launcher.getLauncher(
                    provider.getContext())).getProviderScreens().add(new Pair<>(this, overlayView));
        } else {
            View overlayView = getView(new LinearLayout(new ContextThemeWrapper(this,
                    new ThemeOverride.AlertDialog().getTheme(this))));
            overlayView.setVisibility(View.INVISIBLE);
            new AlertDialog.Builder(this, new ThemeOverride.AlertDialog().getTheme(this))
                    .setView(overlayView).show();
        }
    }
}

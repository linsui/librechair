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

import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import ch.deletescape.lawnchair.LawnchairLauncher;
import com.android.launcher3.Launcher;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// TODO We need to figure out how overlays work, but in the meanwhile we'll just use DrawerLayout to implement the desktop feed

public class FeedOverlay implements Launcher.LauncherOverlay {

    private final LawnchairLauncher mLauncher;
    private float progress = 0;
    private Launcher.LauncherOverlayCallbacks mOverlayCallbacks;

    public FeedOverlay(LawnchairLauncher launcher) {
        mLauncher = launcher;
    }

    @Override
    public void onScrollChange(float progress, boolean rtl) {
        Log.d(getClass().getName(), "onScrollChange: " + progress);
        if (progress < 0.1) {
            mLauncher.getDrawerLayout().closeDrawer(Gravity.START);
            return;
        }
        try {
            Method moveDrawerToOffset = DrawerLayout.class
                    .getDeclaredMethod("moveDrawerToOffset", View.class, float.class);
            moveDrawerToOffset.setAccessible(true);
            moveDrawerToOffset
                    .invoke(mLauncher.getDrawerLayout(), mLauncher.getDrawerLayout().getChildAt(1),
                            progress);
            mOverlayCallbacks.onScrollChanged(progress);
            this.progress = progress;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            this.progress = 0;
        }
        mOverlayCallbacks.onScrollChanged(progress);
    }

    @Override
    public void onScrollInteractionBegin() {
        Log.d(getClass().getName(), "onScrollInteractionBegin: scroll interaction has begin");
    }

    @Override
    public void onScrollInteractionEnd() {
        if (progress > 0.4) {
            mOverlayCallbacks.onScrollChanged(1f);
            mLauncher.getDrawerLayout().openDrawer(Gravity.START);
            mOverlayCallbacks.onScrollChanged(progress);
        } else {
            mOverlayCallbacks.onScrollChanged(0);
            mLauncher.getDrawerLayout().closeDrawer(Gravity.START);
        }
        Log.d(getClass().getName(), "onScrollInteractionBegin: scroll interaction has ended");
    }

    @Override
    public void setOverlayCallbacks(Launcher.LauncherOverlayCallbacks cb) {
        mOverlayCallbacks = cb;
    }
}

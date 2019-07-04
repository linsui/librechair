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

import android.util.Log;
import com.android.launcher3.Launcher;

public class FeedOverlay implements Launcher.LauncherOverlay {

    private final Launcher mLauncher;
    private Launcher.LauncherOverlayCallbacks mOverlayCallbacks;

    public FeedOverlay(Launcher launcher) {
        mLauncher = launcher;
    }

    @Override
    public void onScrollChange(float progress, boolean rtl) {
        Log.d(getClass().getName(), "onScrollChange: " + progress);
        mOverlayCallbacks.onScrollChanged(progress);
    }

    @Override
    public void onScrollInteractionBegin() {
        Log.d(getClass().getName(), "onScrollInteractionBegin: scroll interaction has begin");
    }

    @Override
    public void onScrollInteractionEnd() {
        Log.d(getClass().getName(), "onScrollInteractionBegin: scroll interaction has ended");
    }

    @Override
    public void setOverlayCallbacks(Launcher.LauncherOverlayCallbacks cb) {
        mOverlayCallbacks = cb;
    }
}

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

package com.android.launcher3.plugin;

import static com.android.launcher3.plugin.activity.ActivityPluginClient.STATE_ATTACHED;
import static com.android.launcher3.plugin.activity.ActivityPluginClient.STATE_CREATED;
import static com.android.launcher3.plugin.activity.ActivityPluginClient.STATE_RESUMED;
import static com.android.launcher3.plugin.activity.ActivityPluginClient.STATE_STARTED;

import android.os.Bundle;
import com.android.launcher3.plugin.activity.ActivityPluginClient;
import com.google.android.apps.nexuslauncher.NexusLauncherActivity;

/**
 * Launcher activity extension that sends state information to {@link ActivityPluginClient}.
 */
public abstract class PluginLauncher extends NexusLauncherActivity {

    private ActivityPluginClient mActivityClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PluginManager manager = PluginManager.getInstance(this);
        manager.reloadConnections();

        mActivityClient = manager.getClient(ActivityPluginClient.class);
        mActivityClient.addStateFlag(STATE_CREATED);
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivityClient.addStateFlag(STATE_STARTED);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivityClient.addStateFlag(STATE_RESUMED);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mActivityClient.addStateFlag(STATE_ATTACHED);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mActivityClient.removeStateFlag(STATE_ATTACHED);
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivityClient.removeStateFlag(STATE_RESUMED);
    }

    @Override
    public void onStop() {
        super.onStop();
        mActivityClient.removeStateFlag(STATE_STARTED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivityClient.removeStateFlag(STATE_CREATED);
    }
}

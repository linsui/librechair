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

package com.android.launcher3.plugin.shortcuts;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Process;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.plugin.PluginManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import java.util.ArrayList;
import java.util.List;

class ShortcutExtension {

    private final Context mContext;
    private final LauncherAppsCompat mApps;

    ShortcutExtension(Context context) {
        mContext = context;
        mApps = LauncherAppsCompat.getInstance(context);
    }

    private ShortcutPluginClient getShortcutClient() {
        return PluginManager.getInstance(mContext).getClient(ShortcutPluginClient.class);
    }

    public Drawable getShortcutIconDrawable(ShortcutInfoCompat info, int density) {
        ShortcutInfoCompatExt ext = (ShortcutInfoCompatExt) info;
        return ext.getIcon(mContext, density);
    }

    public List<ShortcutInfoCompat> getForActivity(String packageName, ComponentName activity) {
        List<ShortcutInfoCompat> out = new ArrayList<>();

        List<LauncherActivityInfo> infoList =
                mApps.getActivityList(packageName, Process.myUserHandle());
        for (LauncherActivityInfo info : infoList) {
            ComponentName cn = info.getComponentName();
            if (activity == null || activity.equals(cn)) {
                for (ShortcutPluginClient.ShortcutWithIcon shortcut
                        : getShortcutClient().queryShortcuts(packageName, activity)) {
                    out.add(new ShortcutInfoCompatExt(cn, shortcut));
                }
            }
        }
        return out;
    }
}
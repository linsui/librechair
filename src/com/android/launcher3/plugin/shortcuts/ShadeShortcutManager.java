/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
 *     Copyright (c) 2019 Amir Zaidi
 *
 *     This file is alternatively licensed under the terms of the Apache License, 2.0
 *     wherever it is applicable
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
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import java.util.List;

public class ShadeShortcutManager {

    private final Context mContext;
    public final ShortcutExtension mExtension;
    private DeepShortcutManager mManager;
    private boolean mLastCallExternal;

    public ShadeShortcutManager(Context context, DeepShortcutManager manager) {
        mContext = context;
        mExtension = new ShortcutExtension(context);
        mManager = manager;
    }

    public void startShortcut(String packageName, String id, Intent intent,
            Bundle startActivityOptions, UserHandle user) {
        if (ShortcutInfoCompatExt.isExternal(id)) {
            mContext.startActivity(intent, startActivityOptions);
        } else {
            mManager.startShortcutReal(packageName, id, intent, startActivityOptions, user);
        }
    }

    public Drawable getShortcutIconDrawable(ShortcutInfoCompat shortcutInfo, int density) {
        mLastCallExternal = shortcutInfo instanceof ShortcutInfoCompatExt;
        return mLastCallExternal
                ? mExtension.getShortcutIconDrawable(shortcutInfo, density)
                : mManager.getShortcutIconDrawableReal(shortcutInfo, density);
    }

    public List<ShortcutInfoCompat> getAll(String packageName,
            ComponentName activity) {
        mLastCallExternal = true;
        return mExtension.getForActivity(packageName, activity);
    }
}
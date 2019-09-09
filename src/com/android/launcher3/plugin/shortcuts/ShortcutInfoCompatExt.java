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
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;
import androidx.annotation.NonNull;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import java.net.URISyntaxException;

public class ShortcutInfoCompatExt extends ShortcutInfoCompat {

    private static final String EXTERNAL_PREFIX = "shortcut-external-";

    static boolean isExternal(String id) {
        return id.startsWith(EXTERNAL_PREFIX);
    }

    private final String mPackageName;
    private final ComponentName mActivity;

    private final ShortcutPluginClient.ShortcutWithIcon mShortcut;

    public ShortcutInfoCompatExt(ComponentName activity,
            ShortcutPluginClient.ShortcutWithIcon shortcut) {
        super(null);

        mPackageName = activity.getPackageName();
        mActivity = activity;
        mShortcut = shortcut;
    }

    public Drawable getIcon(Context context, int density) {
        return new BitmapDrawable(context.getResources(), mShortcut.getIcon(density));
    }

    @Override
    public Intent makeIntent() {
        try {
            String uri = mShortcut.getBundle().getString("intent");
            String id = getId();
            return Intent.parseUri(uri, Intent.URI_INTENT_SCHEME).putExtra(EXTRA_SHORTCUT_ID, id);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getPackage() {
        return mPackageName;
    }

    @Override
    public String getId() {
        return EXTERNAL_PREFIX + mShortcut.getBundle().getString("id");
    }

    @Override
    public CharSequence getShortLabel() {
        return mShortcut.getBundle().getString("shortLabel");
    }

    @Override
    public CharSequence getLongLabel() {
        return mShortcut.getBundle().getString("longLabel");
    }

    @Override
    public ComponentName getActivity() {
        Intent intent = makeIntent();
        return intent.getComponent() == null ? mActivity : intent.getComponent();
    }

    @Override
    public UserHandle getUserHandle() {
        return Process.myUserHandle();
    }

    @Override
    public boolean isPinned() {
        return false;
    }

    @Override
    public boolean isDeclaredInManifest() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return mShortcut.getBundle().getBoolean("enabled");
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public int getRank() {
        return mShortcut.getBundle().getInt("rank");
    }

    @Override
    public CharSequence getDisabledMessage() {
        return mShortcut.getBundle().getString("disabledMessage");
    }

    @NonNull
    @Override
    public String toString() {
        return "ShortcutInfoCompatVL{" + makeIntent().toString() + "}";
    }

    public boolean canBePinned() {
        return false;
    }
}
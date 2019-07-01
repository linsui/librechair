package com.google.android.apps.nexuslauncher.allapps;

import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;

public class Action {
    public final String badgePackage;
    public final CharSequence contentDescription;
    public final long expirationTimeMillis;
    public final String id;
    public boolean isEnabled = false;
    public final CharSequence openingPackageDescription;
    public final long position;
    public final String publisherPackage;
    public final ShortcutInfoCompat shortcut;
    public final String shortcutId;
    public final ShortcutInfo shortcutInfo;

    public Action(String var1, String var2, long var3, String var5, String var6, CharSequence var7, ShortcutInfoCompat var8, ShortcutInfo var9, long var10) {
        this.id = var1;
        this.shortcutId = var2;
        this.expirationTimeMillis = var3;
        this.publisherPackage = var5;
        this.badgePackage = var6;
        this.openingPackageDescription = var7;
        this.shortcut = var8;
        this.shortcutInfo = var9;
        this.position = var10;
        this.contentDescription = var9.contentDescription;
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("{");
        var1.append(this.id);
        var1.append(",");
        var1.append(this.shortcut.getShortLabel());
        var1.append(",");
        var1.append(this.position);
        var1.append("}");
        return var1.toString();
    }
}

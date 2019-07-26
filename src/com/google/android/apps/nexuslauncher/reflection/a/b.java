//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.android.apps.nexuslauncher.reflection.a;

import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import android.os.UserHandle;
import java.util.HashMap;
import java.util.Map;

public class b {

    public static final String[] PLACEHOLDERS = new String[] {
            "org.fdroid.fdroid",
            "org.chromium.chromium",
            "com.android.settings",
            "com.android.gallery3d",
            "org.lineageos.snap",
            "com.android.camera2",
            "org.lineageos.jelly",
            "com.android.browser",
            "com.android.calculator2" /* LIBRE-CHANGED: remove Google app placeholders */
    };
    public final a[] aU;
    public final Map<String, a> aV;
    private final UserHandle aW;
    public final PackageManager mPackageManager;

    public b(Context var1) {
        this.aU = new a[PLACEHOLDERS.length];
        this.aV = new HashMap();
        this.mPackageManager = var1.getPackageManager();
        this.aW = Process.myUserHandle();

        for (int var2 = 0; var2 < PLACEHOLDERS.length; ++var2) {
            a var3 = new a(PLACEHOLDERS[var2], "", -1);
            this.aU[var2] = var3;
            this.aV.put(PLACEHOLDERS[var2], var3);
        }

    }

    public final void a(int var1, LauncherActivityInfo var2) throws Throwable {
        synchronized (this) {
        }

        Throwable var10000;
        label84:
        {
            boolean var10001;
            a var11;
            try {
                if (!this.aW.equals(var2.getUser())) {
                    return;
                }

                String var3 = var2.getComponentName().getPackageName();
                var11 = (a) this.aV.get(var3);
            } catch (Throwable var9) {
                var10000 = var9;
                var10001 = false;
                break label84;
            }

            if (var11 == null) {
                return;
            }

            label75:
            try {
                var11.state = var1;
                var11.className = var2.getComponentName().getClassName();
                return;
            } catch (Throwable var8) {
                var10000 = var8;
                var10001 = false;
                break label75;
            }
        }

        Throwable var10 = var10000;
        throw var10;
    }

    public final void a(int var1, String var2, UserHandle var3) {
        if (this.aW.equals(var3)) {
            a var4 = (a) this.aV.get(var2);
            if (var4 != null) {
                var4.state = var1;
            }
        }

    }

    public final void a(int var1, String[] var2, UserHandle var3) {
        int var4 = var2.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            this.a(var1, var2[var5], var3);
        }

    }
}

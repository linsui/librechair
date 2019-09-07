/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.quickstep;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserManager;
import android.util.Log;

import com.android.launcher3.BuildConfig;
import com.android.launcher3.MainProcessInitializer;
import com.android.launcher3.Utilities;
import com.android.systemui.shared.system.ThreadedRendererCompat;

@SuppressWarnings("unused")
public class QuickstepProcessInitializer extends MainProcessInitializer {

    private static final String TAG = "QuickstepProcessInitializer";
    private static final int HEAP_LIMIT_MB = 250;

    public QuickstepProcessInitializer(Context context) { }

    @Override
    protected void init(Context context) {
        if (Utilities.IS_DEBUG_DEVICE) {
            try {
                // Trigger a heap dump if the PSS reaches beyond the target heap limit
                final ActivityManager am = context.getSystemService(ActivityManager.class);
                am.setWatchHeapLimit(HEAP_LIMIT_MB * 1024 * 1024);
            } catch (SecurityException e) {
                // Do nothing
            }
        }

        // Workaround for b/120550382, an external app can cause the launcher process to start for
        // a work profile user which we do not support. Disable the application immediately when we
        // detect this to be the case.
        UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
        if (um.isManagedProfile()) {
            PackageManager pm = context.getPackageManager();
            pm.setApplicationEnabledSetting(context.getPackageName(),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0 /* flags */);
            Log.w(TAG, "Disabling " + BuildConfig.APPLICATION_ID
                    + ", unable to run in a managed profile");
            return;
        }

        super.init(context);

        // Elevate GPU priority for Quickstep and Remote animations.
        if (Utilities.ATLEAST_Q) {
            try {
                ThreadedRendererCompat.setContextPriority(
                        ThreadedRendererCompat.EGL_CONTEXT_PRIORITY_HIGH_IMG);
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
            }
        }
    }
}

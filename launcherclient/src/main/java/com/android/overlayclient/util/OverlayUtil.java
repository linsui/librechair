/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.android.overlayclient.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class OverlayUtil {
    public static List<ApplicationInfo> availableFeedProviders(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent("com.android.launcher3.WINDOW_OVERLAY")
                .setData(Uri.parse("app://" + context.getPackageName()));
        List<ApplicationInfo> aiList = new ArrayList<>();
        for (ResolveInfo ri : pm.queryIntentServices(intent, PackageManager.GET_RESOLVED_FILTER)) {
            if (ri.serviceInfo != null) {
                ApplicationInfo ai = ri.serviceInfo.applicationInfo;
            }
        }
        return aiList;
    }
}

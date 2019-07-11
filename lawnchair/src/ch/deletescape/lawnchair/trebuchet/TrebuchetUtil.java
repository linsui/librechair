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

package ch.deletescape.lawnchair.trebuchet;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import java.util.ArrayList;
import java.util.List;

public class TrebuchetUtil {
    public static List<ApplicationInfo> availableQuickstepProviders(Context context) {
        List<ApplicationInfo> infos = new ArrayList<>();
        for (ApplicationInfo packageInfo : context.getPackageManager().getInstalledApplications(0)) {
            if (isQuickstepProvider(packageInfo.packageName, context)) {
                infos.add(packageInfo);
            }
        }
        return infos;
    }

    public static boolean isQuickstepProvider(String packageName, Context context) {
        Intent intent = new Intent("android.intent.action.QUICKSTEP_SERVICE").setPackage(packageName);
        return context.getPackageManager().resolveService(intent, 0) != null;
    }
}

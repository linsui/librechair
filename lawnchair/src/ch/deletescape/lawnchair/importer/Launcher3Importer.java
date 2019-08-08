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

package ch.deletescape.lawnchair.importer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import com.android.launcher3.provider.ImportDataTask;
import java.io.File;
import org.jetbrains.annotations.NotNull;

public class Launcher3Importer implements Importer {

    @Override
    public boolean available(@NotNull ApplicationInfo pkg) {
        return pkg.packageName.equals("com.android.launcher3") || pkg.packageName
                .equals("org.lineageos.trebuchet") || pkg.packageName
                .equals("com.android.quickstep");
    }

    @Override
    public void importWorkspace(@NotNull ApplicationInfo pkg, Context context) {
        // TODO
    }

    @NotNull
    @Override
    public File getSharedPreferencesFile(@NotNull ApplicationInfo pkg) {
        return null;
    }

    @Override
    public void migrateSharedPreferences(@NotNull ApplicationInfo pkg,
            @NotNull SharedPreferences sharedPreferences) {

    }
}

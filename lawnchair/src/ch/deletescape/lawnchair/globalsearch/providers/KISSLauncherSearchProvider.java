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

package ch.deletescape.lawnchair.globalsearch.providers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import ch.deletescape.lawnchair.globalsearch.SearchProvider;
import com.android.launcher3.R;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

public class KISSLauncherSearchProvider extends SearchProvider {

    public KISSLauncherSearchProvider(@NotNull Context context) {
        super(context);
    }

    @Override
    public boolean isAvailable() {
        try {
            getContext().getPackageManager().getPackageInfo("fr.neamar.kiss", 0);
            return true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @NotNull
    @Override
    public String getName() {
        return getContext().getString(R.string.search_provider_just_search);
    }

    @Override
    public boolean getSupportsVoiceSearch() {
        return false;
    }

    @Override
    public boolean getSupportsAssistant() {
        return false;
    }

    @Override
    public boolean getSupportsFeed() {
        return false;
    }

    @Override
    public void startSearch(@NotNull Function1<? super Intent, Unit> callback) {
        Intent i = new Intent();
        i.setComponent(new ComponentName("fr.neamar.kiss", "fr.neamar.kiss.MainActivity"));
        callback.invoke(i);
    }

    @NotNull
    @Override
    public Drawable getIcon() {
        return getContext().getDrawable(R.drawable.ic_search);
    }
}

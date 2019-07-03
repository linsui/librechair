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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.globalsearch.SearchProvider;
import com.android.launcher3.R;
import java.io.IOException;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

public abstract class ChromiumBromiteSearchProvider extends SearchProvider {

    private ProviderType type;

    public ChromiumBromiteSearchProvider(ProviderType type, Context context) {
        super(context);
        this.type = type;
    }

    @NotNull
    @Override
    public String getName() {
        return (type == ProviderType.CHROMIUM ? getContext().getString(R.string.chromium)
                : getContext().getString(
                        R.string.bromite)) + " (root)";
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
        Intent i = new Intent(getContext(), Trampoline.class);
        i.putExtra("type", type == ProviderType.BROMITE ? "bromite" : "chromium");
        callback.invoke(i);
    }

    @NotNull
    @Override
    public Drawable getIcon() {
        return getContext().getResources().getDrawable(R.drawable.ic_search);
    }

    public enum ProviderType {
        BROMITE, CHROMIUM
    }

    public static class Trampoline extends Activity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getIntent().getStringExtra("type").equals("bromite")) {
                try {
                    Runtime.getRuntime().exec(new String[]{"su", "-c",
                            String.format(
                                    "am start %s/org.chromium.chrome.browser.searchwidget.SearchActivity",
                                    "org.bromite.bromite")});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Runtime.getRuntime().exec(new String[]{"su", "-c",
                            String.format(
                                    "am start %s/org.chromium.chrome.browser.searchwidget.SearchActivity",
                                    LawnchairUtilsKt.getLawnchairPrefs(this).getChromiumPackageName())});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onResume() {
            super.onResume();
            finish();
        }
    }

    public static class BromiteSearchProvider extends ChromiumBromiteSearchProvider {

        public BromiteSearchProvider(Context context) {
            super(ProviderType.BROMITE, context);
        }
    }

    public static class ChromiumSearchProvider extends ChromiumBromiteSearchProvider {

        public ChromiumSearchProvider(Context context) {
            super(ProviderType.CHROMIUM, context);
        }
    }
}

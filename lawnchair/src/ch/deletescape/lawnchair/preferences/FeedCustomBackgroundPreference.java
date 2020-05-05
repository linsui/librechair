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

package ch.deletescape.lawnchair.preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import ch.deletescape.lawnchair.LawnchairPreferences;
import ch.deletescape.lawnchair.LawnchairPreferences.OnPreferenceChangeListener;
import ch.deletescape.lawnchair.feed.images.providers.CustomBackgroundProvider;

public class FeedCustomBackgroundPreference extends Preference implements OnPreferenceChangeListener {
    
    public FeedCustomBackgroundPreference(@NotNull Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
        setVisible(Objects.equals(Utilities.getLawnchairPrefs(
                context).getFeedBackground().getClazz().getQualifiedName(),
                CustomBackgroundProvider.class.getName()));
        Utilities.getLawnchairPrefs(context).addOnPreferenceChangeListener("pref_feed_background", this);
    }

    @Override
    public CharSequence getTitle() {
        return getContext().getString(R.string.title_pref_feed_custom_background);
    }

    @Override
    public CharSequence getSummary() {
        return getContext().getString(R.string.summary_pref_feed_custom_background);
    }

    @Override
    public void onValueChanged(@NotNull String key, @NotNull LawnchairPreferences prefs,
            boolean force) {
        setVisible(prefs.getFeedBackground().getClazz().getQualifiedName()
                .equals(CustomBackgroundProvider.class.getName()));
    }
}

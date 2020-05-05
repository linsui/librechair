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

package ch.deletescape.lawnchair.preferences;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.android.launcher3.R;

import org.jetbrains.annotations.NotNull;

public class FeedTabColorsPreference extends DialogPreference implements FragmentInitializer {
    public FeedTabColorsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.preference_dialog_recyclerview;
    }

    @Override
    public CharSequence getPositiveButtonText() {
        return getContext().getString(R.string.add_new_tab);
    }

    @Override
    public CharSequence getNegativeButtonText() {
        return null;
    }

    @Override
    public PreferenceDialogFragmentCompat getPrefFragment(@NotNull String key) {
        FeedTabColorsFragment fragment = new FeedTabColorsFragment();
        Bundle args = new Bundle();
        args.putString("key", key);
        fragment.setArguments(args);
        return fragment;
    }
}

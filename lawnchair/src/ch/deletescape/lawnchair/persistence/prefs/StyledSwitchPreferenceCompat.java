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

package ch.deletescape.lawnchair.persistence.prefs;

import android.content.Context;
import android.util.AttributeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.deletescape.lawnchair.persistence.SimplePersistence;

public class StyledSwitchPreferenceCompat extends
        ch.deletescape.lawnchair.preferences.StyledSwitchPreferenceCompat {
    public StyledSwitchPreferenceCompat(
            @NotNull Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean persistBoolean(boolean value) {
        SimplePersistence.InstanceHolder.getInstance(getContext())
                .put(getKey(), String.valueOf(value));
        return true;
    }

    @Override
    protected boolean getPersistedBoolean(boolean defaultReturnValue) {
        String value = SimplePersistence.InstanceHolder.getInstance(getContext())
                .get(getKey(), null);
        return value != null ? Boolean.valueOf(value) : defaultReturnValue;
    }
}

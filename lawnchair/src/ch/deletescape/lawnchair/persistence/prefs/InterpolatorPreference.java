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

import androidx.preference.ListPreference;

import ch.deletescape.lawnchair.feed.anim.interpolator.InterpolatorRegistry;
import ch.deletescape.lawnchair.persistence.SimplePersistence;

public class InterpolatorPreference extends ListPreference {
    public InterpolatorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultValue("accel");
        setEntryValues(InterpolatorRegistry.ALL.keySet().toArray(new CharSequence[0]));
        setEntries(InterpolatorRegistry.NAMES.keySet()
                .stream()
                .map(InterpolatorRegistry.NAMES::get)
                .map(context::getString)
                .toArray(String[]::new));
    }

    @Override
    protected boolean persistString(String value) {
        SimplePersistence.InstanceHolder
                .getInstance(getContext())
                .put(getKey(), value);
        return true;
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        return SimplePersistence.InstanceHolder
                .getInstance(getContext())
                .get(getKey(), defaultReturnValue);
    }
}

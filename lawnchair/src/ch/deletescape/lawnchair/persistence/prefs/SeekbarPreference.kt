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

package ch.deletescape.lawnchair.persistence.prefs

import android.content.Context
import android.util.AttributeSet
import ch.deletescape.lawnchair.persistence.SimplePersistence
import ch.deletescape.lawnchair.preferences.SeekbarPreference

class SeekbarPreference(context: Context, attrs: AttributeSet?) :
        SeekbarPreference(context, attrs) {
    override fun persistFloat(value: Float): Boolean {
        SimplePersistence.InstanceHolder
                .getInstance(context).put(key, value.toString())
        return true
    }

    override fun getPersistedFloat(defaultReturnValue: Float): Float {
        return SimplePersistence.InstanceHolder
                .getInstance(context).get(key, defaultReturnValue.toString()).toFloat();
    }
}
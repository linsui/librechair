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

package ch.deletescape.lawnchair.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference
import ch.deletescape.lawnchair.feed.anim.AnimationDelegate
import ch.deletescape.lawnchair.fromStringRes

class FeedAnimationDelegatePreference(context: Context, attrs: AttributeSet) :
        ListPreference(context, attrs) {
    init {
        entries = AnimationDelegate.allDelegates.map {
            AnimationDelegate.delegateNames.getOrElse(it) {
                error("there must be a valid name for delegate $it")
            }.fromStringRes(context)
        }.toTypedArray()
        entryValues = AnimationDelegate.allDelegates.map { it.qualifiedName!! }.toTypedArray()
    }
}
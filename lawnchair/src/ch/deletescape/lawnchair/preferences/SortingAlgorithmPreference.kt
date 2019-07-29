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

/*
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

package ch.deletescape.lawnchair.preferences

import android.content.Context
import android.support.v7.preference.ListPreference
import android.util.AttributeSet
import ch.deletescape.lawnchair.feed.OrderedSortingAlgorithm
import ch.deletescape.lawnchair.feed.getFeedSortingAlgorithmName
import ch.deletescape.lawnchair.feed.getFeedSortingAlgorithms


class SortingAlgorithmPreference(context: Context, attrs: AttributeSet?) :
        ListPreference(context, attrs) {

    init {
        entryValues = getEntryList();
        entries = getDisplayList();
        setDefaultValue(OrderedSortingAlgorithm::class.java.name)
    }

    fun getEntryList(): Array<CharSequence> {
        return getFeedSortingAlgorithms().toTypedArray()
    }

    fun getDisplayList(): Array<CharSequence> {
        val entries = ArrayList<String>()

        getEntryList().forEach {
            entries += getFeedSortingAlgorithmName(it.toString(), context)
        }
        return entries.toTypedArray();
    }
}

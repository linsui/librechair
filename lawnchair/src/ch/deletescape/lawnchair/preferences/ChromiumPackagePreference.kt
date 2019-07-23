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

package ch.deletescape.lawnchair.preferences

import android.content.Context
import android.support.v7.preference.ListPreference
import android.util.AttributeSet
import ch.deletescape.lawnchair.globalsearch.providers.ChromiumBromiteSearchProvider
import ch.deletescape.lawnchair.lawnchairPrefs

class ChromiumPackagePreference(context: Context, attrs: AttributeSet) :
        ListPreference(context, attrs) {
    val prefs by lazy { context.lawnchairPrefs }

    init {
        entryValues = ChromiumBromiteSearchProvider.ChromiumHelper.getChromiumPackages(context)
                .map { it.packageName as CharSequence }.toTypedArray()
        entries = ChromiumBromiteSearchProvider.ChromiumHelper.getChromiumPackages(context)
                .map { context.packageManager.getApplicationLabel(it) as CharSequence }.toTypedArray()
    }

}
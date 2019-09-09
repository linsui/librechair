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

import android.content.ComponentName
import android.content.Context
import android.util.AttributeSet
import androidx.preference.MultiSelectListPreference
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.feed.RemoteFeedProvider
import ch.deletescape.lawnchair.lawnchairPrefs

class RemoteFeedProvidersPreference(c: Context, attributes: AttributeSet) :
        MultiSelectListPreference(c, attributes), LawnchairPreferences.OnPreferenceChangeListener {
    override fun onValueChanged(key: String, prefs: LawnchairPreferences, force: Boolean) {
        summary = prefs.remoteFeedProviders.joinToString {
            context.packageManager.getServiceInfo(ComponentName.unflattenFromString(it), 0)
                    .loadLabel(context.packageManager)
        }
    }

    init {
        setDefaultValue(setOf<String>())
        entries = RemoteFeedProvider.allProviders(context).map {
            context.packageManager.getServiceInfo(it, 0).loadLabel(context.packageManager)
        }.toTypedArray()
        entryValues = RemoteFeedProvider.allProviders(context).map { it.flattenToString() }
                .toTypedArray()
        summary = context.lawnchairPrefs.remoteFeedProviders.joinToString {
            context.packageManager.getServiceInfo(ComponentName.unflattenFromString(it), 0)
                    .loadLabel(context.packageManager)
        }
        context.lawnchairPrefs.addOnPreferenceChangeListener(this, "pref_remote_feed_providers")
    }
}
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

package ch.deletescape.lawnchair.settings.ui

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.support.v7.preference.ListPreference
import android.util.AttributeSet
import ch.deletescape.lawnchair.trebuchet.TrebuchetUtil
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.BuildConfig


class RecentsProviderPreference(context: Context, attrs: AttributeSet?) :
        ListPreference(context, attrs) {

    init {
        entryValues = getEntryList();
        entries = getDisplayList();
        setDefaultValue(BuildConfig.APPLICATION_ID)
    }

    fun getEntryList(): Array<CharSequence> {
        val entries = ArrayList<String>()
        TrebuchetUtil.availableQuickstepProviders(context).forEach {
            d("getEntryList: " + it.packageName);
            if (it.packageName != null) {
                entries += it.packageName!!
            }
        }
        return entries.toTypedArray();
    }

    fun getDisplayList(): Array<CharSequence> {
        val entries = ArrayList<String>()

        getEntryList().forEach {
            var applicationInfo: ApplicationInfo?
            try {
                applicationInfo = context.packageManager.getApplicationInfo(it.toString(), 0)
            } catch (e: PackageManager.NameNotFoundException) {
                applicationInfo = null
            }
            entries += context.packageManager.getApplicationLabel(applicationInfo)!!.toString()
        }
        return entries.toTypedArray();
    }

}

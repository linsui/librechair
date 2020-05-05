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
import android.util.AttributeSet
import androidx.preference.ListPreference
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.persistence.SimplePersistence
import ch.deletescape.lawnchair.smartspace.BlankDataProvider
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider
import ch.deletescape.lawnchair.smartspace.weather.forecast.OWMForecastProvider
import ch.deletescape.lawnchair.util.buildEntries
import com.android.launcher3.Utilities

class ForecastProviderPreference(context: Context, attrs: AttributeSet?) :
        ListPreference(context, attrs) {

    init {
        setDefaultValue(OWMForecastProvider::class.java.name)
        buildEntries {
            ForecastProvider.Controller.getProviderList().forEach {
                addEntry(ForecastProvider.Controller.getProviderName(context, it), it)
            }
        }
    }

    override fun shouldDisableDependents(): Boolean {
        return super.shouldDisableDependents() || value == BlankDataProvider::class.java.name
    }

    override fun getPersistedString(defaultReturnValue: String?): String {
        return SimplePersistence.InstanceHolder.getInstance(context).get(key, defaultReturnValue)
    }

    override fun persistString(value: String): Boolean {
        SimplePersistence.InstanceHolder.getInstance(context).put(key, value)
        return true
    }
}
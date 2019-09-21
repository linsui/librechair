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
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.DialogPreference
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.preference.PreferenceViewHolder
import androidx.room.InvalidationTracker
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.feed.chips.ChipDatabase
import ch.deletescape.lawnchair.feed.chips.ChipProvider
import ch.deletescape.lawnchair.feed.chips.ChipProviderContainer
import ch.deletescape.lawnchair.runOnMainThread
import ch.deletescape.lawnchair.settings.ui.ControlledPreference
import com.android.launcher3.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChipProvidersPreference(context: Context, attrs: AttributeSet?) :
        DialogPreference(context, attrs),
        ControlledPreference by ControlledPreference.Delegate(context, attrs),
        LawnchairPreferences.MutableListPrefChangeListener,
        FragmentInitializer {

    init {
        updateSummary()
    }

    override fun getPrefFragment(key: String): PreferenceDialogFragmentCompat {
        return ChipProvidersFragment.newInstance(key)
    }

    fun setProviders(providers: List<ChipProviderContainer>) {
        GlobalScope.launch {
            ChipDatabase.Holder.getInstance(context).dao().removeAll()
            providers.forEach {
                it.order = providers.indexOf(it)
                ChipDatabase.Holder.getInstance(context).dao().add(it)
            }
        }.invokeOnCompletion {
            runOnMainThread {
                updateSummary()
            }
        }
    }

    private fun updateSummary() {
        val providerNames = ChipDatabase.Holder.getInstance(context).dao().all
                .map { ChipProvider.Names.getNameForContainer(it) }
        if (providerNames.isNotEmpty()) {
            summary = TextUtils.join(", ", providerNames)
        } else {
            setSummary(R.string.weather_provider_disabled)
        }
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val summaryView = holder.findViewById(android.R.id.summary) as TextView
        summaryView.maxLines = 1
        summaryView.ellipsize = TextUtils.TruncateAt.END
    }

    override fun onAttached() {
        super.onAttached()
        ChipDatabase.Holder.getInstance(context).invalidationTracker.addObserver(object : InvalidationTracker.Observer("chipprovidercontainer") {
            override fun onInvalidated(tables: MutableSet<String>) {
                runOnMainThread {
                    updateSummary()
                }
            }
        })
    }

    override fun onListPrefChanged(key: String) {
        runOnMainThread {
            updateSummary()
        }
    }

    override fun getDialogLayoutResource() = R.layout.dialog_preference_recyclerview
}

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
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import androidx.databinding.ObservableList
import androidx.preference.DialogPreference
import androidx.preference.PreferenceViewHolder
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.feed.FeedProviderContainer
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.feed.MainFeedController
import ch.deletescape.lawnchair.persistence.feedPrefs
import ch.deletescape.lawnchair.runOnMainThread
import ch.deletescape.lawnchair.settings.ui.ControlledPreference
import com.android.launcher3.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FeedProvidersPreference(context: Context, attrs: AttributeSet?) :
        DialogPreference(context, attrs),
        ControlledPreference by ControlledPreference.Delegate(context, attrs),
        LawnchairPreferences.MutableListPrefChangeListener {

    init {
        updateSummary()
    }

    fun setProviders(providers: List<FeedProviderContainer>) {
        context.feedPrefs.feedProviders.apply {
            clear()
            FeedScope.launch {
                delay(100)
                runOnMainThread {
                    addAll(providers)
                }
            }
        }
    }

    private fun updateSummary() {
        runOnMainThread {
            val providerNames = context.feedPrefs.feedProviders
                    .map { MainFeedController.getDisplayName(it, context) }
            if (providerNames.isNotEmpty()) {
                summary = TextUtils.join(", ", providerNames)
            } else {
                setSummary(R.string.weather_provider_disabled)
            }
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
        context.feedPrefs.feedProviders.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<FeedProviderContainer>>() {
            override fun onChanged(sender: ObservableList<FeedProviderContainer>?) {
                updateSummary()
            }

            override fun onItemRangeRemoved(sender: ObservableList<FeedProviderContainer>?,
                                            positionStart: Int, itemCount: Int) {
                updateSummary()
            }

            override fun onItemRangeMoved(sender: ObservableList<FeedProviderContainer>?, fromPosition: Int,
                                          toPosition: Int, itemCount: Int) {
                updateSummary()
            }

            override fun onItemRangeInserted(sender: ObservableList<FeedProviderContainer>?,
                                             positionStart: Int, itemCount: Int) {
                updateSummary()
            }

            override fun onItemRangeChanged(sender: ObservableList<FeedProviderContainer>?,
                                            positionStart: Int, itemCount: Int) {
                updateSummary()
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

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
import android.os.Bundle
import android.support.v7.preference.DialogPreference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.lawnchairPrefs
import com.android.launcher3.R

class WebApplicationsPreference(context: Context?, attrs: AttributeSet?) :
        DialogPreference(context, attrs), LawnchairPreferences.OnPreferenceChangeListener {
    override fun onValueChanged(key: String, prefs: LawnchairPreferences, force: Boolean) {
        // TODO refresh summary when pref changed
    }

    override fun getNegativeButtonText(): CharSequence {
        return context.getString(R.string.negative_button_test_web_applications_preference)
    }

    override fun getDialogLayoutResource() = R.layout.dialog_preference_recyclerview
    class Fragment : PreferenceDialogFragmentCompat() {
        override fun onDialogClosed(positiveResult: Boolean) {
            if (!positiveResult) {
                // TODO allow creating web apps
            }
        }

        override fun onBindDialogView(view: View) {
            super.onBindDialogView(view)
            val recyclerView = view.findViewById(R.id.list) as RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = Adapter()
        }

        companion object {
            fun make() = Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEY, "pref_feed_web_applications")
                }
            }
        }

        inner class Adapter : RecyclerView.Adapter<ProviderItemViewHolder>() {
            override fun getItemCount(): Int {
                return context?.lawnchairPrefs?.feedWebApplications?.size ?: 0
            }

            override fun onBindViewHolder(holder: ProviderItemViewHolder, position: Int) {
                val app = context?.lawnchairPrefs?.feedWebApplications?.get(position)
                holder.title.text = app?.title
                holder.summary.text = app?.title.also {
                    if (it != null) holder.summary.visibility == View.VISIBLE else holder.summary
                            .visibility = View.GONE
                }
                holder.dragHandle.visibility = View.VISIBLE
            }

            override fun onCreateViewHolder(parent: ViewGroup,
                                            viewType: Int) = ProviderItemViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.event_provider_dialog_item,
                                                         parent, false))
        }
    }
}
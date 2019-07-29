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
import android.widget.TextView
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.appWidgetManager
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.Launcher
import com.android.launcher3.R

class FeedWidgetsListPreference(context: Context, attrs: AttributeSet) :
        DialogPreference(context, attrs) {

    override fun getNegativeButtonText(): CharSequence? {
        return null
    }

    override fun getDialogLayoutResource() = R.layout.dialog_preference_recyclerview


    class ViewHolder(itemView: View,
                     val title: TextView = itemView.findViewById(android.R.id.title),
                     val summary: TextView = itemView.findViewById(android.R.id.summary)) :
            RecyclerView.ViewHolder(itemView)

    class Fragment : PreferenceDialogFragmentCompat() {
        val preference by lazy { context!!.lawnchairPrefs.feedWidgetList }

        override fun onDialogClosed(positiveResult: Boolean) {

        }

        override fun onBindDialogView(view: View) {
            super.onBindDialogView(view)
            d("onBindDialogView: bound to dialog view $view")
            val recyclerView = view.findViewById<RecyclerView>(R.id.list)
            d("onBindDialogView: found recycler $recyclerView")
            recyclerView.layoutManager = LinearLayoutManager(context)
            d("onBindDialogView: set layoutManager for RecyclerView $recyclerView")
            recyclerView.adapter = FeedWidgetsPreferenceAdapter(preference)
            d("onBindDialogView: set adapter for RecyclerView ${recyclerView.adapter}")
        }

        class FeedWidgetsPreferenceAdapter(
                val preference: LawnchairPreferences.MutableListPref<Int>) :
                RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup,
                                            viewType: Int): ViewHolder = ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.event_provider_dialog_item,
                                                                parent, false))

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                d("onBindViewHolder: retrieving widget information for widget ${preference.getAll()[position]}")
                val appWidgetInfo = holder.itemView.context.appWidgetManager.getAppWidgetInfo(
                        preference.getAll().filter {
                            Launcher.getInstance().appWidgetManager.getAppWidgetInfo(it) != null
                        }[position])
                holder.title.text = appWidgetInfo.loadLabel(holder.itemView.context.packageManager)
            }

            override fun getItemCount(): Int {
                d("getItemCount: getItemCount called")
                d("getItemCount: item count is ${preference.getList().filter {
                    Launcher.getInstance().appWidgetManager.getAppWidgetInfo(it) != null
                }.size}")
                return preference.getList().filter {
                    Launcher.getInstance()?.appWidgetManager?.getAppWidgetInfo(it) != null
                }.size
            }
        }

        companion object {
            fun newInstance(): Fragment {
                return Fragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_KEY, "pref_feed_widgets")
                    }
                }
            }
        }
    }
}
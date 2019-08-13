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

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.preference.DialogPreference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.feed.MainFeedController
import ch.deletescape.lawnchair.feed.tabs.CustomTab
import ch.deletescape.lawnchair.feed.tabs.CustomTabbingController
import ch.deletescape.lawnchair.fromStringRes
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.theme.ThemeOverride
import com.android.launcher3.R
import java.util.*

class CustomizableTabsPreference(context: Context?, attrs: AttributeSet?) :
        DialogPreference(context, attrs), LawnchairPreferences.OnPreferenceChangeListener {
    init {
        summary = context!!.lawnchairPrefs.feedCustomTabs.map { it.name }.joinToString(", ")
        context.lawnchairPrefs
                .addOnPreferenceChangeListener(this, "pref_feed_tab_provider", "pref_feed_tabs")
    }

    override fun onValueChanged(key: String, prefs: LawnchairPreferences, force: Boolean) {
        summary = prefs.feedCustomTabs.map { it.name }.joinToString(", ")
        isVisible = prefs.feedTabController == CustomTabbingController::class.qualifiedName
    }

    override fun getPositiveButtonText(): CharSequence {
        return context.getString(R.string.negative_button_test_web_applications_preference)
    }

    override fun getNegativeButtonText(): CharSequence? {
        return null
    }

    override fun getDialogLayoutResource() = R.layout.dialog_preference_recyclerview
    class Fragment : PreferenceDialogFragmentCompat() {
        lateinit var recyclerView: RecyclerView
        override fun onDialogClosed(positiveResult: Boolean) {
            if (positiveResult) {
                val dialog = object :
                        AlertDialog(context, ThemeOverride.AlertDialog().getTheme(context!!)) {}
                val context = context!!
                val view = LinearLayout(context).apply {
                    setPadding(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f,
                                                         context.resources.displayMetrics).toInt(),
                               0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f,
                                                            context.resources.displayMetrics).toInt(),
                               0)
                }
                view.addView(EditText(context).apply {
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                             ViewGroup.LayoutParams.WRAP_CONTENT)
                    id = R.id.name
                })
                dialog.setTitle(getString(R.string.title_dialog_new_tab))
                dialog.setView(view)
                dialog.setButton(Dialog.BUTTON_POSITIVE,
                                 android.R.string.ok.fromStringRes(context)) { _, _ ->
                    val title = dialog.findViewById<TextView>(R.id.name).text
                    if (title.isEmpty()) {
                        return@setButton
                    } else {
                        context.lawnchairPrefs.feedCustomTabs += CustomTab().apply {
                            name = title.toString()
                            providers = emptyList()
                        }
                        recyclerView.adapter
                                ?.notifyItemInserted(recyclerView.adapter?.itemCount ?: 0)
                    }
                }
                dialog.show()
            }
        }

        override fun onBindDialogView(view: View) {
            super.onBindDialogView(view)
            recyclerView = view.findViewById(R.id.list) as RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = Adapter()
        }

        companion object {
            fun make() = Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEY, "pref_feed_tabs")
                }
            }
        }

        inner class Adapter : RecyclerView.Adapter<ProviderItemViewHolder>() {
            private lateinit var itemTouchHelper: ItemTouchHelper
            override fun getItemCount(): Int {
                return context?.lawnchairPrefs?.feedCustomTabs?.size ?: 0
            }

            override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                super.onAttachedToRecyclerView(recyclerView)
                ItemTouchHelper(object : ItemTouchHelper.Callback() {
                    override fun getMovementFlags(recyclerView: RecyclerView,
                                                  viewHolder: RecyclerView.ViewHolder): Int {
                        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                                                 ItemTouchHelper.START)
                    }

                    override fun onMove(recyclerView: RecyclerView,
                                        viewHolder: RecyclerView.ViewHolder,
                                        target: RecyclerView.ViewHolder): Boolean {
                        val fromPosition = target.adapterPosition
                        val toPosition = viewHolder.adapterPosition
                        val prefList = context!!.lawnchairPrefs.feedCustomTabs.toMutableList()

                        if (fromPosition < toPosition) {
                            for (i in fromPosition until toPosition) {
                                Collections.swap(prefList, i, i + 1)
                            }
                        } else {
                            for (i in fromPosition downTo toPosition + 1) {
                                Collections.swap(prefList, i, i - 1)
                            }
                        }

                        context!!.lawnchairPrefs.feedCustomTabs = prefList.toSet()
                        notifyItemMoved(fromPosition, toPosition)
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        context?.lawnchairPrefs?.feedCustomTabs =
                                context?.lawnchairPrefs?.feedCustomTabs?.toMutableSet()?.apply {
                                    remove(elementAt(viewHolder.adapterPosition))
                                } ?: emptySet()
                        notifyItemRemoved(viewHolder.adapterPosition)
                    }

                    override fun isItemViewSwipeEnabled() = true
                    override fun isLongPressDragEnabled() = true
                }).apply {
                    attachToRecyclerView(recyclerView)
                }.also { itemTouchHelper = it }
            }

            override fun onBindViewHolder(holder: ProviderItemViewHolder, position: Int) {
                val app = context!!.lawnchairPrefs.feedCustomTabs.elementAt(position)
                holder.title.text = app.name
                holder.dragHandle.visibility = View.VISIBLE
                holder.dragHandle.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper.startDrag(holder)
                        true
                    } else {
                        true
                    }
                }
                holder.itemView.setOnClickListener {
                    AlertDialog.Builder(context!!, ThemeOverride.AlertDialog().getTheme(context!!))
                            .setTitle(R.string.title_dialog_select_tab_providers)
                            .setMultiChoiceItems(MainFeedController.getFeedProviders().map {
                                MainFeedController.getDisplayName(it, context!!)
                            }.toTypedArray(), MainFeedController.getFeedProviders().map {
                                app.providers.contains(it)
                            }.toBooleanArray()) { dialog, which, isChecked ->
                                if (!isChecked) {
                                    app.providers = app.providers
                                            .minus(MainFeedController.getFeedProviders().get(which))
                                } else {
                                    app.providers =
                                            app.providers + MainFeedController.getFeedProviders().get(
                                                    which)
                                }

                                context!!.lawnchairPrefs.feedCustomTabs =
                                        context!!.lawnchairPrefs.feedCustomTabs.toMutableSet()
                                                .apply {
                                                    elementAt(holder.adapterPosition).providers =
                                                            app.providers
                                                }
                            }.setPositiveButton(android.R.string.ok) { dialog, which -> }.show()
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup,
                                            viewType: Int) = ProviderItemViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.event_provider_dialog_item,
                                                         parent, false))
        }
    }
}
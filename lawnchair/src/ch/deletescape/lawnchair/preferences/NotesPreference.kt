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
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.fromStringRes
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.theme.ThemeOverride
import ch.deletescape.lawnchair.todo.Note
import com.android.launcher3.R
import java.util.*

class NotesPreference(context: Context?, attrs: AttributeSet?) : DialogPreference(context, attrs),
                                                                 LawnchairPreferences.OnPreferenceChangeListener {
    init {
        summary = context!!.lawnchairPrefs.feedNotes.map { it.title }.joinToString(", ")
        context.lawnchairPrefs.addOnPreferenceChangeListener(this, "pref_feed_notes")
    }

    override fun onValueChanged(key: String, prefs: LawnchairPreferences, force: Boolean) {
        summary = prefs.feedNotes.map { it.title }.joinToString(", ")
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
                val context = context
                val view = LayoutInflater.from(context)
                        .inflate(R.layout.dialog_create_web_app, null, false)
                dialog.setTitle(getString(R.string.title_dialog_new_note))
                dialog.setView(view)
                view.findViewById<TextView>(R.id.web_app_link).hint =
                        context?.getString(R.string.hint_text_note_content)
                dialog.setButton(Dialog.BUTTON_POSITIVE,
                                 android.R.string.ok.fromStringRes(context!!)) { _, _ ->
                    val title = dialog.findViewById<TextView>(R.id.web_app_title).text
                    val link = dialog.findViewById<TextView>(R.id.web_app_link).text
                    if (title.isEmpty() || link.isEmpty()) {
                        return@setButton
                    } else {
                        context.lawnchairPrefs.feedNotes += Note(title.toString(), link.toString(),
                                                                 type = Note.Types.NOTE)
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
                    putString(ARG_KEY, "pref_feed_notes")
                }
            }
        }

        inner class Adapter : RecyclerView.Adapter<ProviderItemViewHolder>() {
            private lateinit var itemTouchHelper: ItemTouchHelper
            override fun getItemCount(): Int {
                return context?.lawnchairPrefs?.feedNotes?.size ?: 0
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
                        val prefList = context!!.lawnchairPrefs.feedNotes.toMutableList()

                        if (fromPosition < toPosition) {
                            for (i in fromPosition until toPosition) {
                                Collections.swap(prefList, i, i + 1)
                            }
                        } else {
                            for (i in fromPosition downTo toPosition + 1) {
                                Collections.swap(prefList, i, i - 1)
                            }
                        }

                        context!!.lawnchairPrefs.feedNotes = prefList
                        notifyItemMoved(fromPosition, toPosition)
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        context?.lawnchairPrefs?.feedNotes =
                                context?.lawnchairPrefs?.feedNotes?.toMutableList()?.apply {
                                    removeAt(viewHolder.adapterPosition)
                                }?.toList() ?: emptyList()
                        notifyItemRemoved(viewHolder.adapterPosition)
                    }

                    override fun isItemViewSwipeEnabled() = true
                    override fun isLongPressDragEnabled() = true
                }).apply {
                    attachToRecyclerView(recyclerView)
                }.also { itemTouchHelper = it }
            }

            override fun onBindViewHolder(holder: ProviderItemViewHolder, position: Int) {
                val app = context?.lawnchairPrefs?.feedNotes?.get(position)
                holder.title.text = app?.title
                holder.summary.text = app?.content?.take(32).also {
                    if (it != null) holder.summary.visibility == View.VISIBLE else holder.summary
                            .visibility = View.GONE
                }
                holder.summary.visibility = View.VISIBLE
                holder.dragHandle.visibility = View.VISIBLE
                holder.dragHandle.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper.startDrag(holder)
                        true
                    } else {
                        true
                    }
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup,
                                            viewType: Int) = ProviderItemViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.event_provider_dialog_item,
                                                         parent, false))
        }
    }
}
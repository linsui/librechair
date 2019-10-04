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

package ch.deletescape.lawnchair.notes

import android.app.Dialog
import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.fromStringRes
import ch.deletescape.lawnchair.getColorEngineAccent
import ch.deletescape.lawnchair.runOnMainThread
import ch.deletescape.lawnchair.theme.ThemeOverride
import ch.deletescape.lawnchair.util.SingleUseHold
import com.android.launcher3.R
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import me.priyesh.chroma.ChromaView
import me.priyesh.chroma.ColorMode

class SimpleNoteAdapter(override val context: Context) :
        NotesAdapter(context, context.getColorEngineAccent()) {
    private lateinit var notes: MutableList<Note>
    private val hold = SingleUseHold()

    init {
        FeedScope.launch {
            notes = DatabaseStore.getAccessObject(context).allNotes.toMutableList()
        }.invokeOnCompletion {
            runOnMainThread {
                notifyDataSetChanged()
                hold.trigger()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NotesViewHolder(parent)
    override fun getItemCount() = if (::notes.isInitialized) notes.size else 0
    override fun add(note: Note) {
        FeedScope.launch {
            hold.waitFor()
            DatabaseStore.getAccessObject(context).insert(note);
        }.invokeOnCompletion {
            notes.add(note)
            runOnMainThread { notifyItemInserted(notes.size) }
        }
    }

    override fun remove(note: Note) {
        FeedScope.launch {
            hold.waitFor()
            DatabaseStore.getAccessObject(context).remove(note);
        }.invokeOnCompletion {
            val oldIndex = notes.indexOf(note)
            notes.minusAssign(note)
            runOnMainThread {
                notifyItemRemoved(oldIndex)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: androidx.recyclerview.widget.RecyclerView,
                                          viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) = makeMovementFlags(
                    0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT)

            override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                                target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Nothing = error(
                    "reorganization has not yet been implemented")

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                remove(notes[viewHolder.adapterPosition])
            }

            override fun isItemViewSwipeEnabled() = true
        }).attachToRecyclerView(recyclerView);
    }

    override fun bindToTabLayout(tabLayout: TabLayout) = Unit

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.item.setup(notes[position].title, notes[position].content);
        holder.item.isSelected = notes[position].selected
        holder.item.setOnClickListener {
            FeedScope.launch {
                hold.waitFor()
                DatabaseStore.getAccessObject(context).setSelected(notes[holder.adapterPosition].id,
                                                                   notes[holder.adapterPosition].selected.not())
                notes[holder.adapterPosition].selected = !notes[holder.adapterPosition].selected
            }.invokeOnCompletion {
                runOnMainThread { notifyItemChanged(holder.adapterPosition) }
            }
        }
        holder.item.tintBackground(notes[position].colour)
        holder.item.setOnLongClickListener {
            val editDialog =
                    object : AlertDialog(context, ThemeOverride.AlertDialog().getTheme(context)) {}
            val editText = EditText(context).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                         LinearLayout.LayoutParams.WRAP_CONTENT)
                        .apply {
                            marginStart = TypedValue
                                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f,
                                                    context.resources.displayMetrics).toInt()
                            marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f,
                                                                  context.resources.displayMetrics)
                                    .toInt()
                        }
                setText(notes[holder.adapterPosition].content)
            }
            editDialog.setTitle(notes[holder.adapterPosition].title)
            editDialog.setView(LinearLayout(context).apply {
                addView(editText)
            })
            editDialog.setButton(Dialog.BUTTON_POSITIVE,
                                 android.R.string.ok.fromStringRes(context)) { dialog, which ->
                FeedScope.launch {
                    hold.waitFor()
                    DatabaseStore.getAccessObject(context)
                            .setContent(notes[holder.adapterPosition].id, editText.text.toString())
                    notes[holder.adapterPosition].content = editText.text.toString()
                }.invokeOnCompletion {
                    runOnMainThread { notifyItemChanged(holder.adapterPosition) }
                }
            }
            editDialog.setButton(Dialog.BUTTON_NEUTRAL,
                                 R.string.title_dialog_select_color.fromStringRes(
                                         context)) { dialog2, which2 ->
                val dialog = object : android.app.AlertDialog(context,
                                                              ThemeOverride.AlertDialog().getTheme(
                                                                      context)) {}
                dialog.setView(ChromaView(notes[holder.adapterPosition].colour, ColorMode.ARGB,
                                          context).apply {
                    id = R.id.color_view
                })
                dialog.setTitle(R.string.title_dialog_select_color);
                dialog.setButton(Dialog.BUTTON_POSITIVE, android.R.string.ok.fromStringRes(
                        context)) { dialogInterface, which ->
                    FeedScope.launch {
                        DatabaseStore.getAccessObject(context)
                                .setColor(notes[holder.adapterPosition].id,
                                          dialog.findViewById<ChromaView>(
                                                  R.id.color_view).currentColor)
                        notes[holder.adapterPosition].colour =
                                dialog.findViewById<ChromaView>(R.id.color_view).currentColor
                    }.invokeOnCompletion {
                        runOnMainThread {
                            notifyItemChanged(holder.adapterPosition)
                        }
                    }
                }
                dialog.show()
            }
            editDialog.show()
            true
        }
    }
}

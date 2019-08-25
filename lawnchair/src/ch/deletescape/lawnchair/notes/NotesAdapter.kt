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

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ch.deletescape.lawnchair.groups.AppGroupsManager
import ch.deletescape.lawnchair.groups.ui.AppCategorizationTypeItem
import ch.deletescape.lawnchair.runOnMainThread
import ch.deletescape.lawnchair.util.SingleUseHold
import com.android.launcher3.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotesAdapter(val context: Context) : RecyclerView.Adapter<NotesViewHolder>() {
    private lateinit var notes: MutableList<Note>
    private val hold = SingleUseHold()

    init {
        GlobalScope.launch {
            notes = DatabaseStore.getAccessObject(context).allNotes.toMutableList()
        }.invokeOnCompletion {
            runOnMainThread {
                notifyItemRangeInserted(0, notes.size - 1)
                hold.trigger()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NotesViewHolder(parent)
    override fun getItemCount() = if (::notes.isInitialized) notes.size else 0
    fun add(note: Note) = GlobalScope.launch {
        hold.waitFor()
        DatabaseStore.getAccessObject(context).insert(note);
    }.invokeOnCompletion {
        notes.add(note)
        runOnMainThread { notifyItemInserted(notes.size) }
    }

    fun remove(note: Note) = GlobalScope.launch {
        hold.waitFor()
        DatabaseStore.getAccessObject(context).remove(note);
    }.invokeOnCompletion {
        val oldIndex = notes.indexOf(note)
        notes.minusAssign(note)
        runOnMainThread {
            notifyItemRemoved(oldIndex)
        }
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.item.setup(AppGroupsManager.CategorizationType.Tabs, notes[position].title,
                          notes[position].content);
        holder.item.isSelected = false
        holder.item.setOnClickListener {}
    }
}

class NotesViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.app_categorization_type_item, parent,
                                                    false)) {
    val item: AppCategorizationTypeItem by lazy { itemView as AppCategorizationTypeItem }
}
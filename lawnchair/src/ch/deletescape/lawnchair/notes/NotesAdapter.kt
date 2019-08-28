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
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.design.widget.TabLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.theme.ThemeOverride
import ch.deletescape.lawnchair.util.SingleUseHold
import ch.deletescape.lawnchair.views.SelectableRoundedView
import com.android.launcher3.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.priyesh.chroma.ChromaView
import me.priyesh.chroma.ColorMode

class NotesAdapter(val context: Context) : RecyclerView.Adapter<NotesViewHolder>() {
    private lateinit var allNotes: MutableList<Note>
    private val notes: List<Note>
        get() = allNotes.filter { it.colour == currentColor }
    private val hold = SingleUseHold()
    private lateinit var tabLayout: TabLayout
    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            currentColor = getColorList()[tab.position]
            tabLayout.setSelectedTabIndicatorColor(getColorList()[tab.position])
            notifyDataSetChanged()
        }
    }
    var currentColor = context.getColorAccent()
    private val googleColours = arrayOf(currentColor, Color.parseColor("#DB4437"),
                                        Color.parseColor("#F4B400"), Color.parseColor("#0F9D58"))

    init {
        GlobalScope.launch {
            allNotes = DatabaseStore.getAccessObject(context).allNotes.toMutableList()
        }.invokeOnCompletion {
            runOnMainThread {
                notifyDataSetChanged()
                hold.trigger()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NotesViewHolder(parent)
    override fun getItemCount() = if (::allNotes.isInitialized) notes.size else 0
    fun bindToTabLayout(tabLayout: TabLayout) {
        this.tabLayout = tabLayout
        GlobalScope.launch {
            hold.waitFor()
            tabLayout.post {
                tabLayout.removeOnTabSelectedListener(tabSelectedListener)
                tabLayout.removeAllTabs()
                getColorList().forEach {
                    tabLayout.addTab(tabLayout.newTab().apply {
                        icon = R.drawable.circle.fromDrawableRes(context).duplicateAndSetColour(it).apply {
                            setColorFilter(it, PorterDuff.Mode.SRC_OVER)
                        }
                    })
                }
                if (getColorList().contains(currentColor)) {
                    tabLayout.getTabAt(getColorList().indexOf(currentColor))!!.select()
                } else {
                    currentColor = getColorList()[0]
                }
                tabLayout.addOnTabSelectedListener(tabSelectedListener)
            }
        }
    }

    public fun getColorList() = (googleColours + allNotes.map {
        it.colour
    }.sorted()).distinct()

    fun add(note: Note) {
        val oldColors = getColorList()
        GlobalScope.launch {
            hold.waitFor()
            DatabaseStore.getAccessObject(context).insert(note);
        }.invokeOnCompletion {
            allNotes.add(note)
            if (note.colour == currentColor) {
                runOnMainThread { notifyItemInserted(notes.size) }
            } else {
                runOnMainThread {
                    if (oldColors != getColorList()) {
                        tabLayout.addTab(tabLayout.newTab().apply {
                            icon = R.drawable.circle.fromDrawableRes(context).duplicateAndSetColour(note.colour).apply {
                                setColorFilter(note.colour, PorterDuff.Mode.SRC_OVER)
                            }
                        }, getColorList().indexOf(note.colour))
                    }
                }
            }
        }
    }

    fun remove(note: Note) {
        val oldColors = getColorList()
        GlobalScope.launch {
            hold.waitFor()
            DatabaseStore.getAccessObject(context).remove(note);
        }.invokeOnCompletion {
            val oldIndex = notes.indexOf(note)
            allNotes.minusAssign(note)
            runOnMainThread {
                notifyItemRemoved(oldIndex)
                if (oldColors != getColorList()) {
                    tabLayout.removeTabAt(oldColors.indexOf(currentColor))
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView,
                                          viewHolder: RecyclerView.ViewHolder) = makeMovementFlags(
                    0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT)

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Nothing = error(
                    "reorganization has not yet been implemented")

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                remove(notes[viewHolder.adapterPosition])
            }

            override fun isItemViewSwipeEnabled() = true
        }).attachToRecyclerView(recyclerView);
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.item.setup(notes[position].title, notes[position].content);
        holder.item.isSelected = notes[position].selected
        holder.item.setOnClickListener {
            GlobalScope.launch {
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
                GlobalScope.launch {
                    hold.waitFor()
                    DatabaseStore.getAccessObject(context)
                            .setContent(notes[holder.adapterPosition].id, editText.text.toString())
                    notes[holder.adapterPosition].content = editText.text.toString()
                }.invokeOnCompletion {
                    runOnMainThread {
                        notifyItemChanged(holder.adapterPosition)
                    }
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
                    val oldColors = getColorList()
                    GlobalScope.launch {
                        DatabaseStore.getAccessObject(context)
                                .setColor(notes[holder.adapterPosition].id,
                                          dialog.findViewById<ChromaView>(
                                                  R.id.color_view).currentColor)
                        notes[holder.adapterPosition].colour =
                                dialog.findViewById<ChromaView>(R.id.color_view).currentColor
                    }.invokeOnCompletion {
                        runOnMainThread {
                            if (dialog.findViewById<ChromaView>(
                                            R.id.color_view).currentColor == currentColor) {
                                notifyItemChanged(holder.adapterPosition)
                            } else {
                                notifyItemRemoved(holder.adapterPosition)
                                if (getColorList() != oldColors) {
                                    bindToTabLayout(tabLayout)
                                }
                            }
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

class NotesViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.generic_selectable_item, parent,
                                                    false)) {
    val item: SelectableRoundedView by lazy { itemView as SelectableRoundedView }
}
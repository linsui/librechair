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
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.colors.SingleUseColorDialog
import ch.deletescape.lawnchair.feed.DbScope
import ch.deletescape.lawnchair.theme.ThemeOverride
import ch.deletescape.lawnchair.util.SingleUseHold
import ch.deletescape.lawnchair.views.SelectableRoundedView
import com.android.launcher3.R
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import me.priyesh.chroma.ColorMode
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.jvm.isAccessible

open class NotesAdapter(open val context: Context, savedInstanceColor: Int = context.getColorEngineAccent()) : androidx.recyclerview.widget.RecyclerView.Adapter<NotesViewHolder>() {
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
            onTabChangeListeners.forEach { it(getColorList()[tab.position] ) }
            notifyDataSetChanged()
        }
    }
    var currentColor = savedInstanceColor
    var onTabChangeListeners = mutableListOf<(color: Int) -> Unit>()
    private lateinit var tabNameMap: MutableMap<Int, String>
    private val googleColours =
            arrayOf(currentColor, Color.parseColor("#DB4437"), Color.parseColor("#F4B400"),
                    Color.parseColor("#0F9D58"))

    init {
        if (this !is SimpleNoteAdapter) {
            DbScope.launch {
                allNotes = DatabaseStore.getAccessObject(context).allNotes.toMutableList()
                tabNameMap = DatabaseStore.getTabNameDbInstance(context).access().all
                        .map { it.color to it.name }.toMap().toMutableMap().also {
                            getColorList().forEach { color ->
                                if (!it.containsKey(color)) {
                                    it.put(color, "")
                                }
                            }
                        }
            }.invokeOnCompletion {
                runOnMainThread {
                    notifyDataSetChanged()
                    hold.trigger()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NotesViewHolder(parent)
    override fun getItemCount() = if (::allNotes.isInitialized && ::tabNameMap.isInitialized) notes.size else 0

    open fun bindToTabLayout(tabLayout: TabLayout) {
        this.tabLayout = tabLayout
        DbScope.launch {
            hold.waitFor()
            tabLayout.post {
                onTabChangeListeners.forEach { it(getColorList()[0] ) }
                tabLayout.removeOnTabSelectedListener(tabSelectedListener)
                tabLayout.removeAllTabs()
                getColorList().forEach {
                    tabLayout.addTab(tabLayout.newTab().apply {
                        icon = R.drawable.circle.fromDrawableRes(context).tint(it)
                                .apply {
                                    setColorFilter(it, PorterDuff.Mode.SRC_OVER)
                                }
                        text = tabNameMap[it]
                    })
                }
                if (getColorList().contains(currentColor)) {
                    tabLayout.getTabAt(getColorList().indexOf(currentColor))!!.select()
                } else {
                    currentColor = getColorList()[0]
                }
                tabLayout.addOnTabSelectedListener(tabSelectedListener)
                updateTabLayout(tabLayout)
            }
        }
    }

    fun updateTabLayout(layout: TabLayout) {
        val view = layout.getChildAt(0) as ViewGroup;
        for (i in 0 until view.childCount) {
            val tab = view.getChildAt(i);
            tab.setOnLongClickListener {
                val dialog = object :
                        AlertDialog(context, ThemeOverride.AlertDialog().getTheme(context)) {}
                val editText: EditText
                dialog.setTitle(R.string.name)
                dialog.setView(LinearLayout(context).apply {
                    addView(EditText(context).apply {
                        editText = this
                        setText(tabNameMap[getColorList()[i]])
                        layoutParams = LinearLayout
                                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                              ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                            marginEnd = 16f.applyAsDip(context).toInt()
                            marginStart = 16f.applyAsDip(context).toInt()
                        }
                    })
                })
                dialog.setButton(Dialog.BUTTON_POSITIVE,
                                 android.R.string.ok.fromStringRes(context)) { dialog, which ->
                    DbScope.launch {
                        if (DatabaseStore.getTabNameDbInstance(context).access().findEntryForColor(
                                        getColorList()[i]) != null) {
                            DatabaseStore.getTabNameDbInstance(context).access()
                                    .updateTabName(getColorList()[i],
                                                   editText.text.trim().toString());
                        } else {
                            DatabaseStore.getTabNameDbInstance(context).access()
                                    .insert(TabDatabaseEntry().apply {
                                        color = getColorList()[i]
                                        name = editText.text.trim().toString()
                                    })
                        }
                        synchronized(tabNameMap) {
                            tabNameMap[getColorList()[i]] = editText.text.trim().toString()
                        }
                        tabLayout.post {
                            tabLayout.apply {
                                getTabAt(i)!!.text = editText.text.trim().toString()
                                updateTabLayout(tabLayout)
                            }
                        }
                    }
                }
                dialog.show()
                true
            }
            if (tab::class.declaredMembers.any { it.name == "textView" }) {
                val textView = tab::class.declaredMembers.first { it.name == "textView" }.apply {
                    isAccessible = true
                }.call(tab) as TextView
                textView.setTextColor(getColorList()[i])
            }
        }
    }

    fun getColorList() = if (::allNotes.isInitialized) (googleColours + allNotes.map {
        it.colour
    }.sorted()).distinct() else googleColours.toList()

    open fun add(note: Note) {
        val oldColors = getColorList()
        DbScope.launch {
            hold.waitFor()
            DatabaseStore.getAccessObject(context).insert(note);
        }.invokeOnCompletion {
            allNotes.add(note)
            if (note.colour == currentColor) {
                runOnMainThread { notifyItemInserted(notes.size) }
            } else {
                if (oldColors != getColorList()) {
                    DbScope.launch {
                        DatabaseStore.getTabNameDbInstance(context).access()
                                .insert(TabDatabaseEntry().apply {
                                    color = note.colour
                                    name = ""
                                })
                    }.invokeOnCompletion {
                        if (!tabNameMap.containsKey(note.colour)) {
                            tabNameMap[note.colour] = "";
                        }
                        tabLayout.apply {
                            post {
                                addTab(tabLayout.newTab().apply {
                                    icon = R.drawable.circle.fromDrawableRes(context)
                                            .tint(note.colour).apply {
                                                setColorFilter(note.colour,
                                                               PorterDuff.Mode.SRC_OVER)
                                            }
                                    text = tabNameMap[note.colour]
                                }, getColorList().indexOf(note.colour))
                                updateTabLayout(this)
                            }
                        }
                    }
                }
            }
        }
    }

    open fun remove(note: Note) {
        val oldColors = getColorList()
        DbScope.launch {
            hold.waitFor()
            DatabaseStore.getAccessObject(context).remove(note);
        }.invokeOnCompletion {
            val oldIndex = notes.indexOf(note)
            allNotes.minusAssign(note)
            runOnMainThread {
                notifyItemRemoved(oldIndex)
                if (oldColors != getColorList()) {
                    tabLayout.removeTabAt(oldColors.indexOf(currentColor))
                    updateTabLayout(tabLayout)
                    DbScope.launch {
                        DatabaseStore.getTabNameDbInstance(context).access().remove(note.colour)
                        synchronized(tabNameMap) {
                            tabNameMap.remove(note.colour)
                        }
                    }
                }
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

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.item.setup(notes[position].title, notes[position].content);
        holder.item.isSelected = notes[position].selected
        holder.item.setOnClickListener {
            DbScope.launch {
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
                DbScope.launch {
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
                /* val dialog = object : android.app.AlertDialog(context,
                                                              ThemeOverride.AlertDialog().getTheme(
                                                                      context)) {}
                dialog.setView(ChromaView(notes[holder.adapterPosition].colour, ColorMode.RGB,
                                          context).apply {
                    id = R.id.color_view
                })
                dialog.setTitle(R.string.title_dialog_select_color);
                dialog.setButton(Dialog.BUTTON_POSITIVE, android.R.string.ok.fromStringRes(
                        context)) { dialogInterface, which ->
                    val oldColors = getColorList()
                    DbScope.launch {
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
                dialog.show() */
                val dialog = SingleUseColorDialog(context, notes[holder.adapterPosition].colour, context.resources.getStringArray(R.array.resolvers_accent).toList(), ColorMode.RGB) {
                    val oldColors = getColorList()
                    DbScope.launch {
                        DatabaseStore.getAccessObject(context)
                                .setColor(notes[holder.adapterPosition].id, it)
                        notes[holder.adapterPosition].colour = it
                    }.invokeOnCompletion {_ ->
                        runOnMainThread {
                            if (it == currentColor) {
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
                dialog.show();
            }
            editDialog.show()
            true
        }
    }
}

class NotesViewHolder(parent: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.generic_selectable_item, parent,
                                                    false)) {
    val item: SelectableRoundedView by lazy { itemView as SelectableRoundedView }
}
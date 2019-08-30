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

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import ch.deletescape.lawnchair.duplicateAndSetColour
import ch.deletescape.lawnchair.fromDrawableRes
import ch.deletescape.lawnchair.getColorAttr
import ch.deletescape.lawnchair.getColorEngineAccent
import ch.deletescape.lawnchair.settings.ui.SettingsBaseActivity
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import com.google.gson.Gson
import java.io.Serializable

@Suppress("TypeParameterFindViewById")
class NotesActivity : SettingsBaseActivity() {
    val recycler by lazy { findViewById(R.id.notes_recycler) as RecyclerView }
    val tabLayout by lazy { findViewById(R.id.note_tabs) as TabLayout }
    lateinit var adapter: NotesAdapter
    var useSimpleNotesAdapter: Boolean = false

    lateinit var addItem: MenuItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        d("onCreate: instance state is $savedInstanceState")
        useSimpleNotesAdapter = savedInstanceState?.getBoolean("vertical_view") == true
        if (useSimpleNotesAdapter) {
            adapter = SimpleNoteAdapter(this)
            tabLayout.visibility = View.GONE
        } else {
            adapter = NotesAdapter(this)
            tabLayout.visibility = View.VISIBLE
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        adapter.currentColor = savedInstanceState?.getInt("persist_color") ?: getColorEngineAccent()
        adapter.bindToTabLayout(tabLayout)
        var toolbarMenuView: ActionMenuView? = null
        val count = toolbar.getChildCount()
        for (i in 0 until count) {
            val child = toolbar.getChildAt(i)
            if (child is ActionMenuView) {
                toolbarMenuView = child
                break
            }
        }
        if (toolbarMenuView != null) {
            toolbarMenuView.overflowIcon?.setTint(adapter.currentColor)
        }
        adapter.onTabChangeListeners.add {
            if (::addItem.isInitialized) {
                addItem.icon = addItem.icon.duplicateAndSetColour(it)
            }
            var menuView: ActionMenuView? = null
            val count = toolbar.getChildCount()
            for (i in 0 until count) {
                val child = toolbar.getChildAt(i)
                if (child is ActionMenuView) {
                    menuView = child
                    break
                }
            }
            if (menuView != null) {
                menuView.overflowIcon?.setTint(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(getString(R.string.title_menu_item_new_note)).apply {
            addItem = this;
            icon = R.drawable.ic_add.fromDrawableRes(this@NotesActivity)
                    .duplicateAndSetColour(this@NotesActivity.getColorAttr(R.attr.colorAccent))
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener {
                startActivityForResult(
                        Intent(this@NotesActivity, NewNoteActivity::class.java)
                                .putExtra("current_color", adapter.currentColor)
                                .putExtra("color_list", adapter.getColorList().toMutableList() as Serializable), 0)
                true
            }
        }
        menu.add(STYLE_GROUP, Menu.NONE, Menu.NONE, R.string.title_menu_item_note_style).setOnMenuItemClickListener { item: MenuItem ->
            useSimpleNotesAdapter = useSimpleNotesAdapter.not()
            recreate()
            true
        }.setChecked(useSimpleNotesAdapter)
        menu.setGroupCheckable(STYLE_GROUP, true, false)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data?.getParcelableExtra<Note>(NewNoteActivity.RETURN_NOTE) != null) {
            d("onActivityResult: creating note ${Gson().toJson(
                    data.getParcelableExtra<Note>(NewNoteActivity.RETURN_NOTE))}")
            adapter.add(data.getParcelableExtra(NewNoteActivity.RETURN_NOTE))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("persist_color", adapter.currentColor)
        outState.putBoolean("vertical_view", useSimpleNotesAdapter)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val STYLE_GROUP = 2;
    }
}

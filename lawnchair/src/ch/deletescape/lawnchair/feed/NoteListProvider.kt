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

package ch.deletescape.lawnchair.feed

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.ViewGroup
import android.widget.TextView
import ch.deletescape.lawnchair.*
import ch.deletescape.lawnchair.todo.INoteProvider
import ch.deletescape.lawnchair.todo.NoteUtils
import com.android.launcher3.R

class NoteListProvider(c: Context) : FeedProvider(c) {
    private val providerMap: MutableMap<Intent, INoteProvider> = mutableMapOf()
    fun updateBindings(intent: Intent? = null) {
        if (intent != null) {
            try {
                context.bindService(intent, object : ServiceConnection {
                    override fun onServiceDisconnected(name: ComponentName?) {
                        providerMap.remove(intent)
                        updateBindings(intent)
                    }

                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        try {
                            providerMap += intent to INoteProvider.Stub.asInterface(service)
                        } catch (e: RuntimeException) {
                            e.printStackTrace()
                        }
                    }
                }, Context.BIND_AUTO_CREATE)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            NoteUtils.getTodoProviderIntents(context).forEach {
                updateBindings(it)
            }
        }
    }

    override fun onFeedShown() {
    }

    override fun onFeedHidden() {
    }

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun getCards(): List<Card> {
        if (providerMap.isEmpty()) {
            updateBindings()
        }
        return listOf(Card(null, null, { parent, _ ->
            (parent as ViewGroup).inflate(R.layout.manage_notes).apply {
                setOnClickListener {
                    NoteProviderScreen(context).display(this@NoteListProvider, it.getPostionOnScreen().first,
                                                        it.getPostionOnScreen().second)
                }
            }
        }, Card.RAISE or Card.NO_HEADER, "nosort, top",
                           "manageNotes".hashCode())) + providerMap.map {
            it.value.notes
        }.flatten().map {
            Card(R.drawable.ic_note_black_24dp.fromDrawableRes(context).duplicateAndSetColour(
                    if (it.color == 0) context.getColorAttr(R.attr.colorAccent) else it.color),
                 it.title, { parent, _ ->
                     TextView(parent.context).apply {
                         text = it.content
                     }
                 }, Card.RAISE, "")
        }
    }
}
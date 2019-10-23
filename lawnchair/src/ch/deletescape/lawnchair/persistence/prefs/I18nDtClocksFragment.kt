/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.persistence.prefs

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.deletescape.lawnchair.inflate
import ch.deletescape.lawnchair.locale
import ch.deletescape.lawnchair.persistence.feedPrefs
import ch.deletescape.lawnchair.preferences.ProviderItemViewHolder
import ch.deletescape.lawnchair.theme.ThemeOverride
import ch.deletescape.lawnchair.util.SingleUseHold
import com.android.launcher3.R
import kotlinx.coroutines.runBlocking
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

class I18nDtClocksFragment : PreferenceDialogFragmentCompat() {
    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        val recyclerView = view.findViewById(R.id.list) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = Adapter(view.context)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val context = context!!
            AlertDialog.Builder(context, ThemeOverride.AlertDialog().getTheme(context))
                    .setItems(ZoneId.getAvailableZoneIds().map {
                        ZoneId.of(it).getDisplayName(TextStyle.FULL, context.locale)
                    }.toTypedArray()) { _, which ->
                        context.feedPrefs.clockTimeZones += ZoneId.getAvailableZoneIds()
                                .elementAt(which)
                    }.show()
        }
    }

    private class Adapter(val context: Context) : RecyclerView.Adapter<ProviderItemViewHolder>() {
        private val initHold = SingleUseHold()
        private lateinit var itemTouchHelper: ItemTouchHelper

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    val startPos = viewHolder.adapterPosition
                    val endPos = target.adapterPosition
                    if (startPos < endPos) {
                        for (i in startPos until endPos) {
                            Collections.swap(context.feedPrefs.clockTimeZones, i, i + 1)
                        }
                    } else {
                        for (i in endPos downTo startPos) {
                            Collections.swap(context.feedPrefs.clockTimeZones, i, i - 1)
                        }
                    }
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.adapterPosition
                    context.feedPrefs.clockTimeZones.removeAt(pos)
                }

                override fun isLongPressDragEnabled() = true

            }).also { itemTouchHelper = it }.attachToRecyclerView(recyclerView)
        }

        override fun onBindViewHolder(holder: ProviderItemViewHolder, position: Int) {
            runBlocking { initHold.waitFor() }
            holder.summary.text = ZoneId.of(context.feedPrefs.clockTimeZones[position])
                    .getDisplayName(TextStyle.FULL, context.locale)
            holder.dragHandle.visibility = View.VISIBLE
            @Suppress("ClickableViewAccessibility")
            holder.dragHandle.setOnTouchListener { _, _ ->
                itemTouchHelper.startDrag(holder)
                true
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderItemViewHolder {
            return ProviderItemViewHolder(parent.inflate(R.layout.event_provider_dialog_item))
        }

        override fun getItemCount(): Int {
            return context.feedPrefs.clockTimeZones.size
        }
    }
}

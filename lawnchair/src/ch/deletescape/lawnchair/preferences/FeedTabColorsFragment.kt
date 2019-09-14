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

package ch.deletescape.lawnchair.preferences

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.deletescape.lawnchair.feed.tabs.colors.custom.ColorDb
import ch.deletescape.lawnchair.inflate
import ch.deletescape.lawnchair.util.SingleUseHold
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import com.android.launcher3.R.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.stream.Collectors

class FeedTabColorsFragment : PreferenceDialogFragmentCompat() {
    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        val recyclerView = view.findViewById(R.id.list) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = Adapter(view.context)
    }

    override fun onDialogClosed(positiveResult: Boolean) {

    }

    private class Adapter(val context: Context) : RecyclerView.Adapter<ProviderItemViewHolder>() {
        private val colors = mutableListOf<Int>()
        private val initHold = SingleUseHold()

        init {
            GlobalScope.launch {
                colors.addAll(async {
                    val colors = ColorDb.getInstance(context).dao().everything()
                    val ordered = IntArray(colors.size)
                    for (color in colors) {
                        ordered[color.index] = color.color
                    }
                    ordered.toList()
                }.await())
                initHold.trigger()
            }
        }

        override fun onBindViewHolder(holder: ProviderItemViewHolder, position: Int) {
            runBlocking { initHold.waitFor() }
            holder.summary.text = "    "
            holder.summary.background = ColorDrawable(colors[holder.adapterPosition])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderItemViewHolder {
            return ProviderItemViewHolder(parent)
        }

        override fun getItemCount(): Int {
            runBlocking { initHold.waitFor() }
            return colors.size
        }
    }
}

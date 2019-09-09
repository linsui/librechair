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

import android.view.View
import android.widget.TextView
import com.android.launcher3.R

class ProviderItemViewHolder(itemView: View,
                             val title: TextView = itemView.findViewById(android.R.id.title),
                             val summary: TextView = itemView.findViewById(android.R.id.summary),
                             val dragHandle: View = itemView.findViewById(R.id.drag_handle)) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)
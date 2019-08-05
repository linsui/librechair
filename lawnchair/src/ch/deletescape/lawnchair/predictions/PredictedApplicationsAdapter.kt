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

package ch.deletescape.lawnchair.predictions

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ch.deletescape.lawnchair.atMost
import com.android.launcher3.AppInfo
import com.android.launcher3.BubbleTextView
import com.android.launcher3.ItemInfoWithIcon
import com.android.launcher3.R
import com.android.launcher3.allapps.AllAppsStore
import com.google.android.apps.nexuslauncher.search.AppItemInfoWithIcon
import com.google.android.apps.nexuslauncher.util.ComponentKeyMapper
import org.apache.commons.lang3.mutable.Mutable
import java.util.ArrayList

open class PredictedApplicationsAdapter : RecyclerView.Adapter<IconViewViewHolder>() {
    open val gridSize = 7
    var predictions: List<ComponentKeyMapper> = mutableListOf()
        set(value) = {
            field.apply {
                this as MutableList
                clear()
                addAll(value)
            }
        }()
        get() = listOf(* field.toTypedArray())
    val appStore = AllAppsStore()
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): IconViewViewHolder = IconViewViewHolder(parent)

    override fun getItemCount(): Int = atMost(predictions.size, gridSize)
    override fun onBindViewHolder(holder: IconViewViewHolder, position: Int) {
        val mapper = predictions.get(position)
        val appInfo = mapper.getApp(appStore)
        if (appInfo is AppInfo) {
            holder.bubbleTextView.applyFromApplicationInfo(appInfo)
        }
    }
}

class IconViewViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.all_apps_icon, parent, false)) {
    val bubbleTextView by lazy { itemView as BubbleTextView }
}
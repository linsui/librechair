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

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ch.deletescape.lawnchair.allapps.ParcelableComponentKeyMapper
import ch.deletescape.lawnchair.atMost
import ch.deletescape.lawnchair.feed.util.FeedUtil

open class PredictedApplicationsAdapter(val context: Context) :
        RecyclerView.Adapter<IconViewViewHolder>() {
    open val gridSize = 6
    var predictions: List<ParcelableComponentKeyMapper> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): IconViewViewHolder = IconViewViewHolder(parent)

    override fun getItemCount(): Int = atMost(predictions.size, gridSize)
    override fun onBindViewHolder(holder: IconViewViewHolder, position: Int) {
        val mapper = predictions.get(position)
        val icon = context.packageManager.getActivityIcon(
                mapper.componentKey.componentName)
        val title = context.packageManager.getActivityInfo(
                mapper.componentKey.componentName, 0).loadLabel(
                context.packageManager).toString()
        holder.view.setImageDrawable(icon)
        holder.view.setOnClickListener {
            FeedUtil.startActivity(context, Intent().setComponent(
                    mapper.componentKey.componentName), holder.view)
        }
    }
}

class IconViewViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(ImageView(parent.context)) {
    internal val view = super.itemView as ImageView
}
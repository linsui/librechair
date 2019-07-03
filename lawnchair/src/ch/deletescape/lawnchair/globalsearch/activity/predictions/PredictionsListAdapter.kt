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

package ch.deletescape.lawnchair.globalsearch.activity.predictions

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class PredictionsListAdapter(predictions: List<String>, onCallHandler: OnCallHandler) :
        RecyclerView.Adapter<PredictionsListAdapter.PredictionViewHolder>() {

    var onCallHandler = onCallHandler
        get() = field
        set(value) {
            field = value
        }
    var predictions = predictions
        get() = field
        set(value) {
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        return PredictionViewHolder(
            LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent,
                                                        false))
    }

    override fun getItemCount(): Int {
        return predictions.size
    }

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        (holder.itemView as TextView).setText(predictions.get(position))
        holder.itemView.setOnClickListener({
                                               onCallHandler.onClick(position)
                                           })
    }

    class PredictionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnCallHandler {
        fun onClick(item: Int)
    }
}
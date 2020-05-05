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

package ch.deletescape.lawnchair.feed.go

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ch.deletescape.lawnchair.allChildren
import ch.deletescape.lawnchair.applyAsDip
import ch.deletescape.lawnchair.font.CustomFontManager
import ch.deletescape.lawnchair.theme.ThemeManager
import com.google.android.material.card.MaterialCardView
import kotlin.math.roundToInt


class GoAdapter(val factories: List<GoCardFactory>) : RecyclerView.Adapter<GoCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GoCardViewHolder(
            parent.context)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                        state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.top = 8f.applyAsDip(recyclerView.context).roundToInt()
                outRect.bottom = 8f.applyAsDip(recyclerView.context).roundToInt()
                outRect.right = 8f.applyAsDip(recyclerView.context).roundToInt()
                outRect.left = 8f.applyAsDip(recyclerView.context).roundToInt()
            }
        })
    }

    override fun getItemCount() = factories.size
    override fun onBindViewHolder(holder: GoCardViewHolder, position: Int) {
        val card = factories[position].card
        if (card == null) {
            holder.cv.visibility = View.GONE
        } else {
            holder.cv.visibility = View.VISIBLE
            holder.cv.removeAllViews()
            holder.cv.addView(card.viewFactory(holder.cv).also {
                if (it is ViewGroup) {
                    it.allChildren.filterIsInstance<TextView>().forEach {
                        CustomFontManager.getInstance(holder.context)
                                .setCustomFont(it as TextView, CustomFontManager.FONT_TEXT,
                                        it.typeface.style)
                    }
                }
            })
        }
    }

}

data class GoCard(val viewFactory: (parent: ViewGroup) -> View)

class GoCardViewHolder(val context: Context) : RecyclerView.ViewHolder(MaterialCardView(context).apply {
    layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT)
}) {
    val cv = itemView as CardView

    init {
        cv.cardElevation = 4f.applyAsDip(context)
        cv.setCardBackgroundColor((if (context.isDark) Color.DKGRAY else Color.WHITE))
    }
}

val Context.isDark
    get() = ThemeManager.getInstance(this).isDark
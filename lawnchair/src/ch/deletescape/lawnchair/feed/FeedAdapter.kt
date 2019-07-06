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

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ch.deletescape.lawnchair.theme.ThemeManager
import com.android.launcher3.R
import java.util.*

class FeedAdapter(var providers: List<FeedProvider>, val context: Context) : RecyclerView.Adapter<CardViewHolder>() {
    private val cards = ArrayList<Card>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return cards[position].type
    }

    override fun getItemCount(): Int {
        cards.clear()
        var i = 0;
        providers.iterator().forEachRemaining {
            it.onAttachedToAdapter(this)
            i += it.cards.size
            cards.addAll(it.cards)
        }
        return i;
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        providers.iterator().forEachRemaining {
            it.onDestroy()
        }
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.description.text = cards[position].title
        holder.icon.setImageDrawable(cards[position].icon)
        holder.viewHolder.removeAllViewsInLayout()
        holder.viewHolder.addView(cards[position].inflateHelper.inflate(holder.viewHolder))
        if (holder.itemViewType and Card.RAISE == 1 && ThemeManager.getInstance(context).isDark) {
            (holder.itemView as CardView).setCardBackgroundColor(context.getColor(R.color.qsb_background_dark));
        } else {
            (holder.itemView as CardView).setCardBackgroundColor(context.getColor(R.color.qsb_background));
        }
    }

}

class CardViewHolder : RecyclerView.ViewHolder {
    val icon: ImageView by lazy {
        itemView.findViewById(R.id.card_provider_small_icon) as ImageView
    }
    val description: TextView by lazy { itemView.findViewById(R.id.card_title) as TextView }
    val viewHolder: LinearLayout by lazy {
        itemView.findViewById(R.id.card_view_holder) as LinearLayout
    }

    constructor(parent: ViewGroup, type: Int) : super(
        LayoutInflater.from(parent.context).inflate(when (type) {
                                                        Card.DEFAULT -> R.layout.card_default
                                                        Card.DEFAULT or Card.NARROW -> R.layout.card_narrow
                                                        Card.DEFAULT or Card.RAISE -> R.layout.card_raised
                                                        Card.DEFAULT or Card.RAISE or Card.NARROW -> R.layout.card_raised_narrow
                                                        Card.DEFAULT or Card.TEXT_ONLY -> R.layout.card_text_only
                                                        Card.DEFAULT or Card.RAISE or Card.TEXT_ONLY -> R.layout.card_raised_text_only

                                                        else -> error("magic: invalid bitmask")
                                                    }, parent, false)) {
        if (type and Card.TEXT_ONLY == 1) {
            viewHolder.visibility = View.GONE
        }
    }
}
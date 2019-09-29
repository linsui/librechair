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

package ch.deletescape.lawnchair.feed.chips

import android.content.Context
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.FeedProvider
import com.android.launcher3.R

class ChipCardProvider(c: Context?) : FeedProvider(c) {
    lateinit var recycler: RecyclerView

    override fun onFeedShown() {

    }

    override fun onFeedHidden() {

    }

    override fun onCreate() {

    }

    override fun onDestroy() {

    }

    override fun getCards(): List<Card> {
        return listOf(Card(null, context.getString(R.string.title_card_chips), { parent, _ ->
            if (::recycler.isInitialized) recycler else RecyclerView(parent.context).apply {
                layoutManager =
                        LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                adapter = feed!!.chipAdapter
                setOnTouchListener { v, event ->
                    controllerView?.disallowInterceptCurrentTouchEvent = true
                    true
                }
                recycler = this
            }
        }, Card.RAISE, "nosort,top", "chips".hashCode()));
    }

}
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

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ch.deletescape.lawnchair.newList
import com.android.launcher3.R

class CategorizedSortingAlgorithm : AbstractFeedSortingAlgorithm() {
    @SuppressLint("DefaultLocale")
    override fun sort(vararg ts: List<Card>): List<Card> {
        val map: MutableMap<String, MutableList<Card>> = mutableMapOf()
        ts.forEach {
            it.forEach { card ->
                if (card.categories.isNullOrEmpty()) {
                    if (map[CATEGORY_NONE] == null) {
                        map += CATEGORY_NONE to mutableListOf()
                    }
                    map[CATEGORY_NONE]!!.add(card)
                } else {
                    card.categories!!.forEach { cards ->
                        if (map[cards.toLowerCase()] == null) {
                            map += cards.toLowerCase() to mutableListOf()
                        }
                        map[cards.toLowerCase()]!!.add(card)
                    }
                }
            }
        }
        val keys = map.keys.filter { it != CATEGORY_NONE }.sortedBy { it }
        val result = mutableListOf<Card>()
        result.addAll(map[CATEGORY_NONE]?.filter { it.algoFlags?.contains("top") == true } ?: newList())
        keys.forEach {
            result.add(Card(null, null, object : Card.Companion.InflateHelper {
                override fun inflate(parent: ViewGroup): View {
                    return LayoutInflater.from(parent.context)
                            .inflate(R.layout.category_header, parent, false).also { it2 ->
                        it2.findViewById<TextView>(R.id.category_header_category_name).text = it
                    }
                }

            }, Card.NO_HEADER, ""))
            result.addAll(map[it] ?: emptyList())
        }
        result.addAll(map[CATEGORY_NONE]?.filter { it.algoFlags?.contains("top") == false } ?: newList())
        return result
    }

    companion object {
        const val CATEGORY_NONE = "Uncategorized"
    }

}
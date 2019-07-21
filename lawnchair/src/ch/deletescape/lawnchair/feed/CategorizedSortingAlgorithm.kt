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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ch.deletescape.lawnchair.newList
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R

class CategorizedSortingAlgorithm : AbstractFeedSortingAlgorithm() {
    init {
        d("init: sorting algorithm initializer called")
    }
    override fun sort(vararg ts: List<Card>): List<Card> {
        d("sort: sorting $ts")
        val map: MutableMap<String, MutableList<Card>> = mutableMapOf()
        ts.forEach {
            d("sort: categories for card provider $it")
            it.forEach {
                d("sort: locating categories for card $it")
                val cardRef = it
                if (it.categories.isNullOrEmpty()) {
                    d("sort: no categories for card $it! classifying card as uncategorized (categories: ${it.categories}")
                    if (map.get(CATEGORY_NONE) == null) {
                        map += CATEGORY_NONE to mutableListOf()
                    }
                    map.get(CATEGORY_NONE)!!.add(it)
                } else {
                    it.categories!!.forEach {
                        d("sort: card $cardRef can be assigned to category $it")
                        if (map.get(it.toLowerCase()) == null) {
                            map += it.toLowerCase() to mutableListOf()
                        }
                        map.get(it.toLowerCase())!!.add(cardRef)
                    }
                }
            }
        }
        val keys = map.keys.filter { it != CATEGORY_NONE }.sortedBy { it }
        val result = mutableListOf<Card>()
        result.addAll(map.get(CATEGORY_NONE)?.filter { it.algoFlags?.contains("top") == true } ?: newList())
        keys.forEach {
            result.add(Card(null, null, object : Card.Companion.InflateHelper {
                override fun inflate(parent: ViewGroup): View {
                    d("inflate: inflating category header view")
                    val v =  LayoutInflater.from(parent.context)
                            .inflate(R.layout.category_header, parent, false)
                    v.findViewById<TextView>(R.id.category_header_category_name).text = it
                    return v
                }

            }, Card.NO_HEADER, ""))
            result.addAll(map.get(it) ?: emptyList())
        }
        result.addAll(map.get(CATEGORY_NONE)?.filter { it.algoFlags?.contains("top") == false } ?: newList())
        return result
    }

    companion object {
        val CATEGORY_NONE = "Uncategorized"
    }

}
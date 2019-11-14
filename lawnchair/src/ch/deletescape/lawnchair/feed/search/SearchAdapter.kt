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

@file:Suppress("NestedLambdaShadowedImplicitParameter")

package ch.deletescape.lawnchair.feed.search

import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.FeedProvider
import ch.deletescape.lawnchair.util.extensions.d

class SearchAdapter(private val parent: FeedAdapter, var searchQuery: String?) :
        FeedAdapter(parent.providers, parent.backgroundColor, parent.context, parent.feed) {
    private lateinit var cache: List<Card>
    override val cardCache: MutableMap<FeedProvider, List<Card>>
        get() = parent.cardCache
    override val cards
        get() = if (::cache.isInitialized) cache else emptyList()

    internal fun refreshSearch() {
        cache = parent.cards.let {
            val searchQuery = this.searchQuery
            if (searchQuery != null) it.filter {
                it.categories?.any {
                    matches(searchQuery, it)
                } == true || it.title?.let { matches(searchQuery, it) } == true
            } else emptyList()
        }
    }

    companion object {
        @JvmStatic
        fun matches(query: String, content: String): Boolean {
            if (query.startsWith(".re ")) {
                try {
                    d("matches: regex query is ${query.substring(4)}")
                    return content.matches(Regex(query.substring(4)))
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                    return false
                }
            } else {
                if (content.contains(query, true)) {
                    return true
                }
                val tokens = query.split(Regex("[ ,]+"))
                if (tokens.all { content.contains(it.trim(), true) }) {
                    return true
                }
                return false
            }
        }
    }
}
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

package ch.deletescape.lawnchair.feed.search

import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.FeedProvider

class SearchAdapter(private val parent: FeedAdapter, var searchQuery: String?) :
        FeedAdapter(parent.providers, parent.backgroundColor, parent.context, parent.feed) {
    override val cardCache: MutableMap<FeedProvider, List<Card>>
        get() = parent.cardCache
    override val cards
        get() = parent.cards.let {
            if (searchQuery != null) it.filter {
                it.categories?.any {
                    it.contains(searchQuery!!, true)
                } == true || it.title?.contains(searchQuery!!, true) == true
            } else emptyList()
        }
}
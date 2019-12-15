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

package ch.deletescape.lawnchair.feed

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlin.coroutines.CoroutineContext

object FeedScope : CoroutineScope {
    @ObsoleteCoroutinesApi
    override val coroutineContext: CoroutineContext =
            newFixedThreadPoolContext(256, "feed")

}

object FeedRefreshScope : CoroutineScope {
    @ObsoleteCoroutinesApi
    override val coroutineContext: CoroutineContext =
            newFixedThreadPoolContext(64, "feed")

}

object ArticleJobsScope : CoroutineScope {
    @ObsoleteCoroutinesApi
    override val coroutineContext: CoroutineContext =
            newFixedThreadPoolContext(64, "articles")

}

object LocationScope : CoroutineScope {
    @ObsoleteCoroutinesApi
    override val coroutineContext: CoroutineContext =
            newFixedThreadPoolContext(64, "lbclm")

}

object DbScope : CoroutineScope {
    @ObsoleteCoroutinesApi
    override val coroutineContext: CoroutineContext =
            newFixedThreadPoolContext(16, "dba")

}

object JobScope : CoroutineScope {
    @ObsoleteCoroutinesApi
    override val coroutineContext: CoroutineContext =
            newFixedThreadPoolContext(64, "overlay_jobs")

}

object CalendarScope : CoroutineScope {
    @ObsoleteCoroutinesApi
    override val coroutineContext: CoroutineContext =
            newFixedThreadPoolContext(1, "calendar_jobs")
}


object SearchScope : CoroutineScope {
    @ObsoleteCoroutinesApi
    override val coroutineContext: CoroutineContext =
            newFixedThreadPoolContext(1, "search_jobs")
}


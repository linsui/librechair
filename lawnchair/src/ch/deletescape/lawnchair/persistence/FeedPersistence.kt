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

package ch.deletescape.lawnchair.persistence

import android.content.Context
import ch.deletescape.lawnchair.feed.maps.MapProvider
import ch.deletescape.lawnchair.util.SingletonHolder

class FeedPersistence private constructor(val context: Context) {
    val useBackgroundImageAsScreenBackground
            by BooleanDelegate(context, "feed_background_as_screen_background", true)
    val useBoxBackgroundBlur
            by BooleanDelegate(context, "feed_boxed_background_blur", false)
    val useJavascriptInSearchScreen
            by BooleanDelegate(context, "feed_javascript_in_search_screen", false)
    val displayActionsAsMenu
            by BooleanDelegate(context, "feed_actions_as_menu", false)
    val mapProvider by DefValueStringDelegate(context, "feed_map_provider",
            MapProvider::class.qualifiedName!!)
    val enableHostsFilteringInWebView
            by BooleanDelegate(context, "feed_hosts_filtering", true)
    val directlyOpenLinksInBrowser
            by BooleanDelegate(context, "feed_directly_open_links", false)
    val conservativeRefreshes
            by BooleanDelegate(context, "feed_conservative_refreshes", true)

    companion object : SingletonHolder<FeedPersistence, Context>(::FeedPersistence)
}

val Context.feedPrefs get() = FeedPersistence.getInstance(this)
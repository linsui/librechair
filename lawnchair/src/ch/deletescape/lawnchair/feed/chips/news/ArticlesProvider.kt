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

package ch.deletescape.lawnchair.feed.chips.news

import android.content.Context
import ch.deletescape.lawnchair.feed.chips.ChipProvider
import ch.deletescape.lawnchair.fromDrawableRes
import ch.deletescape.lawnchair.persistence.cache.CacheTime
import ch.deletescape.lawnchair.runOnNewThread
import com.android.launcher3.R
import com.android.launcher3.Utilities
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.FeedException
import java.io.IOException
import java.util.concurrent.TimeUnit

abstract class ArticlesProvider(val context: Context) : ChipProvider() {
    var feed: SyndFeed? = null
    var entries: List<SyndEntry>? = null
    private val cacheTime = CacheTime(TimeUnit.DAYS.toMillis(1))

    init {
        runOnNewThread {
            try {
                feed = getFeed()
                entries = feed!!.entries.take(3)
                cacheTime.trigger()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: FeedException) {
                e.printStackTrace();
            }
        }
    }

    internal abstract fun getFeed(): SyndFeed

    override fun getItems(context: Context): List<Item> {
        return entries?.map {
            val item = Item()
            item.icon = R.drawable.ic_newspaper_24dp.fromDrawableRes(context)
            item.title = it.title.take(10) + "..."
            item.click = Runnable { Utilities.openURLinBrowser(context, it.uri) }
            item
        } ?: emptyList()
    }
}
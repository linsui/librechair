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
import ch.deletescape.lawnchair.newList
import com.prof.rssparser.Article
import com.prof.rssparser.OnTaskCompleted
import com.prof.rssparser.Parser
import java.util.concurrent.Executors

class CustomizableRSSProvider(c: Context) : AbstractMultipleSyndicationProvider(c) {
    override fun bindFeeds(handler: OnBindHandler) {
        Executors.newSingleThreadExecutor().submit {
            val rssProviders = listOf("https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en",
                                      "https://en.wikipedia.org/w/index.php?title=Main_Page&action=history&feed=rss")
            var syndicationFeeds = newList<List<Article>>();
            rssProviders.forEach {
                val parser = Parser()
                parser.execute(it)
                parser.onFinish(object : OnTaskCompleted {
                    override fun onTaskCompleted(list: MutableList<Article>) {
                        syndicationFeeds.add(list)
                        handler.bindFeed(syndicationFeeds);
                    }

                    override fun onError(e: Exception) {
                        e.printStackTrace()
                    }

                })
            }
        }
    }

}
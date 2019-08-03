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
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.newList
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.FeedException
import com.rometools.rome.io.SyndFeedInput
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.CharSequenceInputStream
import org.xml.sax.InputSource
import java.io.IOException
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.Executors

class CustomizableRSSProvider(c: Context) : AbstractMultipleSyndicationProvider(c) {
    override fun bindFeeds(handler: OnBindHandler) {
        Executors.newSingleThreadExecutor().submit {
            val rssProviders = LawnchairPreferences.getInstance(context).feedRSSSources.getAll()
            val syndicationFeeds = newList<SyndFeed>();
            rssProviders.forEach {
                val feed: String
                try {
                    feed = IOUtils.toString(
                            URL("https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en").openConnection().getInputStream(),
                            Charset.defaultCharset())
                    syndicationFeeds += SyndFeedInput().build(InputSource(
                            CharSequenceInputStream(feed, Charset.defaultCharset())))
                    handler.bindFeed(syndicationFeeds)
                } catch (e: FeedException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

}
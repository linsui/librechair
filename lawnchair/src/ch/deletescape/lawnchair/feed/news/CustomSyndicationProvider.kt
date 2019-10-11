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

package ch.deletescape.lawnchair.feed.news

import android.content.Context
import ch.deletescape.lawnchair.feed.AbstractRSSFeedProvider
import ch.deletescape.lawnchair.feed.FeedScope
import com.rometools.rome.io.FeedException
import com.rometools.rome.io.SyndFeedInput
import kotlinx.coroutines.launch
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.CharSequenceInputStream
import org.xml.sax.InputSource
import java.io.IOException
import java.net.URL
import java.nio.charset.Charset

class CustomSyndicationProvider(c: Context, internal val arguments: Map<String, String>) :
        AbstractRSSFeedProvider(c) {
    override fun bindFeed(callback: BindCallback) {
        FeedScope.launch {
            val feed: String
            try {
                feed = IOUtils.toString(
                        URL(arguments[URL])
                                .openConnection()
                                .getInputStream(), Charset
                        .defaultCharset())
                callback.onBind(SyndFeedInput().build(InputSource(
                        CharSequenceInputStream(feed, Charset.defaultCharset()))))
            } catch (e: FeedException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Suppress("MapGetWithNotNullAssertionOperator")
    override fun getId(): String {
        return arguments[URL]!!.hashCode().toString()
    }

    companion object  {
        const val URL = "synd::custom_url"
    }

}
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
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.CharSequenceInputStream
import org.xml.sax.InputSource
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.util.*

class GSyndicationFeedProvider(c: Context) : AbstractLocationAwareRSSProvider(c) {
    override fun getLocationAwareFeed(location: Pair<Double, Double>, country: String): SyndFeed {
        val feed = IOUtils.toString(URL("https://news.google.com/rss?gl=${Locale("",
                                                                                 country).country}").openConnection().also {
            (it as HttpURLConnection).instanceFollowRedirects = true
        }.getInputStream(), Charset.defaultCharset())
        return SyndFeedInput()
                .build(InputSource(CharSequenceInputStream(feed, Charset.defaultCharset())))
    }

    override fun getFallbackFeed(): SyndFeed {
        return getLocationAwareFeed(0.toDouble() to 0.toDouble(), "US");
    }
}

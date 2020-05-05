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
import ch.deletescape.lawnchair.feed.util.FeedUtil
import com.rometools.rome.io.SyndFeedInput
import org.xml.sax.InputSource
import java.util.function.Consumer

class CustomSyndicationProvider(c: Context, internal val arguments: Map<String, String>) :
        AbstractRSSFeedProvider(c) {
    override fun onInit(tokenCallback: Consumer<String>) {
        tokenCallback.accept(id)
    }

    override fun bindFeed(callback: BindCallback, token: String) {
        FeedUtil.download(arguments[URL]!!, context, {
            callback.onBind(SyndFeedInput().build(InputSource(it)))
        }, null)
    }

    @Suppress("MapGetWithNotNullAssertionOperator")
    override fun getId(): String {
        return (arguments[URL]!!.hashCode() + 1).toString()
    }

    companion object  {
        const val URL = "synd::custom_url"
    }

}
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

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.webkit.WebView
import ch.deletescape.lawnchair.lawnchairPrefs
import java.net.URL

class WebApplicationsProvider(context: Context) : FeedProvider(context) {
    val viewCache = mutableMapOf<URL, WebView>()
    override fun onFeedShown() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFeedHidden() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun getCards(): List<Card> {
        return context.lawnchairPrefs.feedWebApplications.mapNotNull {
            if (it.isShortcut) null else Card(null, it.title, { v, _ ->
                if (viewCache.containsKey(it.url)) viewCache[it.url]!! else WebView(context).apply {
                    settings.javaScriptEnabled = true
                    loadUrl(Uri.decode(it.url.toURI().toASCIIString()))
                }
            }, Card.RAISE, if (it.sort) "" else "nosort,top", it.url.hashCode())
        } // FIXME: add support for shortcuts
    }
}
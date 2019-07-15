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
import android.graphics.BitmapFactory
import android.support.constraint.ConstraintLayout
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.newList
import com.android.launcher3.R
import com.android.launcher3.Utilities
import com.prof.rssparser.Article
import java.io.IOException
import java.net.URL
import java.util.concurrent.Executors

abstract class AbstractMultipleSyndicationProvider(c: Context) : AbstractRSSFeedProvider(c) {

    internal var feeds: List<List<Article>>? = null

    init {
        bindFeeds(object : OnBindHandler {
            override fun bindFeed(feeds2: List<List<Article>>) {
                feeds = feeds2;
            }
        })
    }

    override fun bindFeed(callback: BindCallback) {

    }

    override fun getCards(): List<Card> {
        if (feeds == null) {
            return emptyList()
        } else {
            val cards = newList<List<Card>>()
            for (articles in feeds!!) {
                val temporary = newList<Card>()
                Log.d(javaClass.name, "getCards: iterating through entries: $articles")
                for (entry in articles) {
                    Log.d(javaClass.name, "getCards: syndication entry: $entry")
                    temporary.add(Card(null, null, object : Card.Companion.InflateHelper {
                        override fun inflate(parent: ViewGroup): View {
                            Log.d(javaClass.name, "getCards: inflate syndication: $entry")
                            val v = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.rss_item, parent, false)
                            val title: TextView
                            val description: TextView
                            val date: TextView
                            val icon: ImageView
                            val readMore: Button

                            title = v.findViewById(R.id.rss_item_title)
                            description = v.findViewById(R.id.rss_item_description)
                            icon = v.findViewById(R.id.rss_item_icon)
                            date = v.findViewById(R.id.rss_item_date)
                            readMore = v.findViewById(R.id.rss_item_read_more)

                            icon.visibility = View.INVISIBLE

                            Executors.newSingleThreadExecutor().submit {
                                try {
                                    icon.setImageBitmap(BitmapFactory.decodeStream(
                                        URL(entry.image).openConnection().getInputStream()))
                                    icon.post { icon.visibility = View.VISIBLE }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    icon.post {
                                        icon.layoutParams = ConstraintLayout
                                                .LayoutParams(0, icon.layoutParams.height)
                                    }
                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                    icon.post {
                                        icon.layoutParams = ConstraintLayout
                                                .LayoutParams(0, icon.layoutParams.height)
                                    }
                                }
                            }

                            title.text = entry.title
                            var spanned = Html.fromHtml(entry.description, 0).toString()
                            if (spanned.length > 256) {
                                spanned = spanned.subSequence(0, 256).toString() + "..."
                            }
                            description.text = spanned
                            readMore.setOnClickListener { v2 ->
                                Utilities.openURLinBrowser(v2.context, entry.link)
                            }

                            date.text = entry.pubDate
                            return v
                        }
                    }, Card.RAISE or Card.TEXT_ONLY, null))
                }
                cards.add(temporary)
            }

            val sorted = ReflectionUtils.inflateSortingAlgorithm(
                LawnchairPreferences.getInstance(context).feedPresenterAlgorithm)
                    .sort(* cards.toTypedArray())
            return sorted
        }
    }

    protected abstract fun bindFeeds(handler: OnBindHandler)

    protected interface OnBindHandler {
        fun bindFeed(feeds: List<List<Article>>)
    }
}

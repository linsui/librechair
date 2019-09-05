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
import android.content.Intent
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import ch.deletescape.lawnchair.LawnchairPreferences
import ch.deletescape.lawnchair.clickbait.ClickbaitRanker
import ch.deletescape.lawnchair.getPostionOnScreen
import ch.deletescape.lawnchair.newList
import ch.deletescape.lawnchair.reflection.ReflectionUtils
import ch.deletescape.lawnchair.thumbnailURL
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import com.rometools.rome.feed.synd.SyndFeed
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit

abstract class AbstractMultipleSyndicationProvider(c: Context) : AbstractRSSFeedProvider(c) {
    private var feeds: List<SyndFeed>? = null
    private var lastUpdate: Long = 0

    init {
        @Suppress("LeakingThis") bindFeeds(object : OnBindHandler {
            override fun bindFeed(feeds2: List<SyndFeed>) {
                feeds = feeds2
                lastUpdate = System.currentTimeMillis()
            }
        })
    }

    override fun bindFeed(callback: BindCallback) {
    }

    override fun getCards(): List<Card> {
        if (System.currentTimeMillis() - lastUpdate > TimeUnit.MINUTES.toMinutes(15)) {
            bindFeeds(object : OnBindHandler {
                override fun bindFeed(feeds2: List<SyndFeed>) {
                    feeds = feeds2
                    lastUpdate = System.currentTimeMillis()
                }
            })
        }
        if (feeds == null) {
            return emptyList()
        } else {
            val cards = newList<List<Card>>()
            for (articles in feeds!!) {
                val temporary = newList<Card>()
                Log.d(javaClass.name, "getCards: iterating through entries: $articles")
                for (entry in articles.entries) {
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
                            val categories: TextView

                            title = v.findViewById(R.id.rss_item_title)
                            description = v.findViewById(R.id.rss_item_description)
                            icon = v.findViewById(R.id.rss_item_icon)
                            date = v.findViewById(R.id.rss_item_date)
                            readMore = v.findViewById(R.id.rss_item_read_more)
                            categories = v.findViewById(R.id.rss_item_categories)

                            d("inflate: Image URL is ${entry.thumbnailURL}")

                            if (entry.thumbnailURL != null && entry.thumbnailURL!!.startsWith(
                                            "http")) {
                                Picasso.Builder(parent.context).build().load(entry.thumbnailURL)
                                        .placeholder(R.drawable.work_tab_user_education).into(icon)
                            } else {
                                Picasso.Builder(parent.context).build()
                                        .load("https://" + entry.thumbnailURL)
                                        .placeholder(R.drawable.work_tab_user_education).into(icon)
                            }

                            if (entry.categories.isEmpty()) {
                                categories.text = ""
                            } else {
                                categories.text =
                                        entry.categories.map { it.name }.joinToString(", ")
                            }

                            title.text = ClickbaitRanker.completePipeline(entry.title)
                            var spanned = if (entry.description != null) Html.fromHtml(
                                    entry.description.value, 0).toString() else ""
                            if (spanned.length > 256) {
                                spanned = spanned.subSequence(0, 256).toString() + "..."
                            }
                            description.text = spanned
                            readMore.setOnClickListener { v2 ->
                                ArticleViewerScreen(context, entry.title,
                                        entry.categories.map { it.name }.joinToString(),
                                        entry.uri, entry.description?.value ?: "")
                                        .display(this@AbstractMultipleSyndicationProvider,
                                                v2.getPostionOnScreen().first + v2.width / 2,
                                                v2.getPostionOnScreen().second + v2.height / 2)
                            }

                            date.text = entry.publishedDate?.toLocaleString()
                            return v
                        }
                    }, Card.RAISE or Card.TEXT_ONLY, null, entry.hashCode(), true,
                            entry.categories.map { it.name }).apply {
                        actionName = context.getString(
                                context.resources.getIdentifier("whichSendApplicationLabel",
                                        "string", "android"))
                        actionListener = { context ->
                            val i = Intent(Intent.ACTION_SEND)
                            i.type = "text/plain"
                            i.putExtra(Intent.EXTRA_TEXT, entry.link)
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(i)
                            Unit
                        }
                    })
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
        fun bindFeed(feeds: List<SyndFeed>)
    }
}

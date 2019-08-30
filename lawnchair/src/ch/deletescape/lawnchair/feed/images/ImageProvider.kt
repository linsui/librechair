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

package ch.deletescape.lawnchair.feed.images

import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.room.InvalidationTracker
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.FeedProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ImageProvider(c: Context?) : FeedProvider(c) {
    val images = mutableListOf<Bitmap>()

    init {
        GlobalScope.launch {
            ImageDatabase.getInstance(context).apply {
                this.invalidationTracker.addObserver(object : InvalidationTracker.Observer("image") {
                    override fun onInvalidated(tables: MutableSet<String>) {
                        if (tables.contains("image")) {
                            GlobalScope.launch {
                                ImageDatabase.getInstance(context).access().getAll().forEach {
                                    images.add(ImageStore.getInstance(context).getBitmap(it.id))
                                }
                            }
                        }
                    }
                })
                access().getAll().forEach {
                    images.add(ImageStore.getInstance(context).getBitmap(it.id))
                }
            }
        }
    }

    override fun onFeedShown() {
    }

    override fun onFeedHidden() {
    }

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun getCards(): List<Card> {
        return images.map {
            Card(null, "", { parent, _ ->
                ImageView(parent.context).apply {
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                             ViewGroup.LayoutParams.MATCH_PARENT)
                    setImageBitmap(it)
                }
            }, Card.RAISE or Card.NO_HEADER, "nosort, top", it.hashCode())
        }
    }
}
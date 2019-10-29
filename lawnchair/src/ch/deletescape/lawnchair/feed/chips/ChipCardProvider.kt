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

package ch.deletescape.lawnchair.feed.chips

import android.content.Context
import android.graphics.drawable.VectorDrawable
import android.view.View
import ch.deletescape.lawnchair.colors.ColorEngine
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.impl.LauncherFeed
import ch.deletescape.lawnchair.feed.pod.FeedPod
import ch.deletescape.lawnchair.feed.pod.PodFeedProvider
import ch.deletescape.lawnchair.persistence.feedPrefs
import ch.deletescape.lawnchair.tint
import java.util.*
import java.util.function.Consumer

class ChipCardProvider(context: Context) : PodFeedProvider(context, null) {

    init {
        setDefaultPod { HorizontalChipPodImpl(context, feed) }
        setChangeCallback { HorizontalChipPodImpl(context, feed) }
    }

    override fun isVolatile() = context.feedPrefs.chipCompactCard.not()
}

class HorizontalChipPodImpl(val context: Context, val feed: LauncherFeed) : FeedPod,
        Consumer<List<ChipProvider.Item>?> {
    val items = mutableListOf<ChipProvider.Item>()
    val controller
        get() = ChipController.getInstance(context, feed)

    init {
        controller.subscribe(this)
    }

    override fun accept(t: List<ChipProvider.Item>?) {
        items.clear()
        if (t != null) {
            items += t;
        }
    }

    override fun getCards(): List<Card> {
        return items.map {
            Card(it.icon.let {
                if (it is VectorDrawable) it.tint(
                        ColorEngine.getInstance(context).getResolverCache(
                                ColorEngine.Resolvers.FEED_CHIP).value.computeForegroundColor()) else it
            }, it.title,
                    { _, _ -> View(context) }, Card.TEXT_ONLY or Card.RAISE, "",
                    (it.title?.hashCode() ?: UUID.randomUUID().hashCode())
                            shl 10 or (it.icon?.hashCode() ?: 0 and 0b1111111111)).apply {
                if (it.click != null || it.viewClickListener != null) {
                    globalClickListener = { v ->
                        if (it.viewClickListener != null) {
                            it.viewClickListener.accept(v)
                        }
                        if (it.click != null) {
                            it.click.run()
                        }
                    }
                }
            }
        }
    }
}
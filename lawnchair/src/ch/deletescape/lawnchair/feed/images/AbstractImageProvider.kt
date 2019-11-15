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
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.FeedProvider
import ch.deletescape.lawnchair.persistence.feedPrefs
import ch.deletescape.lawnchair.util.extensions.d

abstract class AbstractImageProvider<Id>(c: Context) : FeedProvider(c) {
    abstract val images: MutableMap<Bitmap, Id>
    abstract val headerCard: List<Card>?
    abstract val onRemoveListener: (id: Id) -> Unit

    override fun onFeedShown() {
    }

    override fun onFeedHidden() {
    }

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun isVolatile() = true

    override fun getCards(): List<Card> {
        return (if (!context.feedPrefs.hideImageOperatorCards) (headerCard
                ?: emptyList()) else emptyList()) + images.keys.map {
            d("getCards: $it")
            Card(null, "", { parent, _ ->
                ImageView(parent.context).apply {
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT)
                    setImageBitmap(it)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }, Card.RAISE or Card.NO_HEADER, "", it.hashCode()).apply {
                canHide = true
                onRemoveListener = {_ ->
                    d("onRemoveListener: removing image ${images[it]}")
                    onRemoveListener(images[it]!!)
                    images.remove(it)
                }
            }
        }
    }
}
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
import ch.deletescape.lawnchair.cp.OverlayCallbacks
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.inflate
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageProvider(c: Context) : AbstractImageProvider<String>(c) {
    override val images = mutableMapOf<Bitmap, String>()
    override val headerCard: Card? = Card(null, null, { parent, _ ->
        (parent as ViewGroup).inflate(R.layout.add_image).apply {
            setOnClickListener {
               OverlayCallbacks.postImageRequest(context) {
                   if (it != null) {
                       FeedScope.launch(Dispatchers.IO) {
                           ImageDatabase.getInstance(
                                   context).access()
                                   .insert(Image(it,
                                           "normal"))
                       }.invokeOnCompletion { _ ->
                           images += ImageStore.getInstance(context).getBitmap(it) to it
                           if (feed != null) {
                               feed.refresh(10, 0, true)
                           }
                       }
                   }
               }
            }
        }
    }, Card.RAISE or Card.NO_HEADER, "nosort, top",
                                          "manageNotes".hashCode())

    override fun isVolatile(): Boolean {
        return true
    }

    override val onRemoveListener: (id: String) -> Unit = {
            d("(id: String) -> Unit: removing image with id $it")
            ImageStore.getInstance(context).remove(it)
            FeedScope.launch {
                ImageDatabase.getInstance(context).access().remove(it)
            }
        }

    init {
        FeedScope.launch {
            ImageDatabase.getInstance(context).apply {
                access().getAll().forEach {
                    images += ImageStore.getInstance(context).getBitmap(it.id) to it.id
                }
            }
        }
    }
}
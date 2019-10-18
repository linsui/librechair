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
import android.view.View
import ch.deletescape.lawnchair.cp.OverlayCallbacks
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.feed.cam.CameraScreen
import ch.deletescape.lawnchair.fromDrawableRes
import ch.deletescape.lawnchair.fromStringRes
import ch.deletescape.lawnchair.getPostionOnScreen
import ch.deletescape.lawnchair.tint
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageProvider(c: Context) : AbstractImageProvider<String>(c) {
    override val images = mutableMapOf<Bitmap, String>()
    override val headerCard: List<Card>? = listOf(Card(R.drawable.ic_add.fromDrawableRes(context)
            .tint(FeedAdapter.getOverrideColor(context)),
            R.string.title_card_add_image.fromStringRes(context), { _, _ ->
        View(context)
    }, Card.RAISE or Card.TEXT_ONLY, "nosort, top",
            "manageNotes".hashCode()).apply {
        globalClickListener = {
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
    },
            Card(R.drawable.ic_camera_alt_black_24dp.fromDrawableRes(context).tint(
                    FeedAdapter.getOverrideColor(context)),
                    context.getString(R.string.title_card_take_image), { _, _ -> View(context) },
                    Card.RAISE or Card.TEXT_ONLY, "nosort, top",
                    "manageNotes".hashCode()).apply {
                globalClickListener = {
                    CameraScreen(it.context) {
                        if (it != null) {
                            FeedScope.launch(Dispatchers.IO) {
                                val id = ImageStore.getInstance(context)
                                        .storeBitmap(it)
                                ImageDatabase.getInstance(
                                        context).access()
                                        .insert(Image(id,
                                                "normal"))
                                images += ImageStore.getInstance(context).getBitmap(id) to id
                                if (feed != null) {
                                    feed.refresh(10, 0, true)
                                }
                            }
                        }
                    }.display(this@ImageProvider,
                            it.getPostionOnScreen().first + it.measuredWidth / 2,
                            it.getPostionOnScreen().second + it.measuredHeight / 2)
                }
            })

    override fun isVolatile(): Boolean {
        return true
    }

    override fun getCards(): List<Card> {
        d("getCards: images are $images")
        return super.getCards()
    }

    override
    val onRemoveListener: (id: String) -> Unit = {
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
                    images += ImageStore.getInstance(context).getBitmap(
                            it.id) to it.id
                }
            }
        }
    }
}
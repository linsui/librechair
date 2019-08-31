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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder
import android.view.ViewGroup
import ch.deletescape.lawnchair.IImageSelector
import ch.deletescape.lawnchair.feed.Card
import ch.deletescape.lawnchair.feed.IImageStoreCallback
import ch.deletescape.lawnchair.inflate
import ch.deletescape.lawnchair.runOnMainThread
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ImageProvider(c: Context?) : AbstractImageProvider<String>(c) {
    override val images = mutableMapOf<Bitmap, String>()
    override val headerCard: Card? = Card(null, null, { parent, _ ->
        (parent as ViewGroup).inflate(R.layout.add_image).apply {
            setOnClickListener {
                context.bindService(Intent(context, ImageSelectorService::class.java),
                                    object : ServiceConnection {
                                        override fun onServiceDisconnected(
                                                name: ComponentName) = {}()

                                        override fun onServiceConnected(name: ComponentName,
                                                                        service: IBinder) {
                                            IImageSelector.Stub.asInterface(service)
                                                    .selectImage(object :
                                                                         IImageStoreCallback.Stub() {
                                                        override fun onImageRetrieved(
                                                                id: String?) {
                                                            d("onImageRetrieved: retrieved image with uuid $id")
                                                            if (id != null) {
                                                                GlobalScope.launch {
                                                                    ImageDatabase.getInstance(
                                                                            context).access()
                                                                            .insert(Image(id,
                                                                                          "normal"))
                                                                }.invokeOnCompletion {
                                                                    runOnMainThread {
                                                                        images += ImageStore.getInstance(context).getBitmap(id) to id
                                                                        if (feed != null) {
                                                                            feed.refresh(0)
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    })
                                        }
                                    }, Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT);
            }
        }
    }, Card.RAISE or Card.NO_HEADER, "nosort, top",
                                          "manageNotes".hashCode())
    override val onRemoveListener: (id: String) -> Unit
        get() = {
            d("(id: String) -> Unit: removing image with id $it")
            ImageStore.getInstance(context).remove(it)
            GlobalScope.launch {
                ImageDatabase.getInstance(context).access().remove(it)
            }
        }

    init {
        GlobalScope.launch {
            ImageDatabase.getInstance(context).apply {
                access().getAll().forEach {
                    images += ImageStore.getInstance(context).getBitmap(it.id) to it.id
                }
            }
        }
    }
}
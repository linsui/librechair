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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ch.deletescape.lawnchair.util.LaunchpadActivity
import ch.deletescape.lawnchair.util.SingletonHolder
import ch.deletescape.lawnchair.util.extensions.d
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ImageStore private constructor(val context: Context) {
    private val fileDir = File(context.filesDir, FILE_DIR)
    val cache: MutableMap<String, Bitmap> = mutableMapOf()

    init {
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
    }

    fun storeBitmap(bitmap: Bitmap): String {
        val id = UUID.randomUUID()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                        FileOutputStream(File(fileDir, "$[$id].png")))
        cache += id.toString() to bitmap
        return id.toString()
    }

    fun getBitmap(id: String): Bitmap {
        return cache[id] ?: BitmapFactory.decodeFile(File(fileDir, "$[$id].png").path)
    }

    fun remove(id: String) {
        cache.remove(id)
        File(fileDir, "$[$id].png").delete()
    }

    companion object : SingletonHolder<ImageStore, Context>(::ImageStore) {
        const val FILE_DIR = "feed_image_store"
    }

    @SuppressLint("Registered")
    class ImageStoreActivity : LaunchpadActivity() {
        companion object {
            const val IMAGE_UUID =
                    "ch.deletescape.lawnchair.feed.images.ImageStore.ImageStoreActivity.IMAGE_UUID"
        }

        override fun getActivity(): Intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }

        override fun onResult(resultCode: Int, data: Intent?) {
            d("onResult: $resultCode, $data")
            if (data != null && data.data != null) {
                GlobalScope.launch {
                    val imageStream = contentResolver.openInputStream(data.data!!)
                    try {
                        setResult(Activity.RESULT_OK, Intent().putExtra(IMAGE_UUID,
                                                                        ImageStore.getInstance(
                                                                                this@ImageStoreActivity).storeBitmap(
                                                                                BitmapFactory.decodeStream(
                                                                                        imageStream)).also {
                                                                            d("onResult: image ID is $it")
                                                                        }))
                    } catch (e: IOException) {
                        e.printStackTrace()
                        setResult(Activity.RESULT_CANCELED)
                    }
                    finish()
                }
            } else {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }
}
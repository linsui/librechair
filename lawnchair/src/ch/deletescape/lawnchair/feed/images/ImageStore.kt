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
import android.graphics.BitmapFactory
import ch.deletescape.lawnchair.util.SingletonHolder
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ImageStore private constructor(val context: Context) {
    val fileDir = File(context.filesDir, FILE_DIR)
    val cache: MutableMap<String, Bitmap> = mutableMapOf()

    fun storeBitmap(bitmap: Bitmap): String {
        val id = UUID.randomUUID();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(File(fileDir, "$$$id$$.png")))
        cache += id.toString() to bitmap
        return id.toString()
    }

    fun getBitmap(id: String): Bitmap {
        return cache[id] ?: BitmapFactory.decodeFile(File(fileDir, "$$$id$$.png").path)
    }

    companion object : SingletonHolder<ImageStore, Context>(::ImageStore) {
        const val FILE_DIR = "feed_image_store"
    }
}
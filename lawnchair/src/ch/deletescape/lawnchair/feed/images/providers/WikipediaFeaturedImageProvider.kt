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

package ch.deletescape.lawnchair.feed.images.providers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import ch.deletescape.lawnchair.feed.wikipedia.image.DailyImage
import ch.deletescape.lawnchair.mainHandler
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit

class WikipediaFeaturedImageProvider(val context: Context) : ImageProvider {
    override val expiryTime: Long = TimeUnit.DAYS.toMillis(1)
    val cache: File
        get() = File(context.cacheDir,
                "wp_daily_cache_${TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())}.png")

    override suspend fun getBitmap(context: Context): Bitmap? = try {
        if (cache.exists().not()) BitmapFactory.decodeStream(
                URL(DailyImage.safeGetFeaturedImage()).openStream()).also {
            it.compress(Bitmap.CompressFormat.PNG, 70, FileOutputStream(cache))
        } else BitmapFactory.decodeFile(cache.absolutePath)
    } catch (e: IOException) {
        null
    }

    override fun registerOnChangeListener(listener: () -> Unit) {
        mainHandler.postAtTime(listener, SystemClock.uptimeMillis())
    }

}
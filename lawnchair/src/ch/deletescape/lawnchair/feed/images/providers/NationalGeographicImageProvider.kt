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
import android.os.Handler
import android.os.SystemClock
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.feed.images.ng.NationalGeographicRetrofitServiceFactory
import ch.deletescape.lawnchair.tomorrow
import kotlinx.coroutines.async
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class NationalGeographicImageProvider(val c: Context) : ImageProvider {
    override val expiryTime: Long
        get() = TimeUnit.DAYS.toMillis(1)

    val cache: File
        get() = File(c.cacheDir, "ng_epoch_${TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())}_.png")

    override suspend fun getBitmap(context: Context): Bitmap? = FeedScope.async {
        if (cache.exists()) {
            return@async BitmapFactory.decodeStream(FileInputStream(cache))
        } else {
            try {
                val response = NationalGeographicRetrofitServiceFactory.getApi(context)
                        .getPictureOfTheDay().execute()
                if (!response.isSuccessful || response.body() == null) {
                    return@async null
                } else {
                    return@async BitmapFactory
                            .decodeStream(URL(response.body()!!.items[0].image.originalUrl).openStream())
                            .also {
                                it.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(cache))
                            }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return@async null
            }
        }
    }.await()

    override fun registerOnChangeListener(listener: () -> Unit) {
        Handler(c.mainLooper).postAtTime(object : Function0<Unit> {
            override fun invoke() {
                listener()
                Handler(c.mainLooper).postAtTime(this, SystemClock.uptimeMillis() + tomorrow(
                        Date()).time - System.currentTimeMillis())
            }
        }, SystemClock.uptimeMillis() + tomorrow(
                Date()).time - System.currentTimeMillis())
    }
}
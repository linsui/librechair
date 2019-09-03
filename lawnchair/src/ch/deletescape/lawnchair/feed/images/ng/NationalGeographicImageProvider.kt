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

package ch.deletescape.lawnchair.feed.images.ng

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.SystemClock
import ch.deletescape.lawnchair.feed.images.providers.ImageProvider
import ch.deletescape.lawnchair.tomorrow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class NationalGeographicImageProvider(val c: Context) : ImageProvider {
    override val expiryTime: Long
        get() = TimeUnit.DAYS.toMillis(1)

    override suspend fun getBitmap(context: Context): Bitmap? = GlobalScope.async {
        try {
            val response =
                    NationalGeographicRetrofitServiceFactory.getApi(context).getPictureOfTheDay()
                            .execute()
            if (!response.isSuccessful || response.body() == null) {
                return@async null
            } else {
                return@async BitmapFactory
                        .decodeStream(URL(response.body()!!.items[0].originalUrl).openStream())
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return@async null
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
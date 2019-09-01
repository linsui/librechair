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

package ch.deletescape.lawnchair.feed.images.providers

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BitmapCache {
    private val cache: MutableMap<String, Pair<Long, Bitmap>> = mutableMapOf()

    suspend fun getBitmap(provider: ImageProvider, c: Context): Bitmap = withContext(
            Dispatchers.Default) {
        synchronized(this@BitmapCache) {
            if (System.currentTimeMillis() - (cache[provider::class.qualifiedName]?.first
                                              ?: 0) >= provider.expiryTime) {
                provider.getBitmap(c).also {
                    cache[provider::class.qualifiedName!!] = System.currentTimeMillis() to it
                }
            } else {
                cache[provider::class.qualifiedName]!!.second
            }
        }
    }
}
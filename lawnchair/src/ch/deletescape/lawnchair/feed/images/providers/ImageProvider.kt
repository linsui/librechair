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
import ch.deletescape.lawnchair.util.extensions.d
import kotlin.reflect.KClass

interface ImageProvider {
    val expiryTime: Long
    suspend fun getBitmap(context: Context): Bitmap?
    suspend fun getDescription(context: Context): String? = null
    suspend fun getUrl(context: Context): String? = null
    fun registerOnChangeListener(listener: () -> Unit)
    fun attachMeta(meta: Map<String, String>) {

    }

    companion object {
        fun inflate(clazz: KClass<out ImageProvider>,
                    meta: Map<String, String>, c: Context): ImageProvider? {
            d("inflate: class constructors ${clazz.constructors}")
            if (clazz.constructors.isNotEmpty()) {
                return clazz.constructors.toList()[0].call(c).also { it.attachMeta(meta) }
            } else {
                return null
            }
        }
    }
}

data class ImageProviderContainer(val clazz: KClass<out ImageProvider>,
                                  val meta: Map<String, String>)

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

package ch.deletescape.lawnchair.clipart

import android.content.Context
import android.graphics.drawable.Drawable
import org.jetbrains.annotations.Contract

object ClipartCache {
    val cache: MutableMap<String, Drawable> = mutableMapOf()
    val providers: MutableList<ClipartResolver> =
            object : ArrayList<ClipartResolver>(), List<ClipartResolver> {
                override fun add(element: ClipartResolver): Boolean {
                    return super.add(element).also {
                        all.addAll(element.allClipart)
                    }
                }

                override fun remove(element: ClipartResolver): Boolean {
                    error("remove: unsupported action")
                }
            }
    val all: MutableList<ClipartResolver.ClipartData> = mutableListOf()
    @Contract("null -> null")
    fun resolveDrawable(key: String?): Drawable? {
        if (key == null) {
            return null;
        }
        return cache[key]
               ?: all.find { it.token == key }?.resolveClipart()?.also { cache += key to it }
    }

    fun initialize(c: Context) {
    }
}
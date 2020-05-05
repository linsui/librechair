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

import com.google.gson.annotations.SerializedName

data class GalleryItem(val image: GalleryImage)

data class GalleryImage(val title: String, val caption: String, val credit: String,
                       val uuid: String,
                       val profileUrl: String, @SerializedName("uri")
                       val originalUrl: String, val altText: String,
                       @SerializedName("full-path-url") val fullPathUrl: String,
                       val aspectRatio: Double, val sizes: Map<String, String>)
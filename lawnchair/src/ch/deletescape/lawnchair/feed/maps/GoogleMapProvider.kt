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
 *     along with Librechair.  If not, see <httpss://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.maps

import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.MapTileIndex

open class GoogleMapProvider : MapProvider() {
    override fun getTileSource(): ITileSource {
        return object : XYTileSource("Google-Roads",
                0, 19, 256, ".png", arrayOf("https://mt0.google.com", "https://mt1.google.com",
                "https://mt2.google.com", "https://mt3.google.com")) {
            override fun getTileURLString(tileLoc: Long): String {
                return "$baseUrl/vt/lyrs=m&x=" + MapTileIndex.getX(
                        tileLoc) + "&y=" + MapTileIndex.getY(
                        tileLoc) + "&z=" + MapTileIndex.getZoom(tileLoc)
            }
        }
    }

    class China : GoogleMapProvider() {
        override fun getTileSource(): ITileSource {
            return object : XYTileSource("Google-Roads-CnGCJ",
                    0, 19, 256, ".png", arrayOf("https://mt0.google.cn", "https://mt1.google.cn",
                    "https://mt2.google.cn", "https://mt3.google.cn")) {
                override fun getTileURLString(tileLoc: Long): String {
                    val (lat, lon) = WgsGcjConverter.num2deg(tileLoc.x, tileLoc.y, tileLoc.zoom)
                    val gcj = WgsGcjConverter.wgs84ToGcj02(lat, lon)
                    val (x, y) = WgsGcjConverter.deg2num(gcj.latOrY, gcj.lonOrX, tileLoc.zoom)
                    return "$baseUrl/vt/lyrs=m&x=$x&y=$y&z=${tileLoc.zoom}"
                }
            }
        }
    }
}

val Long.x get() = MapTileIndex.getX(this)
val Long.y get() = MapTileIndex.getY(this)
val Long.zoom get() = MapTileIndex.getZoom(this)

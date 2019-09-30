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

package ch.deletescape.lawnchair.feed.maps;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.MapTileIndex;

public class GoogleMapProvider extends MapProvider {
    @Override
    public ITileSource getTileSource() {
        return new XYTileSource("Google-Roads",
                0, 19, 256, ".png", new String[]{
                "https://mt0.google.com",
                "https://mt1.google.com",
                "https://mt2.google.com",
                "https://mt3.google.com",

        }, "Greedy Goolag capitalists") {
            @Override
            public String getTileURLString(long tileLoc) {
                return getBaseUrl() + "/vt/lyrs=m&x=" + MapTileIndex.getX(
                        tileLoc) + "&y=" + MapTileIndex.getY(
                        tileLoc) + "&z=" + MapTileIndex.getZoom(tileLoc);
            }
        };
    }

    public static class China extends GoogleMapProvider {
        @Override
        public ITileSource getTileSource() {
            return new XYTileSource("Google-Roads-Cn",
                    0, 19, 256, ".png", new String[]{
                    "https://mt0.google.cn",
                    "https://mt1.google.cn",
                    "https://mt2.google.cn",
                    "https://mt3.google.cn",

            }, "Greedy Goolag capitalists") {
                @Override
                public String getTileURLString(long tileLoc) {
                    return getBaseUrl() + "/vt/lyrs=m&x=" + MapTileIndex.getX(
                            tileLoc) + "&y=" + MapTileIndex.getY(
                            tileLoc) + "&z=" + MapTileIndex.getZoom(tileLoc);
                }
            };
        }
    }
}

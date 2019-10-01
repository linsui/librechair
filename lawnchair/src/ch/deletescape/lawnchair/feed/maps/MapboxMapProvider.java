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

package ch.deletescape.lawnchair.feed.maps;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.MapTileIndex;

public class MapboxMapProvider extends MapProvider {
    public static final String API_KEY = "pk.eyJ1IjoiZ25vbWUtbWFwcyIsImEiOiJjaXF3a3lwbXkwMDJwaTBubmZlaGk4cDZ6In0.8aukTfgjzeqATA8eNItPJA";
    // NOTE: this API key was taken from GNOME Maps.

    public ITileSource getTileSource() {
        return new XYTileSource("Mapbox", 1, 19, 256, ".png",
                new String[]{"https://api.mapbox.com/styles/v1/mapbox/streets-v11/tiles/%d/%d/%d?access_token=%s"}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return String.format(getBaseUrl(), MapTileIndex.getZoom(pMapTileIndex),
                        MapTileIndex.getX(pMapTileIndex),
                        MapTileIndex.getY(pMapTileIndex),
                        API_KEY);
            }
        };
    }
}

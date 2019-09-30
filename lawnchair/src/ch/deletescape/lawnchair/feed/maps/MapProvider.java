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

import com.android.launcher3.R;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapProvider {
    public static final List<Class<? extends MapProvider>> ALL_PROVIDERS = Arrays.asList(
            MapProvider.class, GoogleMapProvider.class, GoogleMapProvider.China.class, BingMapsProvider.class);
    public static final Map<Class<? extends MapProvider>, Integer> NAMES = new HashMap<>();

    static {
        NAMES.put(MapProvider.class, R.string.title_map_provider_default);
        NAMES.put(GoogleMapProvider.class, R.string.title_map_provider_google);
        NAMES.put(GoogleMapProvider.China.class, R.string.title_map_provider_google_china);
        NAMES.put(BingMapsProvider.class, R.string.bing);
    }

    public ITileSource getTileSource() {
        return TileSourceFactory.MAPNIK;
    }

    public static MapProvider inflate(Class<? extends MapProvider> clazz)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return clazz.getConstructor().newInstance();
    }
}

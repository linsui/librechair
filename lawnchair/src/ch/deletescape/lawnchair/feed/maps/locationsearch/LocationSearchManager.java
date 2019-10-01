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

package ch.deletescape.lawnchair.feed.maps.locationsearch;

import android.content.Context;
import android.util.Pair;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import ch.deletescape.lawnchair.feed.maps.locationsearch.mapbox.MapboxFactory;
import ch.deletescape.lawnchair.feed.maps.locationsearch.mapbox.MapboxResult;
import ch.deletescape.lawnchair.nominatim.NominatimFactory;
import ch.deletescape.lawnchair.nominatim.NominatimResponse;
import retrofit2.Response;

public final class LocationSearchManager {
    private final Context context;
    private static LocationSearchManager sInstance;

    public synchronized static LocationSearchManager getInstance(Context context) {
        return sInstance != null ? sInstance : (sInstance = new LocationSearchManager(context));
    }

    private LocationSearchManager(Context context) {
        this.context = context;
    }

    @Nullable
    public Pair<Double, Double> get(String query) {
        Response<MapboxResult> resultResponse;
        Response<NominatimResponse[]> nominatimResponseResponse;
        try {
            if ((resultResponse = MapboxFactory.getInstance(context).getLocation(
                    query).execute()).body() != null && resultResponse.isSuccessful() && resultResponse.body().features.length >= 1) {
                return new Pair<>(resultResponse.body().features[0].center[0],
                        resultResponse.body().features[0].center[1]);
            } else if ((nominatimResponseResponse = NominatimFactory.getInstance(context).query(
                    query).execute()).isSuccessful() && nominatimResponseResponse.body() != null && nominatimResponseResponse.body()
                    .length > 0) {
                return new Pair<>(nominatimResponseResponse.body()[0].lat,
                        nominatimResponseResponse.body()[0].lon);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

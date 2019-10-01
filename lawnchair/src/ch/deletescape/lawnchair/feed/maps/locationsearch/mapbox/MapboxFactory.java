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

package ch.deletescape.lawnchair.feed.maps.locationsearch.mapbox;

import android.content.Context;

import ch.deletescape.lawnchair.feed.maps.MapboxMapProvider;
import ch.deletescape.lawnchair.util.okhttp.OkHttpClientBuilder;
import kotlin.Pair;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapboxFactory {
    private static MapboxApi sMapboxInstance;

    public static synchronized MapboxApi getInstance(Context context) {
        return sMapboxInstance != null ? sMapboxInstance : (sMapboxInstance = new Retrofit.Builder().baseUrl(
                "https://api.mapbox.com").addConverterFactory(
                GsonConverterFactory.create()).client(
                new OkHttpClientBuilder().addQueryParam(new Pair<>("access_token",
                        MapboxMapProvider.API_KEY)).build(context)).build().create(
                MapboxApi.class));
    }
}

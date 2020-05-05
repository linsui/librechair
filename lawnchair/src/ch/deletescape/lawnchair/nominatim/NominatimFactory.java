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

package ch.deletescape.lawnchair.nominatim;

import android.content.Context;

import ch.deletescape.lawnchair.util.okhttp.OkHttpClientBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NominatimFactory {
    private NominatimFactory() {
        throw new RuntimeException("this class cannot be instantiated");
    }

    private static NominatimService sInstance;

    public static synchronized NominatimService getInstance(Context c) {
        return sInstance != null ? sInstance : (sInstance = new Retrofit.Builder().baseUrl(
                "https://nominatim.openstreetmap.org").addConverterFactory(
                GsonConverterFactory.create()).client(
                new OkHttpClientBuilder().build(c)).build().create(NominatimService.class));
    }
}

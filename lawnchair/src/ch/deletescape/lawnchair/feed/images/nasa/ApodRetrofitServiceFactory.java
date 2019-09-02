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

package ch.deletescape.lawnchair.feed.images.nasa;

import android.content.Context;
import ch.deletescape.lawnchair.util.okhttp.OkHttpClientBuilder;
import kotlin.Pair;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApodRetrofitServiceFactory {

    private static final String BASE_URL = "https://api.nasa.gov/";
    private static ApodApi sApiInstance;

    public static ApodApi manufacture(Context context) {
        if (sApiInstance == null) {
            return sApiInstance = new Retrofit.Builder().baseUrl(BASE_URL).client(
                    new OkHttpClientBuilder().addQueryParam(new Pair<>("api_key", Apod.API_KEY))
                            .build(context)).addConverterFactory(
                    GsonConverterFactory.create()).build().create(ApodApi.class);
        } else {
            return sApiInstance;
        }
    }
}

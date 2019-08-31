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

package ch.deletescape.lawnchair.feed.images.bing;

import android.content.Context;
import ch.deletescape.lawnchair.util.okhttp.OkHttpClientBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BingRetrofitServiceFactory {

    public final static BingRetrofitServiceFactory INSTANCE;
    public final static String BASE_URL = "https://bing.biturl.top/";
    private static BingApi sApiInstance;

    static {
        INSTANCE = new BingRetrofitServiceFactory();
    }

    private BingRetrofitServiceFactory() {
    }

    public synchronized BingApi getApi(Context c) {
        if (sApiInstance == null) {
            return sApiInstance = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
                    GsonConverterFactory.create()).client(new OkHttpClientBuilder().build(c))
                    .build().create(BingApi.class);
        } else {
            return sApiInstance;
        }
    }
}

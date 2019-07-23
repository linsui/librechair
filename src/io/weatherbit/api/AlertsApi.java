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

package io.weatherbit.api;

import io.weatherbase.api.model.WeatherAlert;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AlertsApi {
  /**
   * Returns severe weather alerts issued by meteorological agencies - Given a lat/lon.
   * Returns severe weather alerts issued by meteorological agencies - given a lat, and a lon.
   * @param lat Latitude component of location. (required)
   * @param lon Longitude component of location. (required)
   * @param key Your registered API key. (required)
   * @param paramCallback Wraps return in jsonp callback - Example - callback&#x3D;func (optional)
   * @return Call&lt;WeatherAlert&gt;
   */
  @GET("alerts?lat={lat}&lon={lon}")
  Call<WeatherAlert> alertslatlatlonlonGet(

          @Path("lat") Double lat,
          @Path("lon") Double lon, @Query("key") String key
          , @Query("callback") String paramCallback

  );

}

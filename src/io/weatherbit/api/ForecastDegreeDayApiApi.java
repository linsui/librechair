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

import io.weatherbase.api.model.EnergyObsGroupForecast;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ForecastDegreeDayApiApi {
  /**
   * Returns Energy Forecast API response  - Given a single lat/lon. 
   * Retrieve an 8 day forecast relevant to te Energy Sector (degree days, solar radiation, precipitation, wind).
   * @param lat Latitude component of location. (required)
   * @param lon Longitude component of location. (required)
   * @param key Your registered API key. (required)
   * @param threshold Temperature threshold to use to calculate degree days (default 18 C)  (optional)
   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param tp Time period (default: daily) (optional)
   * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
   * @return Call&lt;EnergyObsGroupForecast&gt;
   */
  @GET("forecast/energy?lat={lat}&lon={lon}")
  Call<EnergyObsGroupForecast> forecastEnergylatlatlonlonGet(

          @Path("lat") Double lat,
          @Path("lon") Double lon, @Query("key") String key
          , @Query("threshold") Double threshold
          , @Query("units") String units
          , @Query("tp") String tp
          , @Query("callback") String paramCallback

  );

}

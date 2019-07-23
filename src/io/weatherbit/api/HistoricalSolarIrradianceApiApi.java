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

import io.weatherbase.api.model.EnergyObsGroup;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HistoricalSolarIrradianceApiApi {
  /**
   * Returns multiple locations given a bounding box. 
   * Returns aggregate energy specific historical weather fields, over a specified time period. Supply a bounding box ex: lat1&#x3D;40&amp;lon1&#x3D;-78&amp;lat2&#x3D;38&amp;lon2&#x3D;-80. This API will return UP TO 150 stations, aggregated by the specified time period start_date to end_date. 
   * @param lat1 Latitude of upper left corner. (required)
   * @param lon1 Longitude of upper left corner. (required)
   * @param lat2 Latitude of lower right corner. (required)
   * @param lon2 Longitude of lower right corner. (required)
   * @param startDate Start Date (YYYY-MM-DD or YYYY-MM-DD:HH). (required)
   * @param endDate End Date (YYYY-MM-DD or YYYY-MM-DD:HH). (required)
   * @param key Your registered API key. (required)
   * @param threshold Temperature threshold to use to calculate degree days (default 18 C)  (optional)
   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
   * @return Call&lt;EnergyObsGroup&gt;
   */
  @GET("history/energy/bbox?lat1={lat1}&lon1={lon1}&lat2={lat2}&lon2={lon2}")
  Call<EnergyObsGroup> historyEnergyBboxlat1lat1lon1lon1lat2lat2lon2lon2Get(

          @Path("lat1") Double lat1,
          @Path("lon1") Double lon1,
          @Path("lat2") Double lat2,
          @Path("lon2") Double lon2, @Query("start_date") String startDate
          , @Query("end_date") String endDate
          , @Query("key") String key
          , @Query("threshold") Double threshold
          , @Query("units") String units
          , @Query("callback") String paramCallback

  );

  /**
   * Returns Energy API response  - Given a single lat/lon. 
   * Returns aggregate energy specific historical weather fields, over a specified time period.
   * @param lat Latitude component of location. (required)
   * @param lon Longitude component of location. (required)
   * @param startDate Start Date (YYYY-MM-DD or YYYY-MM-DD:HH). (required)
   * @param endDate End Date (YYYY-MM-DD or YYYY-MM-DD:HH). (required)
   * @param key Your registered API key. (required)
   * @param tp Time period to aggregate by (daily, monthly) (optional)
   * @param threshold Temperature threshold to use to calculate degree days (default 18 C)  (optional)
   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
   * @return Call&lt;EnergyObsGroup&gt;
   */
  @GET("history/energy?lat={lat}&lon={lon}")
  Call<EnergyObsGroup> historyEnergylatlatlonlonGet(

          @Path("lat") Double lat,
          @Path("lon") Double lon, @Query("start_date") String startDate
          , @Query("end_date") String endDate
          , @Query("key") String key
          , @Query("tp") String tp
          , @Query("threshold") Double threshold
          , @Query("units") String units
          , @Query("callback") String paramCallback

  );

}

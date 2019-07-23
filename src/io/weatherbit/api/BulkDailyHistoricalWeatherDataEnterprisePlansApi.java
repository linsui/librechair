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

import io.weatherbase.api.model.HistoryDay;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BulkDailyHistoricalWeatherDataEnterprisePlansApi {
  /**
   * [Enterprise Plan Only] Returns Historical Observations - Given a City ID
   * Returns Historical Observations - Given a City ID.
   * @param cityId City ID. Example: 4487042 (required)
   * @param startDate Start Date (YYYY-MM-DD or YYYY-MM-DD:HH) (required)
   * @param endDate End Date (YYYY-MM-DD or YYYY-MM-DD:HH) (required)
   * @param key Your registered API key. (required)
   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
   * @return Call&lt;HistoryDay&gt;
   */
  @GET("bulk/history/daily?city_id={city_id}")
  Call<HistoryDay> bulkHistoryDailycityIdcityIdGet(

          @Path("city_id") String cityId, @Query("start_date") String startDate
          , @Query("end_date") String endDate
          , @Query("key") String key
          , @Query("units") String units
          , @Query("lang") String lang
          , @Query("callback") String paramCallback

  );

  /**
   * [Enterprise Plan Only] Returns Historical Observations - Given City and/or State, Country.
   * Returns Historical Observations - Given a city in the format of City,ST or City. The state, and country parameters can be provided to make the search more accurate.
   * @param city City search.. Example - &amp;city&#x3D;Raleigh,NC or &amp;city&#x3D;Berlin,DE or city&#x3D;Paris&amp;country&#x3D;FR (required)
   * @param country Country Code (2 letter). (required)
   * @param startDate Start Date (YYYY-MM-DD or YYYY-MM-DD:HH). (required)
   * @param endDate End Date (YYYY-MM-DD or YYYY-MM-DD:HH). (required)
   * @param key Your registered API key. (required)
   * @param state Full name of state. (optional)
   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
   * @return Call&lt;HistoryDay&gt;
   */
  @GET("bulk/history/daily?city={city}&country={country}")
  Call<HistoryDay> bulkHistoryDailycitycitycountrycountryGet(

          @Path("city") String city,
          @Path("country") String country, @Query("start_date") String startDate
          , @Query("end_date") String endDate
          , @Query("key") String key
          , @Query("state") String state
          , @Query("units") String units
          , @Query("lang") String lang
          , @Query("callback") String paramCallback

  );

  /**
   * [Enterprise Plan Only] Returns Historical Observations - Given IP Address, or auto.
   * Returns Historical Observations - Given IP Address, or auto.
   * @param ip IP Address, or auto. Example: ip&#x3D;auto (required)
   * @param startDate Start Date (YYYY-MM-DD or YYYY-MM-DD:HH). (required)
   * @param endDate End Date (YYYY-MM-DD or YYYY-MM-DD:HH). (required)
   * @param key Your registered API key. (required)
   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
   * @return Call&lt;HistoryDay&gt;
   */
  @GET("bulk/history/daily?ip={ip}")
  Call<HistoryDay> bulkHistoryDailyipipGet(

          @Path("ip") String ip, @Query("start_date") String startDate
          , @Query("end_date") String endDate
          , @Query("key") String key
          , @Query("units") String units
          , @Query("lang") String lang
          , @Query("callback") String paramCallback

  );

  /**
   * [Enterprise Plan Only] Returns Historical Observations - Given a lat/lon.
   * Returns Historical Observations - Given a lat, and lon.
   * @param lat Latitude component of location. (required)
   * @param lon Longitude component of location. (required)
   * @param startDate Start Date (YYYY-MM-DD or YYYY-MM-DD:HH). (required)
   * @param endDate End Date (YYYY-MM-DD or YYYY-MM-DD:HH). (required)
   * @param key Your registered API key. (required)
   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
   * @return Call&lt;HistoryDay&gt;
   */
  @GET("bulk/history/daily?lat={lat}&lon={lon}")
  Call<HistoryDay> bulkHistoryDailylatlatlonlonGet(

          @Path("lat") Double lat,
          @Path("lon") Double lon, @Query("start_date") String startDate
          , @Query("end_date") String endDate
          , @Query("key") String key
          , @Query("units") String units
          , @Query("lang") String lang
          , @Query("callback") String paramCallback

  );

  /**
   * [Enterprise Plan Only] Returns Historical Observations - Given a Postal Code
   * Returns Historical Observations - Given a Postal Code.
   * @param postalCode Postal Code. Example: 28546 (required)
   * @param startDate Start Date (YYYY-MM-DD or YYYY-MM-DD:HH) (required)
   * @param endDate End Date (YYYY-MM-DD or YYYY-MM-DD:HH) (required)
   * @param key Your registered API key. (required)
   * @param country Country Code (2 letter). (optional)
   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
   * @return Call&lt;HistoryDay&gt;
   */
  @GET("bulk/history/daily?postal_code={postal_code}")
  Call<HistoryDay> bulkHistoryDailypostalCodepostalCodeGet(

          @Path("postal_code") String postalCode, @Query("start_date") String startDate
          , @Query("end_date") String endDate
          , @Query("key") String key
          , @Query("country") String country
          , @Query("units") String units
          , @Query("lang") String lang
          , @Query("callback") String paramCallback

  );

  /**
   * [Enterprise Plan Only] Returns Historical Observations - Given a station ID.
   * Returns Historical Observations - Given a station ID.
   * @param station Station ID. (required)
   * @param startDate Start Date (YYYY-MM-DD or YYYY-MM-DD:HH). (required)
   * @param endDate End Date (YYYY-MM-DD or YYYY-MM-DD:HH). (required)
   * @param key Your registered API key. (required)
   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
   * @return Call&lt;HistoryDay&gt;
   */
  @GET("bulk/history/daily?station={station}")
  Call<HistoryDay> bulkHistoryDailystationstationGet(

          @Path("station") String station, @Query("start_date") String startDate
          , @Query("end_date") String endDate
          , @Query("key") String key
          , @Query("units") String units
          , @Query("lang") String lang
          , @Query("callback") String paramCallback

  );

}

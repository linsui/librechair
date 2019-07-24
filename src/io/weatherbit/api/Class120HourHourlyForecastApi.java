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

import io.weatherbase.api.model.ForecastHourly;
import java.math.BigDecimal;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Class120HourHourlyForecastApi {
  /**
   * Returns a 120 hour (hourly) forecast - Given a City ID.
   *  Returns an hourly forecast, where each point represents a one hour   period. Every point has a datetime string in the format \&quot;YYYY-MM-DD:HH\&quot;. Time is UTC.  
   * @param cityId City ID. Example: 4487042 (required)

   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example - callback&#x3D;func (optional)
   * @param hours Number of hours to return. (optional)
   * @return Call&lt;ForecastHourly&gt;
   */
  @GET("forecast/hourly")
  Call<ForecastHourly> forecastHourlycityIdcityIdGet(

          @Query("city_id") Integer cityId
          , @Query("units") String units
          , @Query("lang") String lang
          , @Query("callback") String paramCallback
          , @Query("hours") Integer hours

  );

  /**
   * Returns an 120 hour (hourly forecast) - Given City and/or State, Country.
   *  Returns an hourly forecast, where each point represents a one hour   period. Every point has a datetime string in the format \&quot;YYYY-MM-DD:HH\&quot;. Time is UTC. Accepts a city in the format of City,ST or City. The state, and country parameters can be provided to make the search more accurate. 
   * @param city City search.. Example - &amp;city&#x3D;Raleigh,NC or &amp;city&#x3D;Berlin,DE or city&#x3D;Paris&amp;country&#x3D;FR (required)
   * @param country Country Code (2 letter). (required)

   * @param state Full name of state. (optional)
   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
   * @param hours Number of hours to return. (optional)
   * @return Call&lt;ForecastHourly&gt;
   */
  @GET("forecast/hourly")
  Call<ForecastHourly> forecastHourlycitycitycountrycountryGet(

          @Query("city") String city,
          @Query("country") String country
          , @Query("state") String state
          , @Query("units") String units
          , @Query("lang") String lang
          , @Query("callback") String paramCallback
          , @Query("hours") Integer hours

  );

  /**
   * Returns a hourly forecast - Given an IP Address, or ip&#x3D;auto for automatic IP lookup.
   *  Returns an hourly forecast, where each point represents a one hour period. Every point has a datetime string in the format \&quot;YYYY-MM-DD:HH\&quot;. Time is UTC.  
   * @param ip IP address, or auto. Example: ip&#x3D;auto (required)

   * @param days Number of days to return. Default 16. (optional)
   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
   * @param hours Number of hours to return. (optional)
   * @return Call&lt;ForecastHourly&gt;
   */
  @GET("forecast/hourly?ip={ip}")
  Call<ForecastHourly> forecastHourlyipipGet(

          @Query("ip") String ip
          , @Query("days") BigDecimal days
          , @Query("units") String units
          , @Query("lang") String lang
          , @Query("callback") String paramCallback
          , @Query("hours") Integer hours

  );

  /**
   * Returns 120 hour (hourly) forecast - Given a lat/lon.
   * Returns an hourly forecast, where each point represents a one hour period. Every point has a datetime string in the format \&quot;YYYY-MM-DD:HH\&quot;. Time is UTC.  
   * @param lat Latitude component of location. (required)
   * @param lon Longitude component of location. (required)

   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example - callback&#x3D;func (optional)
   * @param hours Number of hours to return. (optional)
   * @return Call&lt;ForecastHourly&gt;
   */
  @GET("forecast/hourly")
  Call<ForecastHourly> forecastHourlylatlatlonlonGet(

          @Query("lat") Double lat,
          @Query("lon") Double lon
          , @Query("units") String units
          , @Query("lang") String lang
          , @Query("callback") String paramCallback
          , @Query("hours") Integer hours

  );

  /**
   * Returns a 120 hour (hourly) forecast - Given a Postal Code.
   *  Returns an hourly forecast, where each point represents a one hour   period. Every point has a datetime string in the format \&quot;YYYY-MM-DD:HH\&quot;. Time is UTC.  
   * @param postalCode Postal Code. Example: 28546 (required)

   * @param country Country Code (2 letter). (optional)
   * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
   * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27; href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
   * @param paramCallback Wraps return in jsonp callback. Example - callback&#x3D;func (optional)
   * @param hours Number of hours to return. (optional)
   * @return Call&lt;ForecastHourly&gt;
   */
  @GET("forecast/hourly")
  Call<ForecastHourly> forecastHourlypostalCodepostalCodeGet(

          @Query("postal_code") Integer postalCode
          , @Query("country") String country
          , @Query("units") String units
          , @Query("lang") String lang
          , @Query("callback") String paramCallback
          , @Query("hours") Integer hours

  );

}

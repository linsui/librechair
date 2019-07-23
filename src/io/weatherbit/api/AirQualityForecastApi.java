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

import io.weatherbase.api.model.AQHourly;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AirQualityForecastApi {
  /**
   * Returns 72 hour (hourly) Air Quality forecast - Given a City ID.
   * Returns 72 hour (hourly) Air Quality forecast, where each point represents a one hour period.
   * @param cityId City ID. Example: 4487042 (required)
   * @param key Your registered API key. (required)
   * @param paramCallback Wraps return in jsonp callback. Example - callback&#x3D;func (optional)
   * @param hours Number of hours to return. (optional)
   * @return Call&lt;AQHourly&gt;
   */
  @GET("forecast/airquality?city_id={city_id}")
  Call<AQHourly> forecastAirqualitycityIdcityIdGet(

          @Path("city_id") Integer cityId, @Query("key") String key
          , @Query("callback") String paramCallback
          , @Query("hours") Integer hours

  );

  /**
   * Returns 72 hour (hourly) Air Quality forecast - Given City and/or State, Country.
   * Returns 72 hour (hourly) Air Quality forecast, where each point represents a one hour period.
   * @param city City search.. Example - &amp;city&#x3D;Raleigh,NC or &amp;city&#x3D;Berlin,DE or city&#x3D;Paris&amp;country&#x3D;FR (required)
   * @param country Country Code (2 letter). (required)
   * @param key Your registered API key. (required)
   * @param state Full name of state. (optional)
   * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
   * @param hours Number of hours to return. (optional)
   * @return Call&lt;AQHourly&gt;
   */
  @GET("forecast/airquality?city={city}&country={country}")
  Call<AQHourly> forecastAirqualitycitycitycountrycountryGet(

          @Path("city") String city,
          @Path("country") String country, @Query("key") String key
          , @Query("state") String state
          , @Query("callback") String paramCallback
          , @Query("hours") Integer hours

  );

  /**
   * Returns 72 hour (hourly) Air Quality forecast - Given a lat/lon.
   * Returns 72 hour (hourly) Air Quality forecast, where each point represents a one hour period.
   * @param lat Latitude component of location. (required)
   * @param lon Longitude component of location. (required)
   * @param key Your registered API key. (required)
   * @param paramCallback Wraps return in jsonp callback. Example - callback&#x3D;func (optional)
   * @param hours Number of hours to return. (optional)
   * @return Call&lt;AQHourly&gt;
   */
  @GET("forecast/airquality?lat={lat}&lon={lon}")
  Call<AQHourly> forecastAirqualitylatlatlonlonGet(

          @Path("lat") Double lat,
          @Path("lon") Double lon, @Query("key") String key
          , @Query("callback") String paramCallback
          , @Query("hours") Integer hours

  );

  /**
   * Returns 72 hour (hourly) Air Quality forecast - Given a Postal Code.
   * Returns 72 hour (hourly) Air Quality forecast, where each point represents a one hour period.
   * @param postalCode Postal Code. Example: 28546 (required)
   * @param key Your registered API key. (required)
   * @param country Country Code (2 letter). (optional)
   * @param paramCallback Wraps return in jsonp callback. Example - callback&#x3D;func (optional)
   * @param hours Number of hours to return. (optional)
   * @return Call&lt;AQHourly&gt;
   */
  @GET("forecast/airquality?postal_code={postal_code}")
  Call<AQHourly> forecastAirqualitypostalCodepostalCodeGet(

          @Path("postal_code") Integer postalCode, @Query("key") String key
          , @Query("country") String country
          , @Query("callback") String paramCallback
          , @Query("hours") Integer hours

  );

}

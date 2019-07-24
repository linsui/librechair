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
import retrofit2.http.Query;

public interface Class5Day3HourForecastApi {

    /**
     * Returns a 3-hourly forecast - Given a City ID. Returns a 3-hourly forecast, where each point
     * represents a three hour   period. Every point has a datetime string in the format
     * \&quot;YYYY-MM-DD:HH\&quot;. Time is UTC.
     *
     * @param cityId City ID. Example: 4487042 (required)
     
     * @param days Number of days to return. Default 5. (optional)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
     * @return Call&lt;ForecastHourly&gt;
     */
    @GET("forecast/3hourly")
    Call<ForecastHourly> forecast3hourlycityIdcityIdGet(

            @Query("city_id") Integer cityId
            , @Query("days") BigDecimal days
            , @Query("units") String units
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

    /**
     * Returns a 3-hourly forecast - Given City and/or State, Country. Returns a 3-hourly forecast,
     * where each point represents a three hour   period. Every point has a datetime string in the
     * format \&quot;YYYY-MM-DD:HH\&quot;. Time is UTC.
     *
     * @param city City search.. Example - &amp;city&#x3D;Raleigh,NC or &amp;city&#x3D;Berlin,DE or
     * city&#x3D;Paris&amp;country&#x3D;FR (required)
     * @param country Country Code (2 letter). (required)
     
     * @param state Full name of state. (optional)
     * @param days Number of days to return. Default 5. (optional)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback. Example - callback&#x3D;func (optional)
     * @return Call&lt;ForecastHourly&gt;
     */
    @GET("forecast/3hourly")
    Call<ForecastHourly> forecast3hourlycitycitycountrycountryGet(

            @Query("city") String city,
            @Query("country") String country
            , @Query("state") String state
            , @Query("days") BigDecimal days
            , @Query("units") String units
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

    /**
     * Returns a 3hourly forecast - Given an IP Address, or ip&#x3D;auto for automatic IP lookup.
     * Returns a 3-hourly forecast, where each point represents a three hour   period. Every point
     * has a datetime string in the format \&quot;YYYY-MM-DD:HH\&quot;. Time is UTC.
     *
     * @param ip IP address, or auto. Example: ip&#x3D;auto (required)
     
     * @param days Number of days to return. Default 16. (optional)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
     * @return Call&lt;ForecastHourly&gt;
     */
    @GET("forecast/3hourly")
    Call<ForecastHourly> forecast3hourlyipipGet(

            @Query("ip") String ip
            , @Query("days") BigDecimal days
            , @Query("units") String units
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

    /**
     * Returns a 3-hourly forecast - Given a lat/lon. Returns a 3-hourly forecast, where each point
     * represents a three hour   period. Every point has a datetime string in the format
     * \&quot;YYYY-MM-DD:HH\&quot;. Time is UTC.
     *
     * @param lat Latitude component of location. (required)
     * @param lon Longitude component of location. (required)
     
     * @param days Number of days to return. Default 5. (optional)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback. Example - callback&#x3D;func (optional)
     * @return Call&lt;ForecastHourly&gt;
     */
    @GET("forecast/3hourly")
    Call<ForecastHourly> forecast3hourlylatlatlonlonGet(

            @Query("lat") Double lat,
            @Query("lon") Double lon
            , @Query("days") BigDecimal days
            , @Query("units") String units
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

    /**
     * Returns a 3-hourly forecast - Given a Postal Code. Returns a 3-hourly forecast, where each
     * point represents a three hour period. Every point has a datetime string in the format
     * \&quot;YYYY-MM-DD:HH\&quot;. Time is UTC.
     *
     * @param postalCode Postal Code. Example: 28546 (required)
     
     * @param country Country Code (2 letter). (optional)
     * @param days Number of days to return. Default 5. (optional)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
     * @return Call&lt;ForecastHourly&gt;
     */
    @GET("forecast/3hourly")
    Call<ForecastHourly> forecast3hourlypostalCodepostalCodeGet(

            @Query("postal_code") Integer postalCode
            , @Query("country") String country
            , @Query("days") BigDecimal days
            , @Query("units") String units
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

}

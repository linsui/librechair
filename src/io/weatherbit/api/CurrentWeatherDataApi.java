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

import io.weatherbase.api.model.CurrentObsGroup;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Query;

public interface CurrentWeatherDataApi {

    /**
     * Returns a group of observations given a list of cities Returns a group of Current
     * Observations - Given a list of City IDs.
     *
     * @param cities Comma separated list of City ID&#x27;s. Example: 4487042, 4494942, 4504871
     * (required)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param marine Marine stations only (buoys, oil platforms, etc) (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback - Example - callback&#x3D;func
     * (optional)
     * @return Call&lt;CurrentObsGroup&gt;
     */
    @GET("current")
    Call<CurrentObsGroup> currentcitiescitiesGet(

            @Query("cities") String cities
            , @Query("units") String units
            , @Query("marine") String marine
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

    /**
     * Returns a current observation by city id. Returns current weather observation - Given a City
     * ID.
     *
     * @param cityId City ID. Example: 4487042 (required)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param marine Marine stations only (buoys, oil platforms, etc) (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback - Example - callback&#x3D;func
     * (optional)
     * @return Call&lt;CurrentObsGroup&gt;
     */
    @GET("current?city_id={city_id}")
    Call<CurrentObsGroup> currentcityIdcityIdGet(

            @Query("city_id") String cityId
            , @Query("units") String units
            , @Query("marine") String marine
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

    /**
     * Returns a Current Observation - Given City and/or State, Country. Returns a Current
     * Observation - Given a city in the format of City,ST or City. The state, and country
     * parameters can be provided to make the search more accurate.
     *
     * @param city City search.. Example - &amp;city&#x3D;Raleigh,NC or &amp;city&#x3D;Berlin,DE or
     * city&#x3D;Paris&amp;country&#x3D;FR (required)
     * @param country Country Code (2 letter). (required)
     * @param state Full name of state. (optional)
     * @param marine Marine stations only (buoys, oil platforms, etc) (optional)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback - Example - callback&#x3D;func
     * (optional)
     * @return Call&lt;CurrentObsGroup&gt;
     */
    @GET("current")
    Call<CurrentObsGroup> currentcitycitycountrycountryGet(

            @Query("city") String city,
            @Query("country") String country
            , @Query("state") String state
            , @Query("marine") String marine
            , @Query("units") String units
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

    /**
     * Returns a Current Observation - Given an IP address, or auto. Returns a Current Observation -
     * Given an IP address, or auto.
     *
     * @param ip IP Address, or auto. Example: ip&#x3D;auto (required)
     * @param marine Marine stations only (buoys, oil platforms, etc) (optional)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback - Example - callback&#x3D;func
     * (optional)
     * @return Call&lt;CurrentObsGroup&gt;
     */
    @GET("current")
    Call<CurrentObsGroup> currentipipGet(

            @Query("ip") String ip
            , @Query("marine") String marine
            , @Query("units") String units
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

    /**
     * Returns a Current Observation - Given a lat/lon. Returns a Current Observation - given a lat,
     * and a lon.
     *
     * @param lat Latitude component of location. (required)
     * @param lon Longitude component of location. (required)
     * @param marine Marine stations only (buoys, oil platforms, etc) (optional)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback - Example - callback&#x3D;func
     * (optional)
     * @return Call&lt;CurrentObsGroup&gt;
     */
    @GET("current")
    Call<CurrentObsGroup> currentlatlatlonlonGet(

            @Query("lat") Double lat,
            @Query("lon") Double lon
            , @Query("marine") String marine
            , @Query("units") String units
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

    /**
     * Returns a current observation by postal code. Returns current weather observation - Given a
     * Postal Code.
     *
     * @param postalCode Postal Code. Example: 28546 (required)
     * @param country Country Code (2 letter). (optional)
     * @param marine Marine stations only (buoys, oil platforms, etc) (optional)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback - Example - callback&#x3D;func
     * (optional)
     * @return Call&lt;CurrentObsGroup&gt;
     */
    @GET("current")
    Call<CurrentObsGroup> currentpostalCodepostalCodeGet(

            @Query("postal_code") String postalCode
            , @Query("country") String country
            , @Query("marine") String marine
            , @Query("units") String units
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

    /**
     * Returns a group of observations given a list of stations Returns a group of Current
     * Observations - Given a list of Station Call IDs.
     *
     * @param stations Comma separated list of Station Call ID&#x27;s. Example: KRDU,KBFI,KVNY
     * (required)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
     * @return Call&lt;CurrentObsGroup&gt;
     */
    @GET("current?stations={stations}")
    Call<CurrentObsGroup> currentstationsstationsGet(

            @Query("stations") String stations
            , @Query("units") String units
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

    /**
     * Returns a Current Observation. - Given a station ID. Returns a Current Observation - Given a
     * station ID.
     *
     * @param station Station Call ID. (required)
     * @param units Convert to units. Default Metric See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;units field description&lt;/a&gt; (optional)
     * @param lang Language (Default: English) See &lt;a target&#x3D;&#x27;blank&#x27;
     * href&#x3D;&#x27;/api/requests&#x27;&gt;language field description&lt;/a&gt; (optional)
     * @param paramCallback Wraps return in jsonp callback. Example: callback&#x3D;func (optional)
     * @return Call&lt;CurrentObsGroup&gt;
     */
    @GET("current")
    Call<CurrentObsGroup> currentstationstationGet(

            @Query("station") String station
            , @Query("units") String units
            , @Query("lang") String lang
            , @Query("callback") String paramCallback

    );

}

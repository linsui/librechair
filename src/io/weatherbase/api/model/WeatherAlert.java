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

/*
 * Weatherbit.io - Swagger UI Weather API documentation
 * This is the documentation for the Weatherbit Weather API.  The base URL for the API is [http://api.weatherbit.io/v2.0/](http://api.weatherbit.io/v2.0/) or [https://api.weatherbit.io/v2.0/](http://api.weatherbit.io/v2.0/). Below is the Swagger UI documentation for the API. All API requests require the `key` parameter.        An Example for a 5 day forecast for London, UK would be `http://api.weatherbit.io/v2.0/forecast/3hourly?city=London`&`country=UK`. See our [Weather API description page](https://www.weatherbit.io/api) for additional documentation.
 *
 * OpenAPI spec version: 2.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.weatherbase.api.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * WeatherAlert
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2019-07-23T18:38:21.490044+08:00[Asia/Shanghai]")
public class WeatherAlert {
  @SerializedName("lat")
  private BigDecimal lat = null;

  @SerializedName("lon")
  private BigDecimal lon = null;

  @SerializedName("alerts")
  private List<WeatherAlertGroup> alerts = null;

  public WeatherAlert lat(BigDecimal lat) {
    this.lat = lat;
    return this;
  }

   /**
   * Latitude
   * @return lat
  **/
  @Schema(example = "38.0", description = "Latitude")
  public BigDecimal getLat() {
    return lat;
  }

  public void setLat(BigDecimal lat) {
    this.lat = lat;
  }

  public WeatherAlert lon(BigDecimal lon) {
    this.lon = lon;
    return this;
  }

   /**
   * Longitude
   * @return lon
  **/
  @Schema(example = "-78.25", description = "Longitude")
  public BigDecimal getLon() {
    return lon;
  }

  public void setLon(BigDecimal lon) {
    this.lon = lon;
  }

  public WeatherAlert alerts(List<WeatherAlertGroup> alerts) {
    this.alerts = alerts;
    return this;
  }

  public WeatherAlert addAlertsItem(WeatherAlertGroup alertsItem) {
    if (this.alerts == null) {
      this.alerts = new ArrayList<WeatherAlertGroup>();
    }
    this.alerts.add(alertsItem);
    return this;
  }

   /**
   * Get alerts
   * @return alerts
  **/
  @Schema(description = "")
  public List<WeatherAlertGroup> getAlerts() {
    return alerts;
  }

  public void setAlerts(List<WeatherAlertGroup> alerts) {
    this.alerts = alerts;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WeatherAlert weatherAlert = (WeatherAlert) o;
    return Objects.equals(this.lat, weatherAlert.lat) &&
        Objects.equals(this.lon, weatherAlert.lon) &&
        Objects.equals(this.alerts, weatherAlert.alerts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lat, lon, alerts);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WeatherAlert {\n");
    
    sb.append("    lat: ").append(toIndentedString(lat)).append("\n");
    sb.append("    lon: ").append(toIndentedString(lon)).append("\n");
    sb.append("    alerts: ").append(toIndentedString(alerts)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
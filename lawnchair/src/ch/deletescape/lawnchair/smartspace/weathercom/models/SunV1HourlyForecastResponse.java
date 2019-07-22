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

package ch.deletescape.lawnchair.smartspace.weathercom.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

public class SunV1HourlyForecastResponse extends BaseModel {

    public SuccessMetadataSchema metadata;
    public List<ForecastSchema> forecasts;
    public List<ErrorWrapper> errors;


    public class SuccessMetadataSchema extends BaseModel {
        public String language;
        public String transactionID;
        public String version;
        public String units;

        public Long expire_time_gmt;
        public Integer status_code;

        public Double latitude;
        public Double longitude;
    }

    public class ErrorWrapper extends BaseModel {
        public ErrorSchema errorResponse;
    }

    public class ForecastSchema extends BaseModel {
        @SerializedName("class")
        public String propertyClass = "fod_long_range_daily";

        @SerializedName("expire_time_gmt")
        public Long expireTimeGmt ;

        @SerializedName("fcst_valid")
        public Long fcstValid ;

        @SerializedName("fcst_valid_local")
        public String fcstValidLocal ;

        @SerializedName("temp")
        public Integer temp ;

        @SerializedName("icon_extd")
        public Integer iconExtd ;

        @SerializedName("icon_code")
        public Integer iconCode ;

        @SerializedName("dow")
        public String dow = "Thursday";

        @SerializedName("phrase_12char")
        public String phrase12char = "Cloudy.";

        @SerializedName("phrase_22char")
        public String phrase22char = "Cloudy.";

        @SerializedName("phrase_32char")
        public String phrase32char = "Fog Late.";

        @SerializedName("pop")
        public Integer pop ;

        @SerializedName("precip_type")
        public String precipType = "rain";

        @SerializedName("rh")
        public Integer rh ;

        @SerializedName("wspd")
        public Integer wspd ;

        @SerializedName("wdir")
        public Integer wdir ;

        @SerializedName("wdir_cardinal")
        public String wdirCardinal = "SE";

        @SerializedName("gust")
        public Integer gust ;

        @SerializedName("mslp")
        public BigDecimal mslp ;

        @SerializedName("num")
        public Integer num ;

        @SerializedName("day_ind")
        public String dayInd = "D";

        @SerializedName("dewpt")
        public Integer dewpt ;

        @SerializedName("hi")
        public Integer hi ;

        @SerializedName("wc")
        public Integer wc ;

        @SerializedName("feels_like")
        public Integer feelsLike ;

        @SerializedName("subphrase_pt1")
        public String subphrasePt1 = "Cloudy";

        @SerializedName("subphrase_pt2")
        public String subphrasePt2 = "Late";

        @SerializedName("subphrase_pt3")
        public String subphrasePt3 = "Thunder";

        @SerializedName("qpf")
        public BigDecimal qpf ;

        @SerializedName("snow_qpf")
        public Integer snowQpf = 0;

        @SerializedName("clds")
        public Integer clds ;

        @SerializedName("uv_index_raw")
        public BigDecimal uvIndexRaw ;

        @SerializedName("uv_index")
        public Integer uvIndex ;

        @SerializedName("uv_desc")
        public String uvDesc = "2 is Not Available; 1 is No Report; 0 to 2 is Low; 3 to 5 is Moderate; 6 to 7 is High; 8 to 10 is Very High; 11 to 16 is Extreme";

        @SerializedName("uv_warning")
        public Integer uvWarning ;

        @SerializedName("golf_index")
        public Integer golfIndex ;

        @SerializedName("golf_category")
        public String golfCategory = "Very Good";

        @SerializedName("severity")
        public Integer severity = 8;
    }
}

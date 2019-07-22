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

public class SunV1DailyForecastResponse extends BaseModel {
    public SuccessMetadataSchema metadata;
    public List<Forecast> forecastList;
    public List<ErrorSchema> errors;
    public Boolean success;

    public static class SuccessMetadataSchema extends BaseModel {
        public String version;
        public String transaction_id;
        public String language;
        public String units;

        public Long expire_time_gmt;
        public Integer status_code;

        public Double longitude;
        public Double latitude;

        public boolean success;
        public ErrorSchema error;
    }

    public static class Forecast extends BaseModel {
        @SerializedName("class")
        public String propertyClass;

        @SerializedName("expire_time_gmt")
        public Long expireTimeGmt;

        @SerializedName("fcst_valid")
        public Long fcstValid;

        @SerializedName("fcst_valid_local")
        public String fcstValidLocal;

        @SerializedName("num")
        public Integer num;

        @SerializedName("max_temp")
        public Integer maxTemp;

        @SerializedName("min_temp")
        public Integer minTemp;

        @SerializedName("torcon")
        public Integer torcon;

        @SerializedName("stormcon")
        public Integer stormcon;

        @SerializedName("blurb")
        public String blurb;

        @SerializedName("blurb_author")
        public String blurbAuthor;

        @SerializedName("lunar_phase_day")
        public Integer lunarPhaseDay;

        @SerializedName("dow")
        public String dow;

        @SerializedName("lunar_phase")
        public String lunarPhase;

        @SerializedName("lunar_phase_code")
        public String lunarPhaseCode;

        @SerializedName("sunrise")
        public String sunrise;

        @SerializedName("sunset")
        public String sunset;

        @SerializedName("moonrise")
        public String moonrise;

        @SerializedName("moonset")
        public String moonset;

        @SerializedName("qualifier_code")
        public String qualifierCode;

        @SerializedName("qualifier")
        public String qualifier;

        @SerializedName("narrative")
        public String narrative;

        @SerializedName("qpf")
        public BigDecimal qpf;

        @SerializedName("snow_qpf")
        public BigDecimal snowQpf;

        @SerializedName("snow_range")
        public String snowRange;

        @SerializedName("snow_phrase")
        public String snowPhrase;

        @SerializedName("snow_code")
        public String snowCode;
    }
    
    public static class ForecastDay extends BaseModel {
        @SerializedName("accumulation_phrase")
        public String accumulationPhrase;

        @SerializedName("alt_ daypart_name")
        public String altDaypartName;

        @SerializedName("clds")
        public Integer clds;

        @SerializedName("day_ind")
        public String dayInd;

        @SerializedName("daypart_name")
        public String daypartName;

        @SerializedName("expire_time_gmt")
        public BigDecimal expireTimeGmt;

        @SerializedName("fcst_valid")
        public BigDecimal fcstValid;

        @SerializedName("fcst_valid_local")
        public BigDecimal fcstValidLocal;

        @SerializedName("golf_category")
        public String golfCategory;

        @SerializedName("golf_index")
        public Integer golfIndex;

        @SerializedName("hi")
        public Integer hi;

        @SerializedName("icon_code")
        public Integer iconCode;

        @SerializedName("icon_extd")
        public Integer iconExtd;

        @SerializedName("long_daypart_name")
        public String longDaypartName;

        @SerializedName("narrative")
        public String narrative;

        @SerializedName("num")
        public Integer num;

        @SerializedName("phrase_12char")
        public String phrase12char;

        @SerializedName("phrase_22char")
        public String phrase22char;

        @SerializedName("phrase_32char")
        public String phrase32char;

        @SerializedName("pop")
        public Integer pop;

        @SerializedName("pop_phrase")
        public String popPhrase;

        @SerializedName("precip_type")
        public String precipType;

        @SerializedName("qpf")
        public BigDecimal qpf;

        @SerializedName("qualifier")
        public String qualifier;

        @SerializedName("qualifier_code")
        public String qualifierCode;

        @SerializedName("rh")
        public Integer rh;

        @SerializedName("shortcast")
        public String shortcast;

        @SerializedName("snow_code")
        public String snowCode;

        @SerializedName("snow_phrase")
        public String snowPhrase;

        @SerializedName("snow_qpf")
        public BigDecimal snowQpf;

        @SerializedName("snow_range")
        public BigDecimal snowRange;

        @SerializedName("subphrase_pt1")
        public String subphrasePt1;

        @SerializedName("subphrase_pt2")
        public String subphrasePt2;

        @SerializedName("subphrase_pt3")
        public String subphrasePt3;

        @SerializedName("temp")
        public Integer temp;

        @SerializedName("temp_phrase")
        public String tempPhrase;

        @SerializedName("thunder_enum")
        public Integer thunderEnum;

        @SerializedName("thunder_enum_phrase")
        public String thunderEnumPhrase;

        @SerializedName("uv_desc")
        public String uvDesc;

        @SerializedName("uv_index")
        public Integer uvIndex;

        @SerializedName("uv_index_raw")
        public BigDecimal uvIndexRaw;

        @SerializedName("uv_warning")
        public Integer uvWarning;

        @SerializedName("vocal_key")
        public String vocalKey;

        @SerializedName("wc")
        public Integer wc;

        @SerializedName("wdir")
        public Integer wdir;

        @SerializedName("wdir_cardinal")
        public String wdirCardinal;

        @SerializedName("wind_phrase")
        public String windPhrase;

        @SerializedName("wspd")
        public Integer wspd;

        @SerializedName("wxman")
        public String wxman;
    }
}

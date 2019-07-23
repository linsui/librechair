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
import java.util.ArrayList;
import java.util.List;

public class SunV3DailyForecastResponse extends BaseModel {
    @SerializedName("dayOfWeek")
    private List<String> dayOfWeek;

    @SerializedName("expirationTimeUtc")
    private List<Long> expirationTimeUtc;

    @SerializedName("moonPhase")
    private List<String> moonPhase;

    @SerializedName("moonPhaseCode")
    private List<String> moonPhaseCode;

    @SerializedName("moonPhaseDay")
    private List<Double> moonPhaseDay;

    @SerializedName("moonriseTimeLocal")
    private List<String> moonriseTimeLocal;

    @SerializedName("moonriseTimeUtc")
    private List<Double> moonriseTimeUtc;

    @SerializedName("moonsetTimeLocal")
    private List<String> moonsetTimeLocal;

    @SerializedName("moonsetTimeUtc")
    private List<Double> moonsetTimeUtc;

    @SerializedName("narrative")
    private List<String> narrative;

    @SerializedName("qpf")
    private List<Double> qpf;

    @SerializedName("qpfSnow")
    private List<Double> qpfSnow;

    @SerializedName("sunriseTimeLocal")
    private List<String> sunriseTimeLocal;

    @SerializedName("sunriseTimeUtc")
    private List<Double> sunriseTimeUtc;

    @SerializedName("sunsetTimeLocal")
    private List<String> sunsetTimeLocal;

    @SerializedName("sunsetTimeUtc")
    private List<Double> sunsetTimeUtc;

    @SerializedName("temperatureMax")
    private List<Double> temperatureMax;

    @SerializedName("temperatureMin")
    private List<Double> temperatureMin;

    @SerializedName("validTimeLocal")
    private List<String> validTimeLocal;

    @SerializedName("validTimeUtc")
    private List<Double> validTimeUtc;

    @SerializedName("daypart")
    private List<Object> daypart;

}

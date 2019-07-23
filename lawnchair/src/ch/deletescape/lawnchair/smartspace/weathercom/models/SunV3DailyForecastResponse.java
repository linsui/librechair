/* *     Copyright (c) 2017-2019 the Lawnchair team     *     Copyright (c)  2019 oldosfan (would) *     This file is part of Lawnchair Launcher. * *     Lawnchair Launcher is free software: you can redistribute it and/or modify *     it under the terms of the GNU General Public License as published by *     the Free Software Foundation, either version 3 of the License, or *     (at your option) any later version. * *     Lawnchair Launcher is distributed in the hope that it will be useful, *     but WITHOUT ANY WARRANTY; without even the implied warranty of *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *     GNU General Public License for more details. * *     You should have received a copy of the GNU General Public License *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>. */
package ch.deletescape.lawnchair.smartspace.weathercom.models;

import ch.deletescape.lawnchair.smartspace.weathercom.models.SunV1DailyForecastResponse.ForecastDay;
import java.util.List;

public class SunV3DailyForecastResponse extends BaseModel {
    public List<String> dayOfWeek;
    public List<Long> expirationTimeUtc;
    public List<String> moonPhase;
    public List<String> moonPhaseCode;
    public List<Double> moonPhaseDay;
    public List<String> moonriseTimeLocal;
    public List<Double> moonriseTimeUtc;
    public List<String> moonsetTimeLocal;
    public List<Double> moonsetTimeUtc;
    public List<String> narrative;
    public List<Double> qpf;
    public List<Double> qpfSnow;
    public List<String> sunriseTimeLocal;
    public List<Double> sunriseTimeUtc;
    public List<String> sunsetTimeLocal;
    public List<Double> sunsetTimeUtc;
    public List<Double> temperatureMax;
    public List<Double> temperatureMin;
    public List<String> validTimeLocal;
    public List<Long> validTimeUtc;
    public List<DayPart> daypart;

    public static class DayPart extends BaseModel {
        public List<Integer> cloudCover;
        public List<String> dayOrNight;
        public List<String> dayPartName;
        public List<Integer> iconCode;
        public List<Integer> iconCodeExtend;
        public List<String> narritive;
        public List<Integer> precipChance;
        public List<String> precipType;
        public List<Double> qpf;
        public List<Double> qpfSnow;
        public List<String> qualifierCode;
        public List<String> qualifierPhrase;
        public List<Double> relativeHumidity;
        public List<String> snowRange;
        public List<Double> temperature;
        public List<Double> temperatureHeatIndex;
        public List<Double> temperatureWindChill;
        public List<String> thunderCategory;
        public List<Double> thunderIndex;
        public List<String> uvDescription;
        public List<String> uvIndex;
        public List<Integer> windDirection;
        public List<String> windDirectionCardinal;
        public List<String> windPhrase;
        public List<Integer> windSpeed;
        public List<String> wxPhraseLong;
        public List<String> wxPhraseShort;
    }
}
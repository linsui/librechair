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

package ch.deletescape.lawnchair.smartspace.weather;

import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.WeatherData;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public interface ForecastProvider {

    Forecast getForecast();

    class Forecast {

        private final ForecastData[] data;

        public Forecast(ForecastData[] data) {
            this.data = data;
        }

        public Forecast(List<ForecastData> dataList) {
            this(Arrays.copyOf(dataList.toArray(), dataList.size(), ForecastData[].class));
        }

        public ForecastData[] getData() {
            return data;
        }
    }

    class ForecastData {

        private WeatherData data;
        private Date date;

        public ForecastData(WeatherData data, Date date) {
            this.data = data;
            this.date = date;
        }
    }
}

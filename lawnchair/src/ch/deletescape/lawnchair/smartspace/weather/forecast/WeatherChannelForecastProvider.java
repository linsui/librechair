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

package ch.deletescape.lawnchair.smartspace.weather.forecast;

import ch.deletescape.lawnchair.smartspace.weathercom.Constants.WeatherComConstants;
import ch.deletescape.lawnchair.smartspace.weathercom.WeatherComRetrofitServiceFactory;
import ch.deletescape.lawnchair.smartspace.weathercom.models.SunV1CurrentConditionsResponse;
import ch.deletescape.lawnchair.util.Temperature;
import ch.deletescape.lawnchair.util.Temperature.Unit;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

public class WeatherChannelForecastProvider implements ForecastProvider {

    @NotNull
    @Override
    public Forecast getHourlyForecast(double lat, double lon) throws ForecastException {
        throw new ForecastException("getHourlyForecast not yet implemented");
    }

    @NotNull
    @Override
    public DailyForecast getDailyForecast(double lat, double lon) throws ForecastException {
        throw new ForecastException("getDailyForecast not yet implemented");
    }

    @NotNull
    @Override
    public CurrentWeather getCurrentWeather(double lat, double lon) throws ForecastException {
        try {
            Response<SunV1CurrentConditionsResponse> response = WeatherComRetrofitServiceFactory.INSTANCE.getWeatherComWeatherRetrofitServiceForForecast().getCurrentConditions(lat, lon).execute();
            if (!response.isSuccessful()) {
                throw new ForecastException("api call failed: " + response.message());
            }
            SunV1CurrentConditionsResponse result = response.body();
            return new CurrentWeather(new Integer[] {WeatherComConstants.INSTANCE.getWEATHER_COND_MAP().get(result.observation.wxIcon)},
                    Date.from(Instant.ofEpochSecond(result.observation.validTimeGmt)), new Temperature(result.observation.temp, Unit.Fahrenheit));
        } catch (IOException e) {
            throw new ForecastException(e);
        }
    }
}

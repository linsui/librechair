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

import android.content.Context;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.WeatherData;
import ch.deletescape.lawnchair.smartspace.WeatherIconProvider;
import ch.deletescape.lawnchair.smartspace.weathercom.Constants;
import ch.deletescape.lawnchair.smartspace.weathercom.Constants.WeatherComConstants;
import ch.deletescape.lawnchair.smartspace.weathercom.WeatherComRetrofitServiceFactory;
import ch.deletescape.lawnchair.smartspace.weathercom.models.SunV1CurrentConditionsResponse;
import ch.deletescape.lawnchair.smartspace.weathercom.models.SunV1DailyForecastResponse;
import ch.deletescape.lawnchair.smartspace.weathercom.models.SunV1HourlyForecastResponse;
import ch.deletescape.lawnchair.smartspace.weathercom.models.SunV1HourlyForecastResponse.ForecastSchema;
import ch.deletescape.lawnchair.smartspace.weathercom.models.SunV3DailyForecastResponse;
import ch.deletescape.lawnchair.util.Temperature;
import ch.deletescape.lawnchair.util.Temperature.Unit;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

public class WeatherChannelForecastProvider implements ForecastProvider {

    private final Context c;

    public WeatherChannelForecastProvider(Context c) {

        this.c = c;
    }

    @NotNull
    @Override
    public Forecast getHourlyForecast(double lat, double lon) throws ForecastException {
        try {
            Response<SunV1HourlyForecastResponse> response = WeatherComRetrofitServiceFactory.INSTANCE
                    .getWeatherComWeatherRetrofitServiceForForecast()
                    .getHourlyForecast(lat, lon, 24,
                            LawnchairUtilsKt.getLocale(c).getLanguage(), "m").execute();
            if (!response.isSuccessful()) {
                throw new ForecastException(response.message());
            }
            SunV1HourlyForecastResponse forecast = response.body();
            List<ForecastData> dataList = LawnchairUtilsKt.newList();
            for (ForecastSchema schema : forecast.forecasts) {
                dataList.add(
                        new ForecastData(new WeatherData(new WeatherIconProvider(c).getIcon(
                                schema.dayInd.equals("D") ? WeatherComConstants.INSTANCE
                                        .getWEATHER_ICONS_DAY().get(schema.iconCode).getSecond()
                                        : WeatherComConstants.INSTANCE.getWEATHER_ICONS_NIGHT()
                                                .get(schema.iconCode).getSecond()),
                                new Temperature(schema.temp, Unit.Celsius), null, null, null, lat,
                                lon,
                                (schema.dayInd.equals("D") ? WeatherComConstants.INSTANCE
                                        .getWEATHER_ICONS_DAY().get(schema.iconCode).getSecond()
                                        : WeatherComConstants.INSTANCE.getWEATHER_ICONS_NIGHT()
                                                .get(schema.iconCode).getSecond())),
                                Date.from(Instant.ofEpochSecond(schema.fcstValid)), new Integer[]{
                                WeatherComConstants.INSTANCE.getWEATHER_COND_MAP().get(
                                        schema.iconCode)}));
            }
            return new Forecast(dataList);
        } catch (IOException | NullPointerException e) {
            throw new ForecastException(e);
        }
    }

    @NotNull
    @Override
    public DailyForecast getDailyForecast(double lat, double lon) throws ForecastException {
        try {
            Response<SunV3DailyForecastResponse> response = WeatherComRetrofitServiceFactory.INSTANCE
                    .getWeatherComWeatherRetrofitServiceForForecast()
                    .getDailyForecastWithApiV3(7, lat + "," + lon, LawnchairUtilsKt.getLocale(c).getLanguage(), "m")
                    .execute();

            if (!response.isSuccessful()) {
                throw new ForecastException(response.message());
            }
            SunV3DailyForecastResponse forecast = response.body();
            if (forecast == null) {
                throw new ForecastException("forecast was null!");
            }
            List<DailyForecastData> dailyForecastData = LawnchairUtilsKt.newList();
            for (int i = 0; i < forecast.temperatureMax.size(); ++i) {
                dailyForecastData.add(new DailyForecastData(new Temperature((forecast.temperatureMax.get(i) == null ? 0 : forecast.temperatureMax.get(i).intValue()), Unit.Celsius), new Temperature(forecast.temperatureMin.get(i).intValue(), Unit.Celsius),
                        Date.from(Instant.ofEpochSecond(forecast.validTimeUtc.get(i))),
                        new WeatherIconProvider(c).getIcon(WeatherComConstants.INSTANCE.getWEATHER_ICONS_DAY().get(forecast.daypart.get(0).iconCode.get(i * 2) == null ? forecast.daypart.get(0).iconCode.get(i * 2 + 1) : forecast.daypart.get(0).iconCode.get(i * 2)).getSecond()), null));
            }
            return new DailyForecast(dailyForecastData);

        } catch (IOException e) {
            throw new ForecastException(e);
        }
    }

    @NotNull
    @Override
    public CurrentWeather getCurrentWeather(double lat, double lon) throws ForecastException {
        try {
            Response<SunV1CurrentConditionsResponse> response = WeatherComRetrofitServiceFactory.INSTANCE
                    .getWeatherComWeatherRetrofitServiceForForecast().getCurrentConditions(lat, lon)
                    .execute();
            if (!response.isSuccessful()) {
                throw new ForecastException("api call failed: " + response.message());
            }
            SunV1CurrentConditionsResponse result = response.body();
            return new CurrentWeather(new Integer[]{
                    WeatherComConstants.INSTANCE.getWEATHER_COND_MAP().get(
                            result.observation.wxIcon)},
                    Date.from(Instant.ofEpochSecond(result.observation.validTimeGmt)),
                    new Temperature(result.observation.temp, Unit.Fahrenheit));
        } catch (IOException e) {
            throw new ForecastException(e);
        }
    }
}

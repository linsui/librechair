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
 *
 *     Powered by OpenWeatherMap <openweathermap.org>
 */

package ch.deletescape.lawnchair.smartspace.weather.forecast;

import android.content.Context;
import android.util.Log;
import android.util.TimeUtils;
import androidx.annotation.NonNull;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.WeatherData;
import ch.deletescape.lawnchair.smartspace.WeatherIconProvider;
import ch.deletescape.lawnchair.smartspace.weather.forecast.AccuWeatherForecastProvider.CachedResponse;
import ch.deletescape.lawnchair.util.Temperature;
import ch.deletescape.lawnchair.util.Temperature.Unit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kotlin.Pair;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.core.OWMPro;
import net.aksingh.owmjapis.model.DailyWeatherForecast;
import net.aksingh.owmjapis.model.HourlyWeatherForecast;
import net.aksingh.owmjapis.model.param.Weather;
import org.jetbrains.annotations.NotNull;

public class OWMForecastProvider implements ForecastProvider {

    private final Context context;
    private final OWM owm;
    private CachedResponse<DailyForecast> dailyForecastCachedResponse;
    private CachedResponse<Forecast> forecastCachedResponse;
    private CachedResponse<CurrentWeather> currentWeatherCachedResponse;

    public OWMForecastProvider(Context context) {
        this.context = context;
        this.owm = new OWM(LawnchairUtilsKt.getLawnchairPrefs(context).getWeatherApiKey());
    }

    @NonNull
    @Override
    public Forecast getHourlyForecast(double lat, double lon) throws ForecastException {
        Log.d(getClass().getName(),
                "getHourlyForecast: retrieving forecast, " + forecastCachedResponse);
        if (forecastCachedResponse != null && !forecastCachedResponse.getExpired()) {
            Log.d(getClass().getName(),
                    "getHourlyForecast(double, double): returning cached forecast");
            return forecastCachedResponse.getValue();
        }
        try {
            Log.d(getClass().getName(), "getHourlyForecast(double, double): retrieving forecasts");
            HourlyWeatherForecast forecast = owm.hourlyWeatherForecastByCoords(lat, lon);
            Log.d(getClass().getName(),
                    "getHourlyForecast(double, double): forecasts: " + forecast);
            List<ForecastData> dataList = LawnchairUtilsKt.newList();
            for (net.aksingh.owmjapis.model.param.WeatherData weather : forecast.getDataList()) {
                ArrayList<Integer> integers = new ArrayList<>();
                for (Weather i : weather.getWeatherList()) {
                    integers.add(i.getConditionId());
                }
                dataList.add(new ForecastData(new WeatherData(new WeatherIconProvider(context)
                        .getIcon(weather.getWeatherList().get(0).getIconCode()),
                        new Temperature(weather.getMainData().getTemp().intValue(), Unit.Kelvin),
                        null, null, null, lat, lon, weather.getWeatherList().get(0).getIconCode()),
                        weather.getDateTime(),
                        Arrays.copyOf(integers.toArray(), integers.size(), Integer[].class)));
            }
            forecastCachedResponse = new CachedResponse<>(
                    System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(25),
                    new Forecast(dataList));
            return forecastCachedResponse.getValue();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new ForecastException(e);
        }
    }

    @NonNull
    @Override
    public DailyForecast getDailyForecast(double lat, double lon) throws ForecastException {
        if (dailyForecastCachedResponse != null && !dailyForecastCachedResponse.getExpired()) {
            return dailyForecastCachedResponse.getValue();
        }
        try {
            DailyWeatherForecast forecast = new OWMPro(owm.getApiKey())
                    .dailyWeatherForecastByCoords(lat, lon);
            List<DailyForecastData> dataList = LawnchairUtilsKt.newList();
            for (net.aksingh.owmjapis.model.param.ForecastData weather : forecast.getDataList()) {
                dataList.add(new DailyForecastData(
                        new Temperature(weather.getTempData().getTempMax().intValue(), Unit.Kelvin),
                        new Temperature(weather.getTempData().getTempMin().intValue(), Unit.Kelvin),
                        weather.getDateTime(),
                        new WeatherIconProvider(context)
                                .getIcon(weather.getWeatherList().get(0).getIconCode()), null));
            }
            return LawnchairUtilsKt.also(new DailyForecast(dataList), it -> {
                dailyForecastCachedResponse = new CachedResponse<>(
                        System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(40), it);
                return kotlin.Unit.INSTANCE;
            });
        } catch (Throwable e) {
            throw new ForecastException(e);
        }
    }

    @NotNull
    @Override
    public CurrentWeather getCurrentWeather(double lat, double lon) throws ForecastException {
        Log.d(getClass().getName(),
                "getCurrentWeather: retrieving current weather, " + currentWeatherCachedResponse);
        if (currentWeatherCachedResponse != null && !currentWeatherCachedResponse.getExpired()) {
            Log.d(getClass().getName(),
                    "getCurrentWeather(double, double): returning cached forecast");
            return currentWeatherCachedResponse.getValue();
        }
        try {
            net.aksingh.owmjapis.model.CurrentWeather weather = owm
                    .currentWeatherByCoords(lat, lon);
            List<Integer> integers = LawnchairUtilsKt.newList();
            for (Weather weather1 : weather.getWeatherList()) {
                integers.add(weather1.getConditionId());
            }
            return LawnchairUtilsKt.also(new CurrentWeather(
                    Arrays.copyOf(integers.toArray(), integers.size(), Integer[].class),
                    weather.getDateTime(), new Temperature(
                    weather.getMainData().getTemp().intValue(), Unit.Kelvin),
                    new WeatherIconProvider(context)
                            .getIcon(weather.getWeatherList().get(0).getIconCode()),
                    weather.getRainData().getPrecipVol3h()), it -> {
                currentWeatherCachedResponse = new CachedResponse<>(
                        System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10), it);
                return kotlin.Unit.INSTANCE;
            });
        } catch (Throwable e) {
            throw new ForecastException(e);
        }

    }

    @NotNull
    @Override
    public Pair<Double, Double> getGeolocation(@NotNull String query) throws ForecastException {
        try {
            net.aksingh.owmjapis.model.CurrentWeather weather = owm.currentWeatherByCityName(query);
            return new Pair<>(weather.getCoordData().getLatitude(),
                    weather.getCoordData().getLongitude());
        } catch (APIException | RuntimeException e) {
            throw new ForecastException(e);
        }
    }
}

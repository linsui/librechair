/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.chips.weather;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import com.android.launcher3.Utilities;

import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.LawnchairApp;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.persistence.FeedPersistence;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController;
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider;

public class ForecastChipProvider extends ChipProvider
        implements LawnchairSmartspaceController.Listener {
    @Nullable
    private LawnchairSmartspaceController.WeatherData weather;

    @Nullable
    private ForecastProvider.Forecast forecast;
    private Context context;

    public ForecastChipProvider(Context context) {
        this.context = context;
        ((LawnchairApp) context.getApplicationContext()).getSmartspace().addListener(this);
    }

    @Override
    public List<Item> getItems(Context context) {
        return forecast == null ? Collections.EMPTY_LIST : Arrays.stream(forecast.getData()).map(
                it -> {
                    Item item = new Item();
                    item.icon = new BitmapDrawable(context.getResources(), it.getData().getIcon());
                    item.title = String.format("%s, %s", it.getData().getTemperature().toString(
                            Utilities.getLawnchairPrefs(context).getWeatherUnit()),
                            LawnchairUtilsKt.formatTime(
                                    ZonedDateTime.ofInstant(Instant.ofEpochMilli(
                                            it.getDate().getTime()),
                                            ZoneId.of("UTC")).withZoneSameInstant(
                                            TimeZone.getDefault().toZoneId()), context));
                    return item;
                }).limit((int) Math.round(
                FeedPersistence.Companion.getInstance(context).getWeatherItems())).collect(
                Collectors.toList());
    }

    @Override
    public void onDataUpdated(@Nullable LawnchairSmartspaceController.WeatherData weather,
                              @Nullable LawnchairSmartspaceController.CardData card) {
        this.weather = weather;
        if (weather != null && (forecast == null || ZonedDateTime.ofInstant(Instant.ofEpochMilli(
                System.currentTimeMillis()), TimeZone.getDefault().toZoneId()).withZoneSameInstant(
                ZoneId.of(
                        "UTC")).toEpochSecond() * 1000 > forecast.getData()[0].getDate().getTime())) {
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    forecast = LawnchairUtilsKt.getForecastProvider(context).getHourlyForecast(
                            weather.getCoordLat(), weather.getCoordLon());
                } catch (ForecastProvider.ForecastException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}

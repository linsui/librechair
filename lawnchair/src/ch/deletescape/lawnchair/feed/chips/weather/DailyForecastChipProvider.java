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
import com.google.android.apps.nexuslauncher.graphics.IcuDateTextView;

import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.awareness.WeatherManager;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.persistence.ChipPersistence;
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider;
import kotlin.Unit;

public class DailyForecastChipProvider extends ChipProvider {
    @Nullable
    private ForecastProvider.DailyForecast forecast;
    private Context context;

    public DailyForecastChipProvider(Context context) {
        this.context = context;
        WeatherManager.INSTANCE.subscribeDaily(fc -> {
            forecast = fc;
            return Unit.INSTANCE;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Item> getItems(Context context) {
        return forecast == null ? Collections.EMPTY_LIST : forecast.getDailyForecastData().stream().map(
                it -> {
                    Item item = new Item();
                    item.icon = new BitmapDrawable(context.getResources(), it.getIcon());
                    item.title = String.format("%s / %s, %s", it.getLow().toString(
                            Utilities.getLawnchairPrefs(context).getWeatherUnit()),
                            it.getHigh().toString(
                                    Utilities.getLawnchairPrefs(context).getWeatherUnit()),
                            ZonedDateTime.ofInstant(Instant.ofEpochMilli(
                                    it.getDate().getTime()),
                                    ZoneId.of("UTC")).withZoneSameInstant(
                                    TimeZone.getDefault().toZoneId()).toLocalDate().format(IcuDateTextView.getDateTimeFormat(context)));
                    return item;
                }).limit((int) Math.round(
                ChipPersistence.Companion.getInstance(context).getWeatherItems())).collect(
                Collectors.toList());
    }
}

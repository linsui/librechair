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
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.awareness.WeatherManager;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.feed.util.FeedUtil;
import ch.deletescape.lawnchair.feed.web.WebViewScreen;
import ch.deletescape.lawnchair.persistence.ChipPersistence;
import ch.deletescape.lawnchair.persistence.FeedPersistence;
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider;
import kotlin.Unit;

public class ForecastChipProvider extends ChipProvider {
    @Nullable
    private ForecastProvider.Forecast forecast;
    private Context context;

    public ForecastChipProvider(Context context) {
        this.context = context;
        WeatherManager.INSTANCE.subscribeHourly(fc -> {
            forecast = fc;
            return Unit.INSTANCE;
        });
    }

    @SuppressWarnings("unchecked")
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
                    item.viewClickListener = v -> {
                        if (!FeedPersistence.Companion.getInstance(
                                context).getDirectlyOpenLinksInBrowser()) {
                            WebViewScreen.obtain(context, it.getData().getForecastUrl())
                                    .display(getLauncherFeed(), null, null, v);
                        } else {
                            FeedUtil.openUrl(context, v, it.getData().getForecastUrl());
                        }
                    };
                    return item;
                }).limit((int) Math.round(
                ChipPersistence.Companion.getInstance(context).getWeatherItems())).collect(
                Collectors.toList());
    }
}

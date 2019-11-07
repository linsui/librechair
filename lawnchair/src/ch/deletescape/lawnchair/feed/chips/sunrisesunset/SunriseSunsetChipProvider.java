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

package ch.deletescape.lawnchair.feed.chips.sunrisesunset;

import android.content.Context;

import com.android.launcher3.R;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import net.time4j.PlainDate;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.engine.CalendarDate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.location.LocationManager;
import kotlin.Pair;

public class SunriseSunsetChipProvider extends ChipProvider {
    public SunriseSunsetChipProvider(Context ignored) {
    }

    @Override
    public List<Item> getItems(Context context) {
        Pair<Double, Double> location;
        if ((location = LocationManager.INSTANCE.getLocation()) != null) {
            SolarTime st = SolarTime.ofLocation(location.getFirst(), location.getSecond());
            ZonedDateTime sunrise = ZonedDateTime.ofInstant(Instant.ofEpochSecond(
                    PlainDate.nowInSystemTime().get(st.sunrise()).inZonalView(
                            ZoneId.systemDefault().getId()).getPosixTime()),
                    ZoneId.systemDefault());
            ZonedDateTime sunset = ZonedDateTime.ofInstant(Instant.ofEpochSecond(
                    PlainDate.nowInSystemTime().get(st.sunset()).inZonalView(
                            ZoneId.systemDefault().getId()).getPosixTime()),
                    ZoneId.systemDefault());

            Item sunriseItem = new Item();
            sunriseItem.icon = context.getDrawable(R.drawable.ic_sunrise_24dp);
            sunriseItem.title = LawnchairUtilsKt.formatTime(sunrise, context);

            Item sunsetItem = new Item();
            sunsetItem.icon = context.getDrawable(R.drawable.ic_sunset_24dp);
            sunsetItem.title = LawnchairUtilsKt.formatTime(sunset, context);

            return Arrays.asList(sunriseItem, sunsetItem);
        }
        return Collections.emptyList();
    }
}

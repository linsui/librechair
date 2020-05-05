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

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.awareness.SunriseSunsetManager;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.feed.util.Pairs;

public class SunriseSunsetChipProvider extends ChipProvider {
    private Pairs.Pair<ZonedDateTime, ZonedDateTime> sunriseSunset;

    public SunriseSunsetChipProvider(Context ignored) {
        SunriseSunsetManager.subscribe(ss -> sunriseSunset = ss);
    }

    @Override
    public List<Item> getItems(Context context) {
        Pairs.Pair<ZonedDateTime, ZonedDateTime> sunriseSunset;
        if ((sunriseSunset = this.sunriseSunset) != null) {
            Item sunriseItem = new Item();
            sunriseItem.icon = context.getDrawable(R.drawable.ic_sunrise_24dp);
            sunriseItem.title = LawnchairUtilsKt.formatTime(sunriseSunset.car(), context);

            Item sunsetItem = new Item();
            sunsetItem.icon = context.getDrawable(R.drawable.ic_sunset_24dp);
            sunsetItem.title = LawnchairUtilsKt.formatTime(sunriseSunset.cdr(), context);

            return Arrays.asList(sunriseItem, sunsetItem);
        }
        return Collections.emptyList();
    }
}

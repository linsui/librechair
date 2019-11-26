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

package ch.deletescape.lawnchair.feed.i18n;

import android.annotation.SuppressLint;

import java.util.Locale;

public class UnitLocale {
    private static UnitLocale IMPERIAL = new UnitLocale();
    private static UnitLocale METRIC = new UnitLocale();

    public static UnitLocale getDefault() {
        return getFrom(Locale.getDefault());
    }

    @SuppressLint("DefaultLocale")
    public String formatDistanceKm(long km) {
        return this == IMPERIAL ? String.format("%d mi", Math.round(0.621371 * km)) : String.format(
                "%d km", km);
    }

    public static UnitLocale getFrom(Locale locale) {
        String countryCode = locale.getCountry();
        switch (countryCode) {
            case "US":
            case "LR":
            case "MM":
                return IMPERIAL;
        }
        return METRIC;
    }
}
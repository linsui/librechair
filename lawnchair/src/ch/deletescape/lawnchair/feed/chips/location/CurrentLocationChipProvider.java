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

package ch.deletescape.lawnchair.feed.chips.location;

import android.content.Context;

import com.android.launcher3.R;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.location.LocationManager;
import geocode.GeoName;
import geocode.GeocoderCompat;
import geocode.ReverseGeoCode;
import kotlin.Pair;

public class CurrentLocationChipProvider extends ChipProvider {
    private ReverseGeoCode degeocoder;
    private GeoName name;

    @SuppressWarnings("unused")
    public CurrentLocationChipProvider(Context context) {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                try {
                    Pair<Double, Double> loc = LocationManager.INSTANCE.getLocation();
                    if (loc != null) {
                        degeocoder = new GeocoderCompat(context, true);
                        name = degeocoder.nearestPlace(loc.getFirst(), loc.getSecond());
                        break;
                    }
                    Thread.sleep(1000);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    }

    @Override
    public List<Item> getItems(Context context) {
        return name != null ? Collections.singletonList(((Supplier<Item>) () -> {
            Item item = new Item();
            item.icon = context.getDrawable(R.drawable.ic_location);
            item.title = name.name;
            return item;
        }).get()) : Collections.EMPTY_LIST;
    }
}

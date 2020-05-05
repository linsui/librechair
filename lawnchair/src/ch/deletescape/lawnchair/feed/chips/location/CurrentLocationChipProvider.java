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

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.feed.maps.MapScreen;
import ch.deletescape.lawnchair.location.LocationManager;
import geocode.GeoName;
import geocode.GeocoderCompat;
import geocode.ReverseGeoCode;
import kotlin.Pair;
import kotlin.Unit;

public class CurrentLocationChipProvider extends ChipProvider {
    private ReverseGeoCode degeocoder;
    private GeoName name;
    private Pair<Double, Double> loc;

    @SuppressWarnings("unused")
    public CurrentLocationChipProvider(Context context) {
        LocationManager.INSTANCE.addCallback((lt, lo) -> {
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    loc = new Pair<>(lt, lo);
                    if (degeocoder == null) {
                        degeocoder = new GeocoderCompat(context, false);
                    }
                    name = degeocoder.nearestPlace(loc.getFirst(), loc.getSecond());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return Unit.INSTANCE;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Item> getItems(Context context) {
        return name != null ? Collections.singletonList(((Supplier<Item>) () -> {
            Item item = new Item();
            item.icon = context.getDrawable(R.drawable.ic_location);
            item.title = name.name;
            item.viewClickListener = v -> {
                MapScreen screen = new MapScreen(context, getLauncherFeed(), loc.getFirst(),
                        loc.getSecond(), 12.0);
                screen.display(getLauncherFeed(),
                        LawnchairUtilsKt.getPositionOnScreen(v).getFirst() + v.getMeasuredWidth() / 2,
                        LawnchairUtilsKt.getPositionOnScreen(v).getSecond() + v.getMeasuredHeight() / 2, v);
            };
            return item;
        }).get()) : Collections.EMPTY_LIST;
    }
}

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

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import ch.deletescape.lawnchair.LawnchairApp;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController;

public class WeatherChipProvider extends ChipProvider
        implements LawnchairSmartspaceController.Listener {
    @Nullable
    private LawnchairSmartspaceController.WeatherData weather;

    public WeatherChipProvider(Context context) {
        ((LawnchairApp) context.getApplicationContext()).getSmartspace().addListener(this);
    }

    @Override
    public List<Item> getItems(Context context) {
        return weather == null ? Collections.EMPTY_LIST : Collections.singletonList(((Supplier<Item>) () -> {
            Item item = new Item();
            item.icon = new BitmapDrawable(context.getResources(), weather.getIcon());
            item.title = weather.getTitle(Utilities.getLawnchairPrefs(context).getWeatherUnit());
            return item;
        }).get());
    }

    @Override
    public void onDataUpdated(@Nullable LawnchairSmartspaceController.WeatherData weather,
                              @Nullable LawnchairSmartspaceController.CardData card) {
        this.weather = weather;
    }
}

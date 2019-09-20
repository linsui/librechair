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

package ch.deletescape.lawnchair.feed.chips;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.android.launcher3.R;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.LawnchairApp;
import ch.deletescape.lawnchair.feed.chips.alarm.AlarmChipProvider;
import ch.deletescape.lawnchair.feed.chips.battery.BatteryStatusProvider;
import ch.deletescape.lawnchair.feed.chips.calendar.UpcomingEventsProvider;

public interface ChipProvider {
    List<Item> getItems(Context context);

    public class Cache {
        private static Map<ChipProviderContainer, ChipProvider> providerCache = new HashMap<>();

        public static ChipProvider get(ChipProviderContainer container, Context context) {
            if (providerCache.containsKey(container)) {
                return providerCache.get(container);
            } else {
                try {
                    ChipProvider provider = (ChipProvider) Class.forName(
                            container.clazz).getConstructor(Context.class).newInstance(context);
                    providerCache.put(container, provider);
                    return provider;
                } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public class Item {
        public String title;
        public Runnable click;
        public Drawable icon;
    }

    public class Names {
        private static Map<Class, String> names = new LinkedHashMap<>();

        static {
            names.put(BatteryStatusProvider.class,
                    LawnchairApp.localizationContext.getString(R.string.battery_status));
            names.put(UpcomingEventsProvider.class, LawnchairApp.localizationContext.getString(
                    R.string.title_feed_provider_calendar));
            names.put(AlarmChipProvider.class,
                    LawnchairApp.localizationContext.getString(R.string.title_chip_provider_alarm));
        }

        public static List<ChipProviderContainer> getAll(Context c) {
            return names.keySet().stream().map(it -> {
                ChipProviderContainer container = new ChipProviderContainer();
                container.args = "";
                container.clazz = it.getName();
                return container;
            }).collect(Collectors.toList());
        }

        public static String getNameForClass(Class<? extends ChipProviderContainer> clazz) {
            return names.get(clazz);
        }
    }
}

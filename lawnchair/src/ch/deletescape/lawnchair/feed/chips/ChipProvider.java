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

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.android.launcher3.R;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.deletescape.lawnchair.LawnchairApp;
import ch.deletescape.lawnchair.feed.chips.alarm.AlarmChipProvider;
import ch.deletescape.lawnchair.feed.chips.battery.BatteryStatusProvider;
import ch.deletescape.lawnchair.feed.chips.calendar.UpcomingEventsProvider;
import ch.deletescape.lawnchair.feed.chips.contacts.ContactsChipProvider;
import ch.deletescape.lawnchair.feed.chips.remote.RemoteChipProvider;
import ch.deletescape.lawnchair.feed.chips.remote.RemoteChipProviderUtilities;

public abstract class ChipProvider {
    private ChipAdapter adapter;

    public abstract List<Item> getItems(Context context);

    public void acceptArguments(String args) {
    }

    public final void setAdapter(ChipAdapter adapter) {
        this.adapter = adapter;
    }

    protected final void refresh() {
        if (this.adapter != null) {
            this.adapter.rebindData();
            this.adapter.notifyDataSetChanged();
        }
    }

    public static class Cache {
        private static Map<ChipProviderContainer, ChipProvider> providerCache = new HashMap<>();

        public static ChipProvider get(ChipProviderContainer container, Context context) {
            if (providerCache.containsKey(container)) {
                return providerCache.get(container);
            } else {
                try {
                    ChipProvider provider = (ChipProvider) Class.forName(
                            container.clazz).getConstructor(Context.class).newInstance(context);
                    provider.acceptArguments(container.args);
                    providerCache.put(container, provider);
                    return provider;
                } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static class Item {
        public String title;
        public Runnable click;
        public Drawable icon;
    }

    public static class Names {
        private static Map<ChipProviderContainer, String> names = new LinkedHashMap<>();

        static {
            names.put(buildEmptyContainer(BatteryStatusProvider.class),
                    LawnchairApp.localizationContext.getString(R.string.battery_status));
            names.put(buildEmptyContainer(UpcomingEventsProvider.class),
                    LawnchairApp.localizationContext.getString(
                            R.string.title_feed_provider_calendar));
            names.put(buildEmptyContainer(AlarmChipProvider.class),
                    LawnchairApp.localizationContext.getString(R.string.title_chip_provider_alarm));
            names.put(buildEmptyContainer(ContactsChipProvider.class),
                    LawnchairApp.localizationContext.getString(
                    R.string.title_feed_provider_feed_contacts));
            for (ComponentName name : RemoteChipProviderUtilities.getRemoteChipProviders(
                    LawnchairApp.localizationContext)) {
                ChipProviderContainer container = new ChipProviderContainer();
                container.clazz = RemoteChipProvider.class.getName();
                container.args = name.flattenToString();
                try {
                    names.put(container,
                            LawnchairApp.localizationContext.getPackageManager().getServiceInfo(
                                    name, 0).loadLabel(
                                    LawnchairApp.localizationContext.getPackageManager()).toString());
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        private static ChipProviderContainer buildEmptyContainer(Class clazz) {
            ChipProviderContainer container = new ChipProviderContainer();
            container.clazz = clazz.getName();
            container.args = "";
            return container;
        }

        public static List<ChipProviderContainer> getAll(Context c) {
            return new ArrayList<>(names.keySet());
        }

        public static String getNameForClass(Class<? extends ChipProviderContainer> clazz) {
            return names.get(buildEmptyContainer(clazz));
        }

        public static String getNameForContainer(ChipProviderContainer container) {
            return names.get(container);
        }
    }
}

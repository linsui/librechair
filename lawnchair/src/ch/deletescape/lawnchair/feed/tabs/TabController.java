/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.tabs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import ch.deletescape.lawnchair.feed.FeedProvider;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public class TabController {

    private final Context context;

    public TabController(@NotNull Context context) {
        Intrinsics.checkNotNull(context, "constructor parameter context");
        this.context = context;
    }

    public final Context getContext() {
        return context;
    }

    public List<Item> getAllTabs() {
        return Collections.emptyList();
    }

    public Map<Item, List<FeedProvider>> sortFeedProviders(List<FeedProvider> providers) {
        return Collections.singletonMap(null, providers);
    }

    public static <T extends TabController> T inflate(String clazz, Context context)
            throws ClassNotFoundException, ClassCastException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<? extends TabController> clazs = (Class<? extends TabController>) Class
                .forName(clazz);
        Constructor<? extends TabController> constructor = clazs
                .getDeclaredConstructor(Context.class);
        return (T) constructor.newInstance(context);
    }

    public static List<Class<? extends
            TabController>> getAvailableControllers() {
        return Arrays.asList(TabController.class,
                CategorizedTabbingController.class,
                ProviderTabbingController.class,
                CustomTabbingController.class);
    }

    public static class Item {

        public Drawable icon;
        public String title;

        public Item() {
            this(null, null);
        }

        public Item(Drawable icon, String title) {
            this.icon = icon;
            this.title = title;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Item item = (Item) o;
            return Objects.equals(icon, item.icon) &&
                    Objects.equals(title, item.title);
        }

        @Override
        public int hashCode() {
            return Objects.hash(icon, title);
        }
    }
}

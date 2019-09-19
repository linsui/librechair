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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ChipProvider {
    List<Item> getItems(Context context);

    public class Item {
        public String title;
        public Runnable click;
        public Drawable icon;
    }

    public class Names {
        private static Map<Class, String> names = new LinkedHashMap<>();

        public static List<ChipProviderContainer> getAll(Context c) {
            return Collections.emptyList();
        }

        public static String getNameForClass(Class<? extends ChipProviderContainer> clazz) {
            return names.get(clazz);
        }
    }
}

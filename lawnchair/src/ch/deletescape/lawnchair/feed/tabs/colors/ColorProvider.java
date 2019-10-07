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

package ch.deletescape.lawnchair.feed.tabs.colors;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;

import com.android.launcher3.R;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import ch.deletescape.lawnchair.LawnchairUtilsKt;

public class ColorProvider {
    @NotNull
    public List<Integer> getColors(Context context) {
        return Arrays.asList(LawnchairUtilsKt.getColorEngineAccent(context), Color.parseColor("#DB4437"),
                Color.parseColor("#F4B400"), Color.parseColor("#0F9D58"));
    }

    public static final class Companion {

        public static final List<Pair<Class<? extends ColorProvider>, Integer>> all =
                Arrays.asList(new Pair<>(ColorProvider.class, R.string.theme_default),
                        new Pair<>(AccentProvider.class, R.string.lawnchair_accent),
                        new Pair<>(CustomizableColorProvider.class, R.string.title_color_provider_customizable));

        public static ColorProvider inflate(Class<? extends ColorProvider> clazz) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
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

package ch.deletescape.lawnchair.reflection;

import android.content.Context;
import ch.deletescape.lawnchair.feed.AbstractFeedSortingAlgorithm;
import ch.deletescape.lawnchair.feed.FeedProvider;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ReflectionUtils {
    public static FeedProvider inflateFeedProvider(String clazz, Context context)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class clazs = Class.forName(clazz);
        Constructor<FeedProvider> method = clazs.getConstructor(Context.class);
        return method.newInstance(context);
    }

    public static AbstractFeedSortingAlgorithm inflateSortingAlgorithm(String clazz)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class clazs = Class.forName(clazz);
        Constructor<AbstractFeedSortingAlgorithm> method = clazs.getConstructor();
        return method.newInstance();
    }

    public static <R, T> R getObject(Class<T> clazz, T object, String field)
            throws IllegalAccessException, NoSuchFieldException {
        Field field1 = clazz.getField(field);
        if (!field1.isAccessible()) {
            field1.setAccessible(true);
        }
        return (R) field1.get(object);
    }
}

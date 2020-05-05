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

import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import ch.deletescape.lawnchair.feed.AbstractFeedSortingAlgorithm;
import ch.deletescape.lawnchair.feed.FeedProvider;

@SuppressWarnings("unchecked")
public class ReflectionUtils {

    public static FeedProvider inflateFeedProvider(String clazz, Context context,
                                                   @Nullable Map<String, String> arguments)
            throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        Class clazs = Class.forName(clazz);
        Constructor<FeedProvider> method;
        boolean isArguments = false;
        try {
            method = arguments == null || arguments.isEmpty() ? clazs.getConstructor(Context.class)
                    : clazs.getConstructor(Context.class, Map.class);
            isArguments = !(arguments == null || arguments.isEmpty());
        } catch (NoSuchMethodException e) {
            method = clazs.getConstructor(Context.class, Map.class);
            isArguments = true;
        }
        return isArguments ? method.newInstance(context, arguments) : method.newInstance(context);
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

    public static Class<?> getCallingClass() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(
                    ReflectionUtils.class.getName()) && ste.getClassName().indexOf(
                    "java.lang.Thread") != 0) {
                try {
                    return Class.forName(ste.getClassName());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}

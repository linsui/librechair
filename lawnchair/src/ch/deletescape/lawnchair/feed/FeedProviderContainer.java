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

package ch.deletescape.lawnchair.feed;

import android.content.Context;
import androidx.annotation.Nullable;
import ch.deletescape.lawnchair.reflection.ReflectionUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class FeedProviderContainer {

    public final String clazz;
    @NotNull
    public Map<String, String> arguments = Collections.emptyMap();
    @Nullable
    public String name;

    public FeedProviderContainer(String clazz,
            @Nullable Map<String, String> arguments) {
        this.clazz = clazz;
        if (arguments == null) {
            this.arguments = Collections.emptyMap();
        }
        ;
    }

    public FeedProviderContainer(String clazz, @Nullable Map<String, String> arguments,
            @Nullable String overridenName) {
        this(clazz, arguments);
        this.name = overridenName;
    }

    public FeedProvider instantiate(Context context)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return instantiate(context, arguments);
    }

    public FeedProvider instantiate(Context context, Map<String, String> arguments)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        FeedProvider feedProvider = ReflectionUtils.inflateFeedProvider(clazz, context, arguments);
        feedProvider.setContainer(this);
        return feedProvider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeedProviderContainer that = (FeedProviderContainer) o;
        return Objects.equals(clazz, that.clazz) &&
                Objects.equals(arguments, that.arguments) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, arguments, name);
    }
}

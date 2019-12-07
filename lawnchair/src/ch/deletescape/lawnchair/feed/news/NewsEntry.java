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

package ch.deletescape.lawnchair.feed.news;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@Entity
public class NewsEntry {
    @PrimaryKey
    public int order;
    public long cachedIn;
    public String thumbnail;
    public String title;
    public String content;
    public String url;
    public List<String> categories;
    public Date date;
    public Date lastUpdate;

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static List<String> stringToStringList(String value) {
        if (value == null) {
            return null;
        }
        List<String> strings = new ArrayList<>();
        new JsonParser().parse(value).getAsJsonArray().iterator().forEachRemaining(je -> strings.add(je.getAsString()));
        return strings;
    }

    @TypeConverter
    public static String stringListToString(List<String> value) {
        if (value == null) {
            return null;
        }
        JsonArray array = new JsonArray();
        value.forEach(array::add);
        return array.toString();
    }
}

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

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.HashMap;

@Database(entities = {NewsEntry.class}, version = 2)
@TypeConverters({NewsEntry.class})
public abstract class NewsDb extends RoomDatabase {
    private static volatile HashMap<String, NewsDb> databases = new HashMap<>();

    public synchronized static NewsDb getDatabase(Context context, String id) {
        NewsDb db;
        return (db = databases.get(id)) != null ? db : build(context, id);
    }

    private synchronized static NewsDb build(Context context, String id) {
        NewsDb db = Room.databaseBuilder(context, NewsDb.class, id)
                .fallbackToDestructiveMigration()
                .enableMultiInstanceInvalidation()
                .build();
        databases.put(id, db);
        return db;
    }

    public abstract NewsDao open();
}

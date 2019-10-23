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

package ch.deletescape.lawnchair.persistence.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = {StringEntry.class})
public abstract class StringDatabase extends RoomDatabase {
    private static volatile StringDatabase sInstance;
    private static final Object INSTANTIATION_LOCK = new Object();

    public static StringDatabase getInstance(Context context) {
        StringDatabase stringDatabase = sInstance;
        if (stringDatabase != null) {
            synchronized (INSTANTIATION_LOCK) {
                sInstance = stringDatabase;
                return sInstance;
            }
        } else {
            synchronized (INSTANTIATION_LOCK) {
                StringDatabase s2l = sInstance;
                if (s2l != null) {
                    return s2l;
                } else {
                    s2l = Room.databaseBuilder(context, StringDatabase.class, "strpref")
                            .allowMainThreadQueries()
                            .enableMultiInstanceInvalidation()
                            .build();
                    sInstance = s2l;
                    return s2l;
                }
            }
        }
    }

    public abstract StringDao dao();
}

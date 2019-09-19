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

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ChipProviderContainer.class}, version = 1)
public abstract class ChipDatabase extends RoomDatabase {
    public static final class Holder {
        private static ChipDatabase sInstance;
        public static ChipDatabase getInstance(Context context) {
            return sInstance != null ? sInstance : (sInstance = Room.databaseBuilder(context,
                    ChipDatabase.class, "feed_chips")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build());
        }
    }

    public abstract ChipDao dao();
}

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

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@SuppressWarnings("WeakerAccess")
@Dao
public abstract class StringDao {
    @Insert
    protected abstract void addString(StringEntry entry);

    @Query("select * from stringentry where `key` like :key limit 1")
    protected abstract StringEntry get(String key);

    @Query("update stringentry set value = :value where `key` like :key")
    protected abstract void set(String key, String value);

    public void put(String key, String value) {
        if (get(key) == null) {
            addString(new StringEntry(key, value));
        } else {
            set(key, value);
        }
    }

    public String getSafe(String key) {
        return get(key) != null ? get(key).value : null;
    }
}

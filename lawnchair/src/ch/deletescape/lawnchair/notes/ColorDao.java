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

package ch.deletescape.lawnchair.notes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public abstract class ColorDao {
    @Insert
    public abstract void insert(TabDatabaseEntry... entries);

    @Query("select * from tabdatabaseentry")
    public abstract List<TabDatabaseEntry> getAll();

    @Query("select * from tabdatabaseentry where color like :color limit 1")
    public abstract TabDatabaseEntry findEntryForColor(int color);

    @Query("update tabdatabaseentry set color_name = :name where color like :color")
    public abstract void updateTabName(int color, String name);

    @Query("delete from tabdatabaseentry where color like :color")
    public abstract void remove(int color);

    @Delete
    public abstract void remove(TabDatabaseEntry entry);
}

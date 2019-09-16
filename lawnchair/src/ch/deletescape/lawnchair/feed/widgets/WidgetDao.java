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

package ch.deletescape.lawnchair.feed.widgets;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.jetbrains.annotations.Nullable;

import java.util.List;

@Dao
public abstract class WidgetDao {
    @Insert
    public abstract void addWidget(Widget widget);

    @Query("select * from widget order by entryOrder")
    public abstract List<Widget> getAll();

    @Query("select * from widget where id like :id limit 1")
    public abstract Widget findWidgetById(int id);

    @Query("delete from widget")
    public abstract void removeAll();

    @Query("delete from widget where id like :id")
    public abstract void removeWidget(int id);

    @Query("update widget set showCardTitle = :argshowCardTitle where id like :id")
    public abstract void setShowCardTitle(int id, boolean argshowCardTitle);

    @Query("update widget set sortable = :argsortable where id like :id")
    public abstract void setSortable(int id, boolean argsortable);

    @Query("update widget set customCardTitle = :argtitle where id like :id")
    public abstract void setCardTitle(int id, String argtitle);

    @Query("update widget set raiseCard = :argraiseCard where id like :id")
    public abstract void setRaised(int id, boolean argraiseCard);

    @Query("update widget set height = :argheight where id like :id")
    public abstract void setHeight(int id, @Nullable Integer argheight);

    @Query("update widget set entryOrder = :order where id like :id")
    public abstract void setOrder(int id, int order);
}

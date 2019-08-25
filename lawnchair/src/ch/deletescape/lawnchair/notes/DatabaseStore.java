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

import android.content.Context;
import androidx.room.Room;

public final class DatabaseStore {

    private static NoteDatabase sDbInstance;

    public static NoteDatabase getDbInstance(Context context) {
        if (sDbInstance == null) {
            return sDbInstance = Room.databaseBuilder(context, NoteDatabase.class, "notes")
                    .enableMultiInstanceInvalidation().allowMainThreadQueries().build();
        } else {
            return sDbInstance;
        }
    }

    public static NoteDao getAccessObject(Context context) {
        return getDbInstance(context).access();
    }
}

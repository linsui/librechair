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

package ch.deletescape.lawnchair.habit;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class PackageHabit implements
        Habit<String, String, String> {

    /*
     * This class is guaranteed to remain constant
     * or the usage data will be cleared
     */

    private final String last;
    private final String now;
    private final String later;
    private final transient Context context;

    public PackageHabit(String last, String now, String later, Context context) {
        this.last = last;
        this.now = now;
        this.later = later;
        this.context = context;
    }

    public PackageHabit(String serialized, Context context) throws JsonParseException {
        Gson gson = new Gson();
        PackageHabit habit = gson.fromJson(serialized, PackageHabit.class);
        this.last = habit.last;
        this.now = habit.now;
        this.later = habit.later;
        this.context = habit.context;
    }

    @Override
    public String last() {
        return null;
    }

    @Override
    public String now() {
        return null;
    }

    @Override
    public String later() {
        return null;
    }

    @Override
    public int priority() {
        return 0;
    }
}

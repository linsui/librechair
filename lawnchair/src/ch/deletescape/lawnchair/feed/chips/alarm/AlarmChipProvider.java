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

package ch.deletescape.lawnchair.feed.chips.alarm;

import android.app.AlarmManager;
import android.content.Context;

import com.android.launcher3.R;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.chips.ChipProvider;

public class AlarmChipProvider extends ChipProvider {

    public AlarmChipProvider(Context ignored) {

    }

    @Override
    public List<Item> getItems(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo info = manager.getNextAlarmClock();
        if (info != null) {
            Item item = new Item();
            item.icon = context.getDrawable(R.drawable.ic_alarm_on_black_24dp);
            item.title = LawnchairUtilsKt.formatTime(new Date(info.getTriggerTime()), context);
            return Collections.singletonList(item);
        }
        return null;
    }
}

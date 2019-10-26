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

package ch.deletescape.lawnchair.feed;

import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.android.launcher3.R;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.deletescape.lawnchair.LawnchairUtilsKt;

public class AlarmEventProvider extends FeedProvider {

    public AlarmEventProvider(Context c) {
        super(c);
    }

    @Override
    public void onFeedShown() {

    }

    @Override
    public void onFeedHidden() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public boolean isActionFree() {
        return true;
    }

    @Override
    public boolean isVolatile() {
        return true;
    }

    @Override
    public List<Card> getCards() {
        AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (manager == null) {
            return Collections.EMPTY_LIST;
        }
        AlarmClockInfo info = manager.getNextAlarmClock();
        if (info != null) {
            Drawable alarm = getContext().getDrawable(R.drawable.ic_alarm_on_black_24dp);
            alarm = LawnchairUtilsKt.tint(alarm,
                    FeedAdapter.Companion.getOverrideColor(getContext()));
            return Collections.singletonList(new Card(alarm,
                    LawnchairUtilsKt.formatTime(new Date(info.getTriggerTime()), getContext()),
                    parent -> new View(getContext()),
                    Card.RAISE | Card.TEXT_ONLY,
                    "nosort,top", "alarmEvent".hashCode()));
        }
        return Collections.emptyList();
    }
}

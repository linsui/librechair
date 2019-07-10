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

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;

public class UsageStatsScoringProvider implements ScoringProvider<UsageStats> {

    private UsageStatsManager manager;

    public UsageStatsScoringProvider(UsageStatsManager manager) {
        this.manager = manager;
    }

    @Override
    public int score(UsageStats usageStats) {
        int baseScore = 10000;
        int age = (int) (System.currentTimeMillis() - usageStats.getLastTimeUsed()) / 1000;
        baseScore -= manager.isAppInactive(usageStats.getPackageName()) ? age * 2 : age;
        // baseScore -= usageStats.describeContents();
        return 0;
    }
}

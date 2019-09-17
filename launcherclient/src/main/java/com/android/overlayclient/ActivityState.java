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

package com.android.overlayclient;

public class ActivityState {
    private static int FLAG_INITIALIZED = 1;
    private static int FLAG_ACTIVITY_IN_FOREGROUND = 1 << 1;

    private boolean connected;
    private boolean activityInForeground;


    public int toMask() {
        return connected && !activityInForeground ? FLAG_INITIALIZED :
                connected && activityInForeground ? FLAG_INITIALIZED | FLAG_ACTIVITY_IN_FOREGROUND : 0;
    }

    public int onStart() {
        connected = true;
        return toMask();
    }

    public int onPause() {
        connected = false;
        return toMask();
    }

    public int onResume() {
        connected = true;
        activityInForeground = true;
        return toMask();
    }

    public int onStop() {
        connected = false;
        return toMask();
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isActivityInForeground() {
        return activityInForeground;
    }

    public void setActivityInForeground(boolean activityInForeground) {
        this.activityInForeground = activityInForeground;
    }
}

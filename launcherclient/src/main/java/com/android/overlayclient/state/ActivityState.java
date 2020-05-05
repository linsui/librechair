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

package com.android.overlayclient.state;

import java.util.function.Consumer;

public class ActivityState {
    private static int FLAG_INITIALIZED = 1;
    private static int FLAG_ACTIVITY_IN_FOREGROUND = 1 << 1;

    private boolean connected;
    private boolean activityInForeground;

    private Consumer<ActivityState> onChangeListener;

    public int importMask(int mask) {
        if ((mask & FLAG_INITIALIZED) != 0) {
            if (!connected) {
                onStart();
            }
        } else {
            if (connected) {
                onStop();
            }
        }
        if ((mask & FLAG_ACTIVITY_IN_FOREGROUND) != 0) {
            if (!activityInForeground) {
                onResume();
            }
        } else {
            if (activityInForeground) {
                onPause();
            }
        }
        return toMask();
    }

    public int toMask() {
        return connected && !activityInForeground ? FLAG_INITIALIZED :
                connected ? FLAG_INITIALIZED | FLAG_ACTIVITY_IN_FOREGROUND : 0;
    }

    public int onStart() {
        connected = true;
        if (onChangeListener != null) {
            onChangeListener.accept(this);
        }
        return toMask();
    }

    public int onPause() {
        connected = false;
        if (onChangeListener != null) {
            onChangeListener.accept(this);
        }
        return toMask();
    }

    public int onResume() {
        connected = true;
        activityInForeground = true;
        if (onChangeListener != null) {
            onChangeListener.accept(this);
        }
        return toMask();
    }

    public int onStop() {
        connected = false;
        if (onChangeListener != null) {
            onChangeListener.accept(this);
        }
        return toMask();
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
        if (onChangeListener != null) {
            onChangeListener.accept(this);
        }
    }

    public boolean isActivityInForeground() {
        return activityInForeground;
    }

    public void setActivityInForeground(boolean activityInForeground) {
        this.activityInForeground = activityInForeground;
        if (onChangeListener != null) {
            onChangeListener.accept(this);
        }
    }

    public void setOnChangeListener(
            Consumer<ActivityState> onChangeListener) {
        this.onChangeListener = onChangeListener;
    }
}

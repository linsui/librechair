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

package ch.deletescape.lawnchair.persistence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.android.launcher3.BuildConfig;

import java.util.HashSet;
import java.util.Set;

public final class InvalidationTracker {
    private final static Set<String> CURRENTLY_INVALIDATED = new HashSet<>();

    private InvalidationTracker() {
        throw new RuntimeException();
    }

    public static void attachToContext(Context context) {
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                CURRENTLY_INVALIDATED.add(intent.getStringExtra("invalidated"));
            }
        }, new IntentFilter(BuildConfig.APPLICATION_ID + ".PERSISTENCE_INVALIDATED"));
    }

    static boolean isInvalidated(String key) {
        return CURRENTLY_INVALIDATED.contains(key);
    }

    static void validate(String key) {
        CURRENTLY_INVALIDATED.remove(key);
    }

    static void invalidate(String key, Context context) {
        context.sendBroadcast(new Intent(BuildConfig.APPLICATION_ID + ".PERSISTENCE_INVALIDATED")
                .putExtra("invalidated", key));
    }
}

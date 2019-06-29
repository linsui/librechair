/*
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
/*
 * This class was adapted from smali files a.smali and a$1.smali
 */
package com.google.android.apps.nexuslauncher.reflection;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.PendingIntent.OnFinished;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.MutableLong;
import com.android.launcher3.util.Preconditions;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class a implements b {
    private final long T;

    public a(Context var1) {
        this.T = this.initRecordedTime(var1, 1);
    }

    public final long g() {
        return this.T;
    }

    protected long getAbsoluteBootTime() {
        return Calendar.getInstance().getTimeInMillis() - SystemClock.elapsedRealtime();
    }

    protected long initRecordedTime(Context var1, int var2) {
        Intent var3 = new Intent("com.google.android.apps.nexuslauncher.reflection.ACTION_BOOT_CYCLE");
        PendingIntent var4 = PendingIntent.getBroadcast(var1, var2, var3, 536870912);
        final MutableLong var5 = new MutableLong(this.getAbsoluteBootTime());
        if (var4 != null) {
            try {
                Preconditions.assertNonUiThread();
                final CountDownLatch var6 = new CountDownLatch(1);
                OnFinished var7 = new OnFinished() {
                    public void onSendFinished(PendingIntent var1, Intent var2, int var3, String var4, Bundle var5x) {
                        var5.value = var2.getLongExtra("time", var5.value);
                        var6.countDown();
                    }
                };
                Handler var8 = new Handler(Looper.getMainLooper());
                var4.send(var2, var7, var8);
                var6.await(1L, TimeUnit.SECONDS);
                long var9 = var5.value;
                return var9;
            } catch (InterruptedException | CanceledException var11) {
            }
        }

        var3.putExtra("time", var5.value);
        PendingIntent var12 = PendingIntent.getBroadcast(var1, var2, var3, 134217728);
        ((AlarmManager)var1.getSystemService("alarm")).set(1, 9223372036854775807L, var12);
        return var5.value;
    }
}

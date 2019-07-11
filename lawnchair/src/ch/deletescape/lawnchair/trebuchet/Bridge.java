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

package ch.deletescape.lawnchair.trebuchet;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import androidx.annotation.Nullable;
import com.android.launcher3.BuildConfig;
import com.android.systemui.shared.recents.IOverviewProxy;
import com.android.systemui.shared.recents.IOverviewProxy.Stub;
import com.android.systemui.shared.recents.ISystemUiProxy;

public class Bridge extends Service {

    private IOverviewProxy binder;
    private IOverviewProxy.Stub bridge = new Stub() {
        @Override
        public void onBind(ISystemUiProxy sysUiProxy) throws RemoteException {
            binder.onBind(sysUiProxy);
        }

        @Override
        public void onPreMotionEvent(int downHitTarget) throws RemoteException {
            binder.onPreMotionEvent(downHitTarget);
        }

        @Override
        public void onMotionEvent(MotionEvent event) throws RemoteException {
            binder.onMotionEvent(event);
        }

        @Override
        public void onQuickScrubStart() throws RemoteException {
            binder.onQuickScrubStart();
        }

        @Override
        public void onQuickScrubEnd() throws RemoteException {
            binder.onQuickScrubEnd();
        }

        @Override
        public void onQuickScrubProgress(float progress) throws RemoteException {
            binder.onQuickScrubProgress(progress);
        }

        @Override
        public void onOverviewToggle() throws RemoteException {
            binder.onOverviewToggle();
        }

        @Override
        public void onOverviewShown(boolean triggeredFromAltTab) throws RemoteException {
            binder.onOverviewShown(triggeredFromAltTab);
        }

        @Override
        public void onOverviewHidden(boolean triggeredFromAltTab, boolean triggeredFromHomeKey)
                throws RemoteException {
            binder.onOverviewHidden(triggeredFromAltTab, triggeredFromHomeKey);

        }

        @Override
        public void onQuickStep(MotionEvent event) throws RemoteException {
            binder.onQuickStep(event);
        }

        @Override
        public void onTip(int actionType, int viewType) throws RemoteException {
            binder.onTip(actionType, viewType);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(getClass().getCanonicalName(), "onBind: beginning bridge");
        Intent service = new Intent();
        Log.d(getClass().getCanonicalName(), "onBind: created intent");
        service.setComponent(new ComponentName(BuildConfig.APPLICATION_ID,
                "com.android.quickstep.TouchInteractionService"));
        startService(service);
        Log.d(getClass().getCanonicalName(), "onBind: set component and requesting bind");
        bindService(service, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = (IOverviewProxy) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, 0);
        Log.d(getClass().getCanonicalName(), "onBind: returning binder");
        return bridge;
    }
}

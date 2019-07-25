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
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import androidx.annotation.Nullable;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import com.android.launcher3.BuildConfig;
import com.android.quickstep.TouchInteractionService;
import com.android.systemui.shared.recents.IOverviewProxy;
import com.android.systemui.shared.recents.IOverviewProxy.Stub;
import com.android.systemui.shared.recents.ISystemUiProxy;
import com.android.systemui.shared.system.GraphicBufferCompat;
import java.util.concurrent.Executors;

public class Bridge extends Service {

    private Handler retryHandler;
    private IOverviewProxy binder;
    private IOverviewProxy.Stub bridge = new Stub() {
        @Override
        public void onBind(ISystemUiProxy sysUiProxy) throws RemoteException {
            if (binder == null) {
                Log.i(getClass().getName(), "onBind: binder is still null; retrying bind in 50ms");
                retryHandler.postDelayed(() -> {
                    try {
                        this.onBind(sysUiProxy);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }, 50);
            } else {
                binder.onBind((ISystemUiProxy) new ISystemUiProxy.Stub() {
                    @Override
                    public GraphicBufferCompat screenshot(Rect sourceCrop, int width, int height,
                            int minLayer, int maxLayer, boolean useIdentityTransform, int rotation)
                            throws RemoteException {
                        return sysUiProxy.screenshot(sourceCrop, width, height, minLayer, maxLayer,
                                useIdentityTransform, rotation);
                    }

                    @Override
                    public void startScreenPinning(int taskId) throws RemoteException {
                        sysUiProxy.startScreenPinning(taskId);
                    }

                    @Override
                    public void setInteractionState(int flags) throws RemoteException {
                        sysUiProxy.startScreenPinning(flags);
                    }

                    @Override
                    public void onSplitScreenInvoked() throws RemoteException {
                        sysUiProxy.onSplitScreenInvoked();
                    }

                    @Override
                    public void onOverviewShown(boolean fromHome) throws RemoteException {
                        sysUiProxy.onOverviewShown(fromHome);
                    }

                    @Override
                    public Rect getNonMinimizedSplitScreenSecondaryBounds() throws RemoteException {
                        return sysUiProxy.getNonMinimizedSplitScreenSecondaryBounds();
                    }

                    @Override
                    public void setBackButtonAlpha(float alpha, boolean animate)
                            throws RemoteException {
                        sysUiProxy.setBackButtonAlpha(alpha, animate);
                    }
                }.asBinder());
            }
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

    public Bridge() {
        HandlerThread thread = new HandlerThread("BridgeRetryHandler");
        thread.start();
        retryHandler = new Handler(thread.getLooper());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(getClass().getCanonicalName(), "onBind: beginning bridge");
        Intent service = new Intent();
        Intent fallbackService = new Intent(this, TouchInteractionService.class);
        Log.d(getClass().getCanonicalName(), "onBind: created intent");
        if (LawnchairUtilsKt.getLawnchairPrefs(this).getQuickstep()
                .equals(BuildConfig.APPLICATION_ID)) {
            service = new Intent(this, TouchInteractionService.class);
        } else {
            service.setAction("android.intent.action.QUICKSTEP_SERVICE");
        }
        service.setPackage(LawnchairUtilsKt.getLawnchairPrefs(this).getQuickstep());
        Log.d(getClass().getCanonicalName(), "onBind: set component and requesting bind");
        startService(intent);
        Intent finalService = service;
        Executors.newSingleThreadExecutor().submit(() -> {
            bindService(finalService, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    binder = (IOverviewProxy) service;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            }, Context.BIND_AUTO_CREATE);
        });
        Log.d(getClass().getCanonicalName(), "onBind: returning binder");
        return bridge;
    }
}

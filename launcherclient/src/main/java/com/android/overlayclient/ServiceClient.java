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

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.view.WindowManager;

import com.google.android.libraries.launcherclient.ILauncherOverlay;
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class ServiceClient extends ILauncherOverlayCallback.Stub
        implements OpenableOverscrollClient, DisconnectableOverscrollClient,
        SearchableOverscrollClient, DurationOpenableOverscrollClient, Handler.Callback {

    public static final int MESSAGE_CHANGE_SCROLL = 2;
    public static final int MESSAGE_CHANGE_STATUS = 4;

    private static final int ANIMATE = 1;
    private static final int ANIMATE_DURATION_LSHIFT = 2;

    private static final int DEFAULT_ANIMATION_DURATION =
            (int) TimeUnit.SECONDS.toMillis(1) / 4;

    private ILauncherOverlay overlay;
    private Activity boundActivity;
    private ServiceFactory factory;
    private WindowManager.LayoutParams params;
    private Bundle additionalParams;
    private int apiVersion;
    private OverlayCallback callback;
    private ArrayList<Consumer<ILauncherOverlay>> overlayChangeListeners;
    private int windowShift;
    private ActivityState activityState;
    private Handler mUIHandler;

    public ServiceClient(Activity boundActivity,
                         ServiceFactory factory, OverlayCallback callback,
                         Runnable disconnectCallback, Runnable connectCallback) {
        this.boundActivity = boundActivity;
        this.factory = factory;
        this.apiVersion = factory.getApiVersion();
        this.callback = callback;
        this.overlayChangeListeners = new ArrayList<>();
        factory.setChangeListener(l3overlay -> {
            this.overlay = l3overlay;
            ((ArrayList<Consumer<ILauncherOverlay>>) overlayChangeListeners.clone()).forEach(
                    consumer -> consumer.accept(l3overlay));
            if (params != null) {
                acceptLayoutParams(params);
            } else {
                acceptLayoutParams(params = boundActivity.getWindow().getAttributes());
            }
            if (l3overlay == null) {
                disconnectCallback.run();
                new Handler(boundActivity.getMainLooper()).postDelayed(this::reconnect,
                        TimeUnit.SECONDS.toMillis(2));
            } else {
                connectCallback.run();
            }
        });
        activityState = new ActivityState();
        factory.connect();
        mUIHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public void openOverlay(boolean animate) {
        if (overlay != null) {
            try {
                overlay.openOverlay(getAnimationFlags(animate, DEFAULT_ANIMATION_DURATION));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void closeOverlay(boolean animate) {
        if (overlay != null) {
            try {
                overlay.closeOverlay(getAnimationFlags(animate, DEFAULT_ANIMATION_DURATION));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void openOverlay(int duration) {
        if (overlay != null) {
            try {
                overlay.openOverlay(getAnimationFlags(true, duration));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void closeOverlay(int duration) {
        if (overlay != null) {
            try {
                overlay.closeOverlay(getAnimationFlags(true, duration));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        acceptLayoutParams(boundActivity.getWindow().getAttributes());
    }

    @Override
    public void onDetachedFromWindow() {
        acceptLayoutParams(null);
    }

    @Override
    public void onResume() {
        if (overlay != null) {
            try {
                if (apiVersion >= 4) {
                    overlay.setActivityState(activityState.onResume());
                } else {
                    overlay.onResume();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            overlayChangeListeners.add(new Consumer<ILauncherOverlay>() {
                @Override
                public void accept(ILauncherOverlay iLauncherOverlay) {
                    if (iLauncherOverlay != null) {
                        try {
                            if (apiVersion >= 4) {
                                iLauncherOverlay.setActivityState(activityState.onResume());
                            } else {
                                iLauncherOverlay.onResume();
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (ServiceClient.this) {
                        overlayChangeListeners.remove(this);
                    }
                }
            });
        }
    }

    @Override
    public void onPause() {
        if (overlay != null) {
            try {
                if (apiVersion >= 4) {
                    overlay.setActivityState(activityState.onPause());
                } else {
                    overlay.onPause();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            overlayChangeListeners.add(new Consumer<ILauncherOverlay>() {
                @Override
                public void accept(ILauncherOverlay iLauncherOverlay) {
                    if (iLauncherOverlay != null) {
                        try {
                            if (apiVersion >= 4) {
                                iLauncherOverlay.setActivityState(activityState.onPause());
                            } else {
                                iLauncherOverlay.onPause();
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (ServiceClient.this) {
                        overlayChangeListeners.remove(this);
                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        if (overlay != null) {
            try {
                if (apiVersion >= 4) {
                    overlay.setActivityState(activityState.onStart());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            overlayChangeListeners.add(new Consumer<ILauncherOverlay>() {
                @Override
                public void accept(ILauncherOverlay iLauncherOverlay) {
                    if (iLauncherOverlay != null) {
                        if (apiVersion >= 4) {
                            try {
                                iLauncherOverlay.setActivityState(activityState.onStart());
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    synchronized (ServiceClient.this) {
                        overlayChangeListeners.remove(this);
                    }
                }
            });
        }
    }

    @Override
    public void onStop() {
        if (overlay != null) {
            try {
                if (apiVersion >= 4) {
                    overlay.setActivityState(activityState.onStop());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            overlayChangeListeners.add(new Consumer<ILauncherOverlay>() {
                @Override
                public void accept(ILauncherOverlay iLauncherOverlay) {
                    if (iLauncherOverlay != null) {
                        try {
                            if (apiVersion >= 4) {
                                iLauncherOverlay.setActivityState(activityState.onStop());
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (ServiceClient.this) {
                        overlayChangeListeners.remove(this);
                    }
                }
            });
        }
    }

    @Override
    public void acceptLayoutParams(WindowManager.LayoutParams params) {
        this.params = params;
        if (overlay != null && params == null) {
            try {
                overlay.windowDetached(boundActivity.isChangingConfigurations());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (overlay != null) {
            configure();
        } else {
            overlayChangeListeners.add(new Consumer<ILauncherOverlay>() {
                @Override
                public void accept(ILauncherOverlay iLauncherOverlay) {
                    configure();
                    synchronized (ServiceClient.this) {
                        overlayChangeListeners.remove(this);
                    }
                }
            });
        }
    }

    @Override
    public void startScroll() {
        if (overlay != null) {
            try {
                overlay.startScroll();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onScroll(float percentage) {
        if (overlay != null) {
            try {
                overlay.onScroll(percentage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopScroll() {
        if (overlay != null) {
            try {
                overlay.endScroll();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void reconnect() {
        factory.connect();
    }

    @Override
    public void disconnect() {
        overlay = null;
        factory.disconnect();
    }

    public void putAdditionalParams(Bundle params) {
        additionalParams = params;
        if (apiVersion >= 7) {
            configure();
        }
    }

    private void configure() {
        if (overlay != null) {
            try {
                Point p = new Point();
                boundActivity.getWindowManager().getDefaultDisplay().getRealSize(p);
                windowShift = Math.max(p.x, p.y);
                if (apiVersion < 3) {
                    overlay.windowAttached(boundActivity.getWindow().getAttributes(),
                            this, 3);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("layout_params",
                            params);
                    bundle.putParcelable("configuration",
                            boundActivity.getResources().getConfiguration());
                    bundle.putInt("client_options", 3);
                    if (additionalParams != null) {
                        bundle.putAll(additionalParams);
                    }
                    overlay.windowAttached2(bundle, this);
                }
                if (apiVersion >= 4) {
                    overlay.setActivityState(activityState.toMask());
                } else if (activityState.isActivityInForeground()) {
                    overlay.onResume();
                } else {
                    overlay.onPause();
                }
            } catch (RemoteException ignored) {
            }
        }
    }

    @Override
    public boolean startSearch(byte[] options, Bundle parameters) {
        if (overlay != null && apiVersion >= 6) {
            try {
                return overlay.startSearch(options, parameters);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    protected ILauncherOverlay getOverlay() {
        return overlay;
    }

    @Override
    public void overlayScrollChanged(float progress) throws RemoteException {
        mUIHandler.removeMessages(MESSAGE_CHANGE_SCROLL);
        mUIHandler.obtainMessage(MESSAGE_CHANGE_SCROLL, progress).sendToTarget();
    }

    @Override
    public void overlayStatusChanged(int status) throws RemoteException {
        mUIHandler.obtainMessage(MESSAGE_CHANGE_STATUS, status, 0);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_CHANGE_SCROLL:
                if (overlay != null) {
                    boundActivity.runOnUiThread(
                            () -> callback.overlayScrollChanged((float) msg.obj));
                }
                return true;
            case 3:
                WindowManager.LayoutParams attrs = params;
                if ((boolean) msg.obj) {
                    attrs.x = windowShift;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                } else {
                    attrs.x = windowShift;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                }
                boundActivity.getWindowManager().updateViewLayout(
                        boundActivity.getWindow().getDecorView(), attrs);
                return true;
            case MESSAGE_CHANGE_STATUS:
                if ((msg.arg1 & 1) != 0) {
                    overlay = null;
                }
                if (callback instanceof PersistableScrollCallback) {
                    ((PersistableScrollCallback) callback).setPersistentFlags(msg.arg1);
                }
                return true;
            default:
                return false;
        }
    }

    private static int getAnimationFlags(boolean animate, int duration) {
        return !animate ? 0 : ANIMATE | duration << ANIMATE_DURATION_LSHIFT;
    }
}

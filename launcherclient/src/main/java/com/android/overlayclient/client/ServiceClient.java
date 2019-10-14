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

package com.android.overlayclient.client;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.view.WindowManager;

import com.android.overlayclient.compat.ConfigurationDelegate;
import com.android.overlayclient.state.ActivityState;
import com.android.overlayclient.state.ServiceState;
import com.google.android.libraries.launcherclient.ILauncherOverlay;
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class ServiceClient extends ILauncherOverlayCallback.Stub
        implements DisconnectableOverscrollClient,
        SearchClient, DurationOpenableOverscrollClient, Handler.Callback {

    private static final int MESSAGE_CHANGE_SCROLL = 2;
    private static final int MESSAGE_CHANGE_STATUS = 4;

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

    private List<ConfigurationDelegate> configurationDelegate;

    private final ServiceMode mode;
    private final ServiceState serviceState;

    public ServiceClient(Activity boundActivity,
                         ServiceFactory factory, OverlayCallback callback,
                         Runnable disconnectCallback, Runnable connectCallback, ServiceMode mode) {
        this.mode = mode;
        this.boundActivity = boundActivity;
        this.factory = factory;
        this.apiVersion = factory.getApiVersion();
        this.callback = callback;
        this.overlayChangeListeners = new ArrayList<>();
        this.serviceState = new ServiceState();
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
    public void acceptLayoutParams(WindowManager.LayoutParams ogParams) {
        if (ogParams == null) {
            this.params = null;
        } else {
            this.params = new WindowManager.LayoutParams();
            params.copyFrom(ogParams);
        }
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
        serviceState.setSearchAttached(false);
        serviceState.setOverlayAttached(false);
        factory.disconnect();
    }

    public void putAdditionalParams(Bundle params) {
        if (additionalParams == null) {
            additionalParams = params;
        } else {
            additionalParams.putAll(params);
        }
        if (apiVersion >= 7) {
            configure();
        }
    }

    public void configure() {
        if (overlay != null) {
            try {
                Point p = new Point();
                boundActivity.getWindowManager().getDefaultDisplay().getRealSize(p);
                windowShift = Math.max(p.x, p.y);
                if (apiVersion < 3) {
                    overlay.windowAttached(boundActivity.getWindow().getAttributes(),
                            this,
                            factory.supportsUnifiedConnection() ? 0 : mode == ServiceMode.SEARCH ? 2 : 3);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("layout_params",
                            params);
                    bundle.putParcelable("configuration",
                            boundActivity.getResources().getConfiguration());
                    bundle.putInt("client_options",
                            factory.supportsUnifiedConnection() ? 0 : mode == ServiceMode.SEARCH ? 2 : 3);
                    if (additionalParams != null) {
                        bundle.putAll(additionalParams);
                    }
                    if (configurationDelegate != null) {
                        configurationDelegate.forEach(delegate -> delegate.bind(bundle));
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
        if (overlay != null && apiVersion >= 6 && (factory.supportsUnifiedConnection() ||
                mode == ServiceMode.SEARCH)) {
            try {
                return overlay.startSearch(options, parameters);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (!factory.supportsUnifiedConnection() && mode != ServiceMode.SEARCH) {
            throw new IllegalStateException("this client doesn't support search");
        }
        return false;
    }

    @Override
    public void requestVoiceDetection(boolean start) {
        if ((factory.supportsUnifiedConnection() || mode == ServiceMode.SEARCH)
                && overlay != null) {
            try {
                overlay.requestVoiceDetection(start);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (mode != ServiceMode.SEARCH && !factory.supportsUnifiedConnection() && !factory.supportsUnifiedConnection()) {
            throw new IllegalStateException("this client doesn't support search");
        }
    }

    @Override
    public String getVoiceSearchLanguage() {
        if ((factory.supportsUnifiedConnection() || mode == ServiceMode.SEARCH)
                && overlay != null) {
            try {
                return overlay.getVoiceSearchLanguage();
            } catch (RemoteException ignored) {
            }
        } else if (mode != ServiceMode.SEARCH && !factory.supportsUnifiedConnection()) {
            throw new IllegalStateException("this client doesn't support search");
        }
        return null;
    }

    @Override
    public boolean isVoiceDetectionRunning() {
        if ((factory.supportsUnifiedConnection() || mode == ServiceMode.SEARCH)
                && overlay != null) {
            try {
                return overlay.isVoiceDetectionRunning();
            } catch (RemoteException ignored) {
            }
        } else if (mode != ServiceMode.SEARCH && !factory.supportsUnifiedConnection()) {
            throw new IllegalStateException("this client doesn't support search");
        }
        return false;
    }

    public ILauncherOverlay getOverlay() {
        return overlay;
    }

    @Override
    public void overlayScrollChanged(float progress) {
        mUIHandler.removeMessages(MESSAGE_CHANGE_SCROLL);
        mUIHandler.obtainMessage(MESSAGE_CHANGE_SCROLL, progress).sendToTarget();
    }

    @Override
    public void overlayStatusChanged(int status) {
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
                serviceState.setOverlayAttached((msg.arg1 & ServiceState.FLAG_ATTACHED) != 0);
                serviceState.setSearchAttached((msg.arg1 & ServiceState.FLAG_SEARCH_ATTACHED) != 0);
                if (callback instanceof PersistableScrollCallback) {
                    ((PersistableScrollCallback) callback).setPersistentFlags(msg.arg1);
                }
                return true;
            default:
                return false;
        }
    }

    public void addConfigurationDelegate(ConfigurationDelegate delegate) {
        if (configurationDelegate == null) {
            configurationDelegate = new Vector<>();
        }
        delegate.attachToClient(this);
        configurationDelegate.add(delegate);
        if (overlay != null) {
            configure();
        }
    }

    private static int getAnimationFlags(boolean animate, int duration) {
        return !animate ? 0 : ANIMATE | duration << ANIMATE_DURATION_LSHIFT;
    }
}

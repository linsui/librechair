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
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.libraries.launcherclient.ILauncherOverlay;
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class ServiceClient implements OpenableOverscrollClient, DisconnectableOverscrollClient,
        SearchableOverscrollClient {

    private ILauncherOverlay overlay;
    private Activity boundActivity;
    private ServiceFactory factory;
    private ILauncherOverlayCallback overlayCallback;
    private WindowManager.LayoutParams params;
    private Bundle additionalParams;
    private boolean activityRunning;
    private int apiVersion;
    private ArrayList<Consumer<ILauncherOverlay>> overlayChangeListeners;

    public ServiceClient(Activity boundActivity,
                         ServiceFactory factory, OverlayCallback callback,
                         Runnable disconnectCallback, Runnable connectCallback) {
        this.boundActivity = boundActivity;
        this.factory = factory;
        this.apiVersion = factory.getApiVersion();
        this.overlayChangeListeners = new ArrayList<>();
        this.overlayCallback = new ILauncherOverlayCallback.Stub() {
            @Override
            public void overlayScrollChanged(float progress) throws RemoteException {
                boundActivity.runOnUiThread(() -> callback.overlayScrollChanged(progress));
            }

            @Override
            public void overlayStatusChanged(int status) throws RemoteException {
                boundActivity.runOnUiThread(() -> callback.overlayStatusChanged(status));
            }
        };
        factory.setChangeListener(l3overlay -> {
            this.overlay = l3overlay;
            ((ArrayList<Consumer<ILauncherOverlay>>) overlayChangeListeners.clone()).forEach(
                    consumer -> consumer.accept(l3overlay));
            if (params != null) {
                acceptLayoutParams(params);
            }
            if (l3overlay == null) {
                disconnectCallback.run();
                new Handler(boundActivity.getMainLooper()).postDelayed(this::reconnect,
                        TimeUnit.SECONDS.toMillis(2));
            } else {
                connectCallback.run();
            }
        });
        factory.connect();
    }

    @Override
    public void openOverlay(boolean animate) {
        Throwable throwable = new Throwable();
        Log.d(getClass().getName(), throwable.getStackTrace()[0].getMethodName() + ": Called",
                throwable);
        if (overlay != null) {
            try {
                overlay.openOverlay(animate ? 1 : 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void closeOverlay(boolean animate) {
        if (overlay != null) {
            try {
                overlay.closeOverlay(animate ? 1 : 0);
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
        activityRunning = false;
        if (overlay != null) {
            try {
                overlay.onPause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        activityRunning = false;
        if (overlay != null) {
            try {
                overlay.onPause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        activityRunning = false;
        if (overlay != null) {
            try {
                overlay.onPause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        activityRunning = false;
        if (overlay != null) {
            try {
                overlay.onPause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
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
        Throwable throwable = new Throwable();
        Log.d(getClass().getName(), throwable.getStackTrace()[0].getMethodName() + ": Called",
                throwable);
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
        Throwable throwable = new Throwable();
        Log.d(getClass().getName(), throwable.getStackTrace()[0].getMethodName() + ": Called",
                throwable);
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
        configure();
    }

    private void configure() {
        if (overlay != null) {
            try {
                Point point = new Point();
                boundActivity.getWindowManager().getDefaultDisplay().getRealSize(point);
                if (apiVersion < 3) {
                    overlay.windowAttached(boundActivity.getWindow().getAttributes(),
                            overlayCallback, 0);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("layout_params",
                            params);
                    bundle.putParcelable("configuration",
                            boundActivity.getResources().getConfiguration());
                    bundle.putInt("client_options", 0);
                    if (additionalParams != null) {
                        bundle.putAll(additionalParams);
                    }
                    overlay.windowAttached2(bundle, overlayCallback);
                }
                if (apiVersion >= 4) {
                    overlay.setActivityState(activityRunning ? 1 : 0); // TODO fix this
                } else if (activityRunning) {
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
        if (overlay != null) {
            try {
                return overlay.startSearch(options, parameters);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

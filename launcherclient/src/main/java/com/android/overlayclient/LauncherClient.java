package com.android.overlayclient;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.WindowManager;

import com.google.android.libraries.launcherclient.ILauncherOverlay;
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class LauncherClient implements OpenableOverscrollClient, DisconnectableOverscrollClient {

    private ILauncherOverlay overlay;
    private Activity boundActivity;
    private ServiceFactory factory;
    private ILauncherOverlayCallback overlayCallback;
    private Bundle additionalParams;
    private boolean activityRunning;
    private int apiVersion;

    public LauncherClient(Activity boundActivity,
                          ServiceFactory factory, OverlayCallback callback) {
        this.boundActivity = boundActivity;
        this.factory = factory;
        this.apiVersion = factory.getApiVersion();
        this.overlayCallback = new ILauncherOverlayCallback.Stub() {
            @Override
            public void overlayScrollChanged(float progress) throws RemoteException {
                callback.overlayScrollChanged(progress);
            }

            @Override
            public void overlayStatusChanged(int status) throws RemoteException {
                callback.overlayStatusChanged(status);
            }
        };
        factory.setChangeListener(l3overlay -> {
            this.overlay = l3overlay;
            if (l3overlay == null) {
                new Handler(boundActivity.getMainLooper()).postDelayed(this::reconnect,
                        TimeUnit.SECONDS.toMillis(2));
            }
        });
        factory.connect();
    }

    @Override
    public void openOverlay(boolean animate) {
        // TODO
    }

    @Override
    public void closeOverlay(boolean animate) {
        // TODO
    }

    @Override
    public void onAttachedToWindow() {
        acceptLayoutParams(boundActivity.getWindow().getAttributes());
    }

    @Override
    public void onDetachedFromWindow() {
        acceptLayoutParams(boundActivity.getWindow().getAttributes());
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
        if (overlay != null) {
            try {
                overlay.windowDetached(boundActivity.isChangingConfigurations());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
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
        boundActivity.unbindService(factory);
    }

    public void putAdditionalParams(Bundle params) {
        additionalParams = params;
        configure();
    }

    private void configure() {
        try {
            Point point = new Point();
            boundActivity.getWindowManager().getDefaultDisplay().getRealSize(point);
            if (apiVersion < 3) {
                overlay.windowAttached(boundActivity.getWindow().getAttributes(), overlayCallback, 0);
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelable("layout_params", boundActivity.getWindow().getAttributes());
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

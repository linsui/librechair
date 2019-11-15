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

/**
 * {@link ServiceClient} is an implementation of all
 * inheritors of {@link WorkspaceOverscrollClient}, which is compatible with
 * the Google app, the overlay implementation used by Librechair, and in the
 * ory should be compatible with other third-party overlay providers, such a
 * s HomeFeeder.
 *
 * @author Po Lu
 * @since the beginning of time
 */
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

    /**
     * Creates a ServiceClient from an activity.
     *
     * @param boundActivity      the activity that will display the overlay
     * @param factory            the factory that will provide the overlay service
     * @param callback           the overlay callback
     * @param disconnectCallback the callback that will be executed when the overlay disconnects
     * @param connectCallback    the callback that will be exectuted when the overlay connects
     * @param mode               the mode this client should be attached in
     * @see ServiceClient
     * @see ServiceFactory
     * @see OverlayCallback
     * @see ServiceMode
     */
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

    /**
     * Opens the overlay, with the default animation speed if necessary
     *
     * @param animate whether the overlay should animate the opening
     * @see #closeOverlay
     */
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

    /**
     * Closes the overlay, with the default animation speed if necessary
     *
     * @param animate whether the overlay should animate the closing
     * @see #openOverlay
     */
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

    /**
     * Opens the overlay in {@param duration} milliseconds
     *
     * @param duration the length of the animation, in milliseconds, or 0 if no animation should be used
     */
    @Override
    public void openOverlay(int duration) {
        if (overlay != null) {
            try {
                overlay.openOverlay(getAnimationFlags(duration != 0, duration));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes the overlay in {@param duration} milliseconds
     *
     * @param duration the length of the animation, in milliseconds, or 0 if no animation should be used
     */
    @Override
    public void closeOverlay(int duration) {
        if (overlay != null) {
            try {
                overlay.closeOverlay(getAnimationFlags(duration != 0, duration));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This function should be called whenever {@link android.app.Activity#onAttachedToWindow()}
     * is called in the attached activity
     */
    @Override
    public void onAttachedToWindow() {
        acceptLayoutParams(boundActivity.getWindow().getAttributes());
    }

    /**
     * This function should be called whenever {@link android.app.Activity#onDetachedFromWindow()}
     * is called in the attached activity
     */
    @Override
    public void onDetachedFromWindow() {
        acceptLayoutParams(null);
    }

    /**
     * This function should be called whenever {@link android.app.Activity#onResume()}
     * is called in the attached activity
     */
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

    /**
     * This function should be called whenever {@link android.app.Activity#onPause()}
     * is called in the attached activity.
     */
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

    /**
     * This function should be called whenever {@link android.app.Activity#onStart()}
     * is called in the attached activity.
     */
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

    /**
     * This function should be called whenever {@link android.app.Activity#onStop()} is called in the attached activity.
     */
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

    /**
     * This function sends {@param ogParams} to the overlay.
     * <em>
     * The common programmer has no use for this function; the lifecycle callbacks should be used instead.
     * </em>
     *
     * @param ogParams the layout params to be sent
     * @see #onAttachedToWindow
     * @see #onDetachedFromWindow
     */
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

    /**
     * This function <em>must</em> be called when a scroll interaction begins.
     *
     * @see #stopScroll
     * @see #onScroll
     */
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

    /**
     * Scrolls the overlay to {@param percentage}
     * Note: it is generally <em>bad practice</em> to try to close
     * an overlay by calling this function with {@param percentage}
     * as 0.
     * Use {@link #closeOverlay} and {@link #openOverlay} instead.
     *
     * @param percentage the percentage the overlay has scrolled
     * @see #openOverlay
     * @see #closeOverlay
     */
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

    /**
     * This function <em>must</em> be called when a scroll interaction ends.
     *
     * @see #startScroll
     * @see #onScroll
     */
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

    /**
     * Forces the client to reconnect.
     * This function may have unintended consequences. Use with care.
     */
    @Override
    public void reconnect() {
        factory.connect();
    }

    /**
     * Forcibly disconnects from the overlay.
     */
    @Override
    public void disconnect() {
        serviceState.setSearchAttached(false);
        serviceState.setOverlayAttached(false);
        factory.disconnect();
    }

    /**
     * Puts additional parameters to the overlay configuration bundle.
     * This is only available for overlays using overlay API version 7 or later.
     * This function shouldn't be used normally. Use the configuration delegate system instead.
     *
     * @param params the additional parameters
     * @see com.android.overlayclient.compat.ConfigurationDelegate
     * @see #addConfigurationDelegate
     */
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

    /**
     * Sends required configuration information to the overlay, along with custom parameters defined
     * by {@link #putAdditionalParams} and defined configuration delegates.
     * This function shouldn't be called manually, since configuration is automatically handled by this client.
     *
     * @see #putAdditionalParams
     * @see #addConfigurationDelegate
     */
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

    /**
     * Starts search. Requires the overlay be attached in search mode,
     * or that the overlay supports a unified connection.
     *
     * @param options    unknown protobuf blob
     * @param parameters undocumented parameter bundle.
     * @see ServiceMode
     */
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

    /**
     * Starts voice search. Requires the overlay be attached in search mode,
     * or that the overlay supports a unified connection.
     *
     * @param start whether voice detection should be started
     * @see #startSearch
     * @see ServiceMode
     */
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

    /**
     * Returns the voice search language, in an ISO locale code.
     * The overlay must be attached in search mode, or support
     * a unified connection.
     * Overlays are known to return garbage. Parse output carefully.
     *
     * @return the voice search langauge. may be null or garbage.
     */
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

    /**
     * Returns whether voice detection is running.
     * The overlay must be attached in search mode,
     * or support a unified connection.
     *
     * @return whether hotword detection is running.
     */
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

    /**
     * Returns the raw overlay.
     * This function should <em> never be called </em>
     * by developers.
     *
     * @return the overlay, or null if disconnected
     */
    public ILauncherOverlay getOverlay() {
        return overlay;
    }

    /**
     * Callback intercept method.
     * <em>do not call</em>
     *
     * @see callback
     * @see OverlayCallback
     */
    @Override
    public void overlayScrollChanged(float progress) {
        mUIHandler.removeMessages(MESSAGE_CHANGE_SCROLL);
        mUIHandler.obtainMessage(MESSAGE_CHANGE_SCROLL, progress).sendToTarget();
    }

    /**
     * Callback intercept method.
     * <em>do not call</em>
     *
     * @see callback
     * @see OverlayCallback
     */
    @Override
    public void overlayStatusChanged(int status) {
        mUIHandler.obtainMessage(MESSAGE_CHANGE_STATUS, status, 0);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_CHANGE_SCROLL:
                if (overlay != null) {
                    callback.overlayScrollChanged((float) msg.obj);
                }
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

    /**
     * Attaches an additonal configuration delegate.
     *
     * @see com.android.overlayclient.compat.ConfigurationDelegate
     */
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

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

package ch.deletescape.lawnchair.feed.notifications;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.KeyEvent;

import com.android.launcher3.Utilities;
import com.android.launcher3.notification.NotificationListener;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Paused mode is not supported on Marshmallow because the MediaSession is missing
 * notifications. Without this information, it is impossible to hide on stop.
 */
public class OMCMediaListener extends MediaController.Callback
        implements OnActiveSessionsChangedListener {
    private static final String TAG = "MediaListener";

    private final ComponentName mComponent;
    private final MediaSessionManager mManager;
    private final Runnable mOnChange;
    private final Vector<StatusBarNotification> notifications = new Vector<>();
    private List<MediaController> mControllers = Collections.emptyList();
    private MediaNotificationController mTracking;
    private final Handler mHandler = new Handler();
    private final Handler mWorkHandler;

    @SuppressWarnings({"WeakerAccess", "ConstantConditions"})
    public OMCMediaListener(Context context, Runnable onChange) {
        mComponent = new ComponentName(context, NotificationListener.class);
        mManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        mOnChange = onChange;
        OverlayNotificationManager.addListener(list -> {
            synchronized (notifications) {
                notifications.clear();
                notifications.addAll(list);
            }
            onActiveSessionsChanged(null);
        });
        HandlerThread handlerThread = new HandlerThread("");
        handlerThread.start();
        mWorkHandler = new Handler(handlerThread.getLooper());
        try {
            mManager.addOnActiveSessionsChangedListener(this,
                    new ComponentName(context, NotificationListener.class));
        } catch (SecurityException e) {
            Log.e(getClass().getSimpleName(),
                    "<init>: error subscribing to media manager (probably missing permissions)", e);
        }
    }

    public MediaNotificationController getTracking() {
        return mTracking;
    }

    public String getPackage() {
        return mTracking.controller.getPackageName();
    }

    private void updateControllers(List<MediaController> controllers) {
        for (MediaController mc : mControllers) {
            mc.unregisterCallback(this);
        }
        for (MediaController mc : controllers) {
            mc.registerCallback(this);
        }
        mControllers = controllers;
    }

    @Override
    public void onActiveSessionsChanged(List<MediaController> controllers) {
        if (mWorkHandler != null) {
            mWorkHandler.post(() -> updateTracking(controllers));
        }
    }

    private void updateTracking(List<MediaController> controllers) {
        if (controllers == null) {
            try {
                controllers = mManager.getActiveSessions(mComponent);
            } catch (SecurityException ignored) {
                controllers = Collections.emptyList();
            }
        }
        updateControllers(controllers);

        if (mTracking != null) {
            mTracking.reloadInfo();
        }

        // If the current controller is not paused or playing, stop tracking it.
        if (mTracking != null
                && (!controllers.contains(
                mTracking.controller) || !mTracking.isPausedOrPlaying())) {
            mTracking = null;
        }

        for (MediaController mc : controllers) {
            MediaNotificationController mnc = new MediaNotificationController(mc);
            // Either we are not tracking a controller and this one is valid,
            // or this one is playing while the one we track is not.
            if ((mTracking == null && mnc.isPausedOrPlaying())
                    || (mTracking != null && mnc.isPlaying() && !mTracking.isPlaying())) {
                mTracking = mnc;
            }
        }

        mHandler.removeCallbacks(mOnChange);
        mHandler.post(mOnChange);
    }

    private void pressButton(int keyCode) {
        if (mTracking != null) {
            mTracking.pressButton(keyCode);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void toggle(boolean finalClick) {
        if (!finalClick) {
            Log.d(TAG, "Toggle");
            pressButton(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
        }
    }

    public void next() {
        Log.d(TAG, "Next");
        pressButton(KeyEvent.KEYCODE_MEDIA_NEXT);
        pressButton(KeyEvent.KEYCODE_MEDIA_PLAY);
    }

    public void previous() {
        Log.d(TAG, "Previous");
        pressButton(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        pressButton(KeyEvent.KEYCODE_MEDIA_PLAY);
    }

    // If there is no notification, consider the state to be stopped.
    private boolean hasNotification(@Nullable MediaController mc) {
        return findNotification(mc) != null;
    }

    private StatusBarNotification findNotification(@Nullable MediaController mc) {
        if (mc == null) return null;
        MediaSession.Token controllerToken = mc.getSessionToken();
        synchronized (notifications) {
            for (StatusBarNotification notif : notifications) {
                Bundle extras = notif.getNotification().extras;
                MediaSession.Token notifToken = extras.getParcelable(
                        Notification.EXTRA_MEDIA_SESSION);
                if (controllerToken.equals(notifToken)) {
                    return notif;
                }
            }
        }
        return null;
    }

    /**
     * Events that refresh the current handler.
     */
    public void onPlaybackStateChanged(PlaybackState state) {
        super.onPlaybackStateChanged(state);
        onActiveSessionsChanged(null);
    }

    public void onMetadataChanged(MediaMetadata metadata) {
        super.onMetadataChanged(metadata);
        onActiveSessionsChanged(null);
    }

    public class MediaInfo {

        private CharSequence title;
        private CharSequence artist;
        private CharSequence album;
        private Long length;
        private Bitmap bitmap;

        public CharSequence getTitle() {
            return title;
        }

        @SuppressWarnings("WeakerAccess")
        public CharSequence getArtist() {
            return artist;
        }

        @SuppressWarnings("WeakerAccess")
        public CharSequence getAlbum() {
            return album;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }


        public Long getDuration() {
            return length;
        }
    }

    public class MediaNotificationController {

        private MediaController controller;
        private StatusBarNotification sbn;
        private MediaInfo info;

        private MediaNotificationController(MediaController controller) {
            this.controller = controller;
            this.sbn = findNotification(controller);
            reloadInfo();
        }

        private boolean hasNotification() {
            return sbn != null;
        }

        private boolean hasTitle() {
            return info != null && info.title != null;
        }

        public boolean isPlaying() {
            return (!Utilities.ATLEAST_NOUGAT || hasNotification())
                    && hasTitle()
                    && controller.getPlaybackState() != null
                    && controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING;
        }

        private boolean isPausedOrPlaying() {
            if (Utilities.ATLEAST_NOUGAT) {
                if (!hasNotification() || !hasTitle() || controller.getPlaybackState() == null) {
                    return false;
                }
                int state = controller.getPlaybackState().getState();
                return state == PlaybackState.STATE_PAUSED
                        || state == PlaybackState.STATE_PLAYING;
            }
            return isPlaying();
        }

        private void pressButton(int keyCode) {
            controller.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
            controller.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
        }

        private void reloadInfo() {
            MediaMetadata metadata = controller.getMetadata();
            if (metadata != null) {
                info = new MediaInfo();
                info.title = metadata.getText(MediaMetadata.METADATA_KEY_TITLE);
                info.artist = metadata.getText(MediaMetadata.METADATA_KEY_ARTIST);
                info.album = metadata.getText(MediaMetadata.METADATA_KEY_ALBUM);
                info.length = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
                info.bitmap = metadata.getDescription().getIconBitmap();
            } else if (sbn != null) {
                info = new MediaInfo();
                info.title = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE);
            }
        }

        public String getPackageName() {
            return controller.getPackageName();
        }

        public StatusBarNotification getSbn() {
            return sbn;
        }

        public MediaInfo getInfo() {
            return info;
        }
    }
}

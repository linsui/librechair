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

package ch.deletescape.lawnchair.feed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherNotifications;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.uioverrides.WallpaperColorInfo;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.ParcelablePair;
import com.android.overlayclient.client.CompanionServiceFactory;
import com.android.overlayclient.client.CustomServiceClient;
import com.android.overlayclient.client.OverlayCallback;
import com.android.overlayclient.client.ServiceMode;
import com.android.overlayclient.compat.ColorDelegate;
import com.android.overlayclient.compat.FloatDelegate;
import com.google.android.apps.nexuslauncher.CustomAppPredictor;
import com.google.android.apps.nexuslauncher.allapps.PredictionsFloatingHeader;
import com.google.android.libraries.launcherclient.ILauncherInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.allapps.ParcelableComponentKeyMapper;
import ch.deletescape.lawnchair.colors.ColorEngine;
import ch.deletescape.lawnchair.feed.notifications.INotificationsChangedListener;
import ch.deletescape.lawnchair.persistence.FeedPersistence;

public class ClientOverlay implements Launcher.LauncherOverlay {
    private Launcher.LauncherOverlayCallbacks callbacks;
    private CustomServiceClient client;
    private Handler mainThread;

    public ClientOverlay(Launcher launcher) {
        mainThread = new Handler(Looper.getMainLooper());
        client = new CustomServiceClient(launcher, new CompanionServiceFactory(launcher) {
            @Override
            protected Intent getService() {
                String pkg = Utilities.getLawnchairPrefs(launcher).getFeedProviderPackage();
                return new Intent("com.android.launcher3.WINDOW_OVERLAY")
                        .setPackage(pkg)
                        .setData(Uri.parse("app://" +
                                launcher.getPackageName() +
                                ":" +
                                Process.myUid())
                                .buildUpon()
                                .appendQueryParameter("v", Integer.toString(7))
                                .appendQueryParameter("cv", Integer.toString(9))
                                .build());
            }
        }, new OverlayCallback() {
            @Override
            public void overlayScrollChanged(float progress) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    Log.w(ClientOverlay.this.getClass().getSimpleName(),
                            "overlayScrollChanged: scroll changed from non-main thread");
                    mainThread.post(() -> callbacks.onScrollChanged(progress));
                } else {
                    callbacks.onScrollChanged(progress);
                }
            }

            @Override
            public void overlayStatusChanged(int status) {

            }
        }, () -> {
            launcher.setLauncherOverlay(null);
            callbacks.onScrollChanged(0);
        }, () -> {
            launcher.setLauncherOverlay(this);
            client.attachInterface(new ILauncherInterface.Stub() {
                @Override
                public List<String> getSupportedCalls() {
                    return Arrays.asList(CustomServiceClient.PREDICTIONS_CALL,
                            CustomServiceClient.ACTIONS_CALL,
                            CustomServiceClient.NOTIFICATIONS_CALL);
                }

                @SuppressWarnings("unchecked")
                @Override
                public Bundle call(String callName, Bundle opt) {
                    switch (callName) {
                        case CustomServiceClient.PREDICTIONS_CALL:
                            UserEventDispatcher dispatcher = launcher.getUserEventDispatcher();
                            if (dispatcher instanceof CustomAppPredictor) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList("retval", new ArrayList<>(
                                        ((CustomAppPredictor) dispatcher).getPredictions().stream().limit(
                                                opt.getInt("amt", -1) != -1 ? opt.getInt(
                                                        "amt") : 10).map(
                                                it -> new ParcelableComponentKeyMapper(
                                                        it.getComponentKey(), it.getApp(
                                                        launcher.getAllAppsController().getAppsView().getAppsStore()).iconBitmap)).collect(
                                                Collectors.toList())));
                                return bundle;
                            }
                            break;
                        case CustomServiceClient.ACTIONS_CALL:
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("retval", new ArrayList<>(
                                    ((PredictionsFloatingHeader) LauncherAppState
                                            .getInstanceNoCreate()
                                            .getLauncher()
                                            .getAppsView()
                                            .getFloatingHeaderView())
                                            .getActionsRowView()
                                            .getActions()
                                            .stream()
                                            .limit(opt.getInt("amt", -1) != -1 ? opt.getInt(
                                                    "amt") : 10)
                                            .map(it -> new ParcelablePair(
                                                    it.shortcutInfo.iconBitmap,
                                                    it.shortcut.getShortcutInfo()))
                                            .collect(Collectors.toList())));
                            return bundle;
                        case CustomServiceClient.NOTIFICATIONS_CALL:
                            INotificationsChangedListener listener =
                                    INotificationsChangedListener.Stub.asInterface(
                                            opt.getBinder("listener"));
                            if (listener != null) {
                                LauncherNotifications.getInstance().addListener(
                                        new NotificationListener.NotificationsChangedListener() {
                                            @Override
                                            public synchronized void onNotificationPosted(
                                                    PackageUserKey postedPackageUserKey,
                                                    NotificationKeyData notificationKey,
                                                    boolean shouldBeFilteredOut) {
                                                NotificationListener ll = NotificationListener.getInstanceIfConnected();
                                                if (ll != null) {
                                                    List<StatusBarNotification> sbn;
                                                    if ((sbn = ll.getNotificationsForKeys(
                                                            Collections.singletonList(
                                                                    notificationKey))) != null && sbn.size() > 0) {
                                                        sbn.forEach(posted -> {
                                                            try {
                                                                listener.notificationPosted(posted);
                                                            } catch (RemoteException e) {
                                                                e.printStackTrace();
                                                            }
                                                        });
                                                    }
                                                }
                                            }

                                            @Override
                                            public synchronized void onNotificationRemoved(
                                                    PackageUserKey removedPackageUserKey,
                                                    NotificationKeyData notificationKey) {
                                                try {
                                                    listener.notificationRemoved(
                                                            notificationKey.notificationKey);
                                                } catch (RemoteException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public synchronized void onNotificationFullRefresh(
                                                    List<StatusBarNotification> activeNotifications) {
                                                try {
                                                    listener.notificationsChanged(
                                                            activeNotifications);
                                                } catch (RemoteException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            }
                            return new Bundle();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("err", "e_unsupported");
                    return bundle;
                }
            });
        }, ServiceMode.OVERLAY);
        ColorDelegate primary = new ColorDelegate(
                WallpaperColorInfo.getInstance(launcher).getActualMainColor(),
                ColorDelegate.PRIMARY);
        ColorDelegate secondary = new ColorDelegate(
                WallpaperColorInfo.getInstance(launcher).getSecondaryColor(),
                ColorDelegate.SECONDARY);
        ColorDelegate primaryActual = new ColorDelegate(
                WallpaperColorInfo.getInstance(launcher).getActualSecondaryColor(),
                ColorDelegate.PRIMARY + "_actual");
        ColorDelegate secondaryActual = new ColorDelegate(
                WallpaperColorInfo.getInstance(launcher).getActualSecondaryColor(),
                ColorDelegate.SECONDARY + "_actual");
        ColorDelegate tertiary = new ColorDelegate(
                WallpaperColorInfo.getInstance(launcher).getTertiaryColor(),
                ColorDelegate.TERTIARY);
        ColorDelegate accent = new ColorDelegate(
                ColorEngine.getInstance(launcher).getAccentResolver().resolveColor(),
                ColorDelegate.ACCENT_COLOR);
        FloatDelegate cardCornerRadius = new FloatDelegate(
                (float) FeedPersistence.Companion.getInstance(launcher).getCardCornerRadius(),
                FloatDelegate.CARD_CORNER_RADIUS);
        WallpaperColorInfo.getInstance(launcher).addOnChangeListener(wallpaperColorInfo -> {
            primary.set(wallpaperColorInfo.getActualMainColor());
            secondary.set(wallpaperColorInfo.getSecondaryColor());
            tertiary.set(wallpaperColorInfo.getTertiaryColor());
        });
        ColorEngine.getInstance(launcher).addColorChangeListeners(resolveInfo ->
                accent.set(resolveInfo.getColor()), ColorEngine.Resolvers.ACCENT);
        client.addConfigurationDelegate(primary);
        client.addConfigurationDelegate(secondary);
        client.addConfigurationDelegate(tertiary);
        client.addConfigurationDelegate(primaryActual);
        client.addConfigurationDelegate(secondaryActual);
        client.addConfigurationDelegate(accent);
        client.addConfigurationDelegate(cardCornerRadius);
    }

    @Override
    public void onScrollInteractionBegin() {
        client.startScroll();
    }

    @Override
    public void onScrollInteractionEnd() {
        client.stopScroll();
    }

    @Override
    public void onScrollChange(float progress, boolean rtl) {
        client.onScroll(progress);
    }

    @Override
    public void setOverlayCallbacks(Launcher.LauncherOverlayCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public boolean shouldFadeWorkspaceDuringScroll() {
        return client.shouldFadeWorkspaceDuringScroll();
    }

    @Override
    public boolean shouldScrollLauncher() {
        return client.shouldScrollLauncher();
    }

    public CustomServiceClient getClient() {
        return client;
    }
}

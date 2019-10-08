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

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.ComponentKeyMapper;
import com.android.launcher3.util.ParcelablePair;
import com.android.overlayclient.CompanionServiceFactory;
import com.android.overlayclient.CustomServiceClient;
import com.android.overlayclient.OverlayCallback;
import com.android.overlayclient.ServiceMode;
import com.google.android.apps.nexuslauncher.CustomAppPredictor;
import com.google.android.apps.nexuslauncher.allapps.ActionsRowView;
import com.google.android.apps.nexuslauncher.allapps.PredictionsFloatingHeader;
import com.google.android.libraries.launcherclient.ILauncherInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.allapps.ParcelableComponentKeyMapper;

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
                mainThread.post(() -> callbacks.onScrollChanged(progress));
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
                public List<String> getSupportedCalls() throws RemoteException {
                    return Arrays.asList(CustomServiceClient.PREDICTIONS_CALL,
                            CustomServiceClient.ACTIONS_CALL);
                }

                @Override
                public Bundle call(String callName, Bundle opt) throws RemoteException {
                    if (callName.equals(CustomServiceClient.PREDICTIONS_CALL)) {
                        UserEventDispatcher dispatcher = launcher.getUserEventDispatcher();
                        if (dispatcher instanceof CustomAppPredictor) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("retval", new ArrayList<>(
                                    ((CustomAppPredictor) dispatcher).getPredictions().stream().map(
                                            it -> new ParcelableComponentKeyMapper(
                                                    it.getComponentKey())).collect(
                                            Collectors.toList())));
                            return bundle;
                        }
                    } else if (callName.equals(CustomServiceClient.ACTIONS_CALL)) {
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
                                        .map(it -> new ParcelablePair(it.shortcutInfo.iconBitmap,
                                                        it.shortcut.getShortcutInfo()))
                                        .collect(Collectors.toList())));
                        return bundle;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("err", "e_unsupported");
                    return bundle;
                }
            });
        }, ServiceMode.OVERLAY);
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

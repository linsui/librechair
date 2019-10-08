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
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

import com.google.android.libraries.launcherclient.ILauncherInterface;
import com.google.android.libraries.launcherclient.ILauncherOverlayCompanion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CustomServiceClient extends ServiceClient implements CustomOverscrollClient {
    private int companionApiVersion;
    private CompanionServiceFactory factory;
    private List<Consumer<ILauncherOverlayCompanion>> connectConsumers;

    public CustomServiceClient(Activity boundActivity,
                               CompanionServiceFactory factory, OverlayCallback callback,
                               Runnable disconnectCallback, Runnable connectCallback,
                               ServiceMode mode) {
        super(boundActivity, factory, callback, disconnectCallback, connectCallback, mode);
        this.factory = factory;
        this.companionApiVersion = factory.getCompanionApiVersion();
        this.connectConsumers = new ArrayList<>();
        factory.setCompanionChangeListener(companion -> {
            if (companion != null) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    connectConsumers.forEach(consumer -> consumer.accept(companion));
                    connectConsumers.clear();
                });
            }
        });
    }

    @Override
    public boolean shouldScrollLauncher() {
        if (companionApiVersion < 0 || factory.getCompanion() == null) {
            return true;
        }
        try {
            return factory.getCompanion().shouldScrollWorkspace();
        } catch (RemoteException e) {
            return true;
        }
    }

    @Override
    public boolean shouldFadeWorkspaceDuringScroll() {
        if (companionApiVersion < 1 || factory.getCompanion() == null) {
            return true;
        }
        try {
            return (factory.getCompanion() == null || companionApiVersion < 1) || factory.getCompanion().shouldFadeWorkspaceDuringScroll();
        } catch (RemoteException e) {
            return true;
        }
    }

    @Override
    public void restartProcess() {
        if (factory.getCompanion() != null && companionApiVersion >= 2) {
            try {
                factory.getCompanion().restartProcess();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void attachInterface(ILauncherInterface interfaze) {
        if (factory.getCompanion() != null && companionApiVersion >= 3) {
            try {
                factory.getCompanion().attachInterface(interfaze);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (companionApiVersion >= 3) {
            connectConsumers.add(companion -> {
                if (companionApiVersion >= 3) {
                    try {
                        companion.attachInterface(interfaze);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public boolean onBackPressed() {
        if (factory.getCompanion() != null && companionApiVersion >= 4) {
            try {
                return factory.getCompanion().onBackPressed();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

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
import android.os.RemoteException;

public class CustomServiceClient extends ServiceClient implements CustomOverscrollClient {
    private int companionApiVersion;
    private CompanionServiceFactory factory;

    public CustomServiceClient(Activity boundActivity,
                               CompanionServiceFactory factory, OverlayCallback callback,
                               Runnable disconnectCallback, Runnable connectCallback) {
        super(boundActivity, factory, callback, disconnectCallback, connectCallback);
        this.factory = factory;
        this.companionApiVersion = factory.getCompanionApiVersion();
    }

    @Override
    public boolean shouldScrollLauncher() {
        try {
            return (getOverlay() == null || companionApiVersion == -1 || factory.getCompanion() == null) || factory.getCompanion().shouldScrollWorkspace();
        } catch (RemoteException e) {
            return true;
        }
    }

    @Override
    public boolean shouldFadeWorkspaceDuringScroll() {
        try {
            return (getOverlay() == null || factory.getCompanion() == null || companionApiVersion < 1) || factory.getCompanion().shouldFadeWorkspaceDuringScroll();
        } catch (RemoteException e) {
            return true;
        }
    }

    @Override
    public void restartProcess() {
        if (getOverlay() != null && factory.getCompanion() != null && companionApiVersion >= 2) {
            try {
                factory.getCompanion().restartProcess();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}

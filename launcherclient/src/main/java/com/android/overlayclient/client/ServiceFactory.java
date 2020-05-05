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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;

import com.google.android.libraries.launcherclient.ILauncherOverlay;

import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public abstract class ServiceFactory implements ServiceConnection {
    private ILauncherOverlay overlay;
    private Consumer<ILauncherOverlay> changeListener;
    private Context context;
    private boolean connected;

    protected abstract Intent getService();

    protected ServiceFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        overlay = ILauncherOverlay.Stub.asInterface(iBinder);
        connected = true;
        changeListener.accept(overlay);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        connected = false;
        overlay = null;
        changeListener.accept(null);
    }

    public void connect() {
        context.bindService(getService(), this,
                Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
    }

    public void disconnect() {
        if (connected) {
            connected = false;
            context.unbindService(this);
            changeListener.accept(null);
        }
    }

    public int getApiVersion() {
        ResolveInfo resolveService = context.getPackageManager().resolveService(getService(),
                PackageManager.GET_META_DATA);
        return resolveService == null || resolveService.serviceInfo.metaData == null ?
                1 : resolveService.serviceInfo.metaData.getInt("service.api.version", 1);
    }

    public void setChangeListener(
            Consumer<ILauncherOverlay> changeListener) {
        this.changeListener = changeListener;
    }

    public boolean supportsUnifiedConnection() {
        ResolveInfo resolveService = context.getPackageManager().resolveService(getService(),
                PackageManager.GET_META_DATA);
        return resolveService != null && resolveService.serviceInfo.metaData != null && resolveService.serviceInfo.metaData.getInt(
                "service.api.unifiedConnection", 0) > 0;
    }
}

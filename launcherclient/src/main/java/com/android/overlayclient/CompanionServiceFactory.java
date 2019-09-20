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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;

import com.android.overlayclient.util.OverlayUtil;
import com.google.android.libraries.launcherclient.ILauncherOverlayCompanion;

import java.util.function.Consumer;

public abstract class CompanionServiceFactory extends ServiceFactory {
    private ILauncherOverlayCompanion companion;
    private Consumer<ILauncherOverlayCompanion> companionChangeListener;
    private int companionApiVersion;
    private Context context;
    private ServiceConnection companionServiceConnection;

    protected CompanionServiceFactory(Context context) {
        super(context);
        this.context = context;
        ResolveInfo info = context.getPackageManager().resolveService(getService(),
                PackageManager.GET_META_DATA);
        companionApiVersion = info.serviceInfo.metaData.getInt("service.api.companionVersion", -1);
    }

    @Override
    public void connect() {
        super.connect();
        if (companionApiVersion != -1) {
            Intent i = OverlayUtil.resolveCompanion(getService().getPackage(), context);
            if (i != null) {
                context.bindService(i, companionServiceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        if (companionChangeListener != null) {
                            companionChangeListener.accept(
                                    companion = ILauncherOverlayCompanion.Stub.asInterface(
                                            iBinder));
                        } else {
                            companion = ILauncherOverlayCompanion.Stub.asInterface(iBinder);
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        companionServiceConnection = null;
                        if (companionChangeListener != null) {
                            companionChangeListener.accept(companion = null);
                        } else {
                            companion = null;
                        }
                    }
                }, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
            }
        }
    }

    @Override
    public void disconnect() {
        super.disconnect();
        if (companionServiceConnection != null) {
            context.unbindService(companionServiceConnection);
        }
    }

    public int getCompanionApiVersion() {
        return companionApiVersion;
    }

    public ILauncherOverlayCompanion getCompanion() {
        return companion;
    }

    public void setCompanionChangeListener(
            Consumer<ILauncherOverlayCompanion> companionChangeListener) {
        this.companionChangeListener = companionChangeListener;
    }
}

package com.android.overlayclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;

import com.google.android.libraries.launcherclient.ILauncherOverlay;

import java.util.function.Consumer;

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
}

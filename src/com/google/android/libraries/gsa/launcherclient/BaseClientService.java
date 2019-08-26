package com.google.android.libraries.gsa.launcherclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.google.android.libraries.launcherclient.ILauncherOverlay;

public class BaseClientService implements ServiceConnection {

    private boolean mConnected;
    public final Context mContext;
    private final int mFlags;
    private LauncherClient client;
    private int connectRetries = 0;

    BaseClientService(Context context, int flags, LauncherClient client) {
        mContext = context;
        mFlags = flags;
        this.client = client;
    }

    public final boolean connect() {
        if (connectRetries < 5) {
            try {
                mConnected = mContext
                        .bindService(LauncherClient.getIntent(mContext), this, mFlags);
            } catch (Throwable e) {
                Log.e("LauncherClient", "Unable to connect to overlay service", e);
            }
            connectRetries = 0;
            return mConnected;
        } else {
            return false;
        }
    }

    public final void disconnect() {
        Log.d("LauncherClient", "disconnect: disconnecting overlay", new Throwable());
        if (mConnected) {
            mConnected = false;
            mContext.unbindService(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("LauncherClient", "onServiceConnected: connected to overlay " + name);
        client.setOverlay(ILauncherOverlay.Stub.asInterface(service));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("LauncherClient",
                "onServiceDisconnected: disconnected from overlay " + name.flattenToShortString());
        if (mConnected) {
            ++connectRetries;
            connect();
        }
    }
}
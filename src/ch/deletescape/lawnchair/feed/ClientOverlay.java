package ch.deletescape.lawnchair.feed;

import android.content.Intent;
import android.net.Uri;
import android.os.Process;

import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.overlayclient.LauncherClient;
import com.android.overlayclient.OverlayCallback;
import com.android.overlayclient.ServiceFactory;

public class ClientOverlay implements Launcher.LauncherOverlay {
    private Launcher.LauncherOverlayCallbacks callbacks;
    private LauncherClient client;

    public ClientOverlay(Launcher launcher) {
        client = new LauncherClient(launcher, new ServiceFactory(launcher) {
            @Override
            protected Intent getService() {
                String pkg = Utilities.getLawnchairPrefs(launcher).getFeedProviderPackage();
                return new Intent("com.android.launcher3.WINDOW_OVERLAY")
                        .setPackage(launcher.getPackageName())
                        .setData(Uri.parse(new StringBuilder(pkg.length() + 18)
                                .append("app://")
                                .append(pkg)
                                .append(":")
                                .append(Process.myUid())
                                .toString())
                                .buildUpon()
                                .appendQueryParameter("v", Integer.toString(7))
                                .appendQueryParameter("cv", Integer.toString(9))
                                .build());
            }
        }, new OverlayCallback() {
            @Override
            public void overlayScrollChanged(float progress) {
                callbacks.onScrollChanged(progress);
            }

            @Override
            public void overlayStatusChanged(int status) {

            }
        });
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
        client.onScroll(rtl ? -progress : progress);
    }

    @Override
    public void setOverlayCallbacks(Launcher.LauncherOverlayCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public LauncherClient getClient() {
        return client;
    }
}

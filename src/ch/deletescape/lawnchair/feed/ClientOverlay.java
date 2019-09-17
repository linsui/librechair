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
import android.os.Process;

import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.overlayclient.CompanionServiceFactory;
import com.android.overlayclient.CustomServiceClient;
import com.android.overlayclient.OverlayCallback;

public class ClientOverlay implements Launcher.LauncherOverlay {
    private Launcher.LauncherOverlayCallbacks callbacks;
    private CustomServiceClient client;

    public ClientOverlay(Launcher launcher) {
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
                callbacks.onScrollChanged(progress);
            }

            @Override
            public void overlayStatusChanged(int status) {

            }
        }, () -> {
            launcher.setLauncherOverlay(null);
            callbacks.onScrollChanged(0);
        }, () -> {
            launcher.setLauncherOverlay(this);
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

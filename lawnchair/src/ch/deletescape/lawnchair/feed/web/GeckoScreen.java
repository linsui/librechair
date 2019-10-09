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

package ch.deletescape.lawnchair.feed.web;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.mozilla.geckoview.ContentBlocking;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import ch.deletescape.lawnchair.persistence.FeedPersistenceKt;

public class GeckoScreen extends WebViewScreen {
    private final String uri;
    private final GeckoSession session;
    private static GeckoRuntime runtime;
    private GeckoView view;

    protected GeckoScreen(Context base, String uri) {
        super(base, uri);
        this.uri = uri;
        session = new GeckoSession();
        synchronized (GeckoScreen.class) {
            if (runtime == null) {
                runtime = GeckoRuntime.create(this);
            }
        }
    }

    @Override
    protected View getView(ViewGroup parent) {
        LinearLayout layout = new LinearLayout(parent.getContext());
        GeckoView webView = new GeckoView(parent.getContext());
        session.open(runtime);
        webView.setSession(session);
        webView.setId(android.R.id.content);
        layout.addView(webView);
        return layout;
    }

    @Override
    protected void bindView(View viewO) {
        view = viewO.findViewById(android.R.id.content);
        session.getSettings().setUseTrackingProtection(
                FeedPersistenceKt.getFeedPrefs(this).getEnableHostsFilteringInWebView());
        session.getSettings().setAllowJavascript(
                FeedPersistenceKt.getFeedPrefs(this).getUseJavascriptInSearchScreen());
        runtime.getSettings().getContentBlocking().setAntiTracking(FeedPersistenceKt.getFeedPrefs(
                this).getEnableHostsFilteringInWebView() ? ContentBlocking.AntiTracking.STRICT : ContentBlocking.AntiTracking.NONE);
        runtime.getSettings().getContentBlocking().setEnhancedTrackingProtectionLevel(
                FeedPersistenceKt.getFeedPrefs(
                        this).getEnableHostsFilteringInWebView() ? ContentBlocking.EtpLevel.STRICT : ContentBlocking.EtpLevel.NONE);
        runtime.getSettings().getContentBlocking().setSafeBrowsing(FeedPersistenceKt.getFeedPrefs(
                this).getEnableHostsFilteringInWebView() ? ContentBlocking.SafeBrowsing.DEFAULT : ContentBlocking.SafeBrowsing.NONE);
        session.loadUri(uri);
    }

    @Override
    public void onDestroy() {
        view.releaseSession();
        session.close();
    }
}

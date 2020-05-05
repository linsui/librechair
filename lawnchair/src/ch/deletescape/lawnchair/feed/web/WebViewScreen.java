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
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.android.launcher3.BuildConfig;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;

import java.io.ByteArrayInputStream;

import ch.deletescape.lawnchair.feed.FeedProvider;
import ch.deletescape.lawnchair.feed.ProviderScreen;
import ch.deletescape.lawnchair.feed.adblock.WebSafety;
import ch.deletescape.lawnchair.persistence.FeedPersistenceKt;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class WebViewScreen extends ProviderScreen {
    private final String uri;
    private WebView view;

    protected WebViewScreen(Context base, String uri) {
        super(base);
        this.uri = uri;
        addAction(new FeedProvider.Action(getDrawable(R.drawable.ic_open_in_browser_black_24dp),
                getString(R.string.title_button_open_externally),
                () -> Utilities.openURLinBrowser(this, getCurrentUrl())));
    }

    public static WebViewScreen obtain(Context base, String uri) {
        return !(FeedPersistenceKt.getFeedPrefs(
                base).getUseGecko() && BuildConfig.GECKO) ? new WebViewScreen(base,
                uri) : new GeckoScreen(base, uri);
    }

    @Override
    protected View getView(ViewGroup parent) {
        LinearLayout layout = new LinearLayout(parent.getContext());
        WebView webView = new WebView(parent.getContext());
        webView.setId(android.R.id.content);
        layout.addView(webView);
        return layout;
    }

    @Override
    public boolean onBackPressed() {
        if (view.canGoBack()) {
            view.goBack();
            return true;
        } else {
            return false;
        }
    }

    protected String getCurrentUrl() {
        return view.getUrl();
    }

    @Override
    protected void bindView(View viewO) {
        view = viewO.findViewById(android.R.id.content);
        viewO.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            viewO.getLayoutParams().height = MATCH_PARENT;
            viewO.getLayoutParams().width = MATCH_PARENT;
            view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                view.getLayoutParams().height = MATCH_PARENT;
                view.getLayoutParams().width = MATCH_PARENT;
            });
        });
        view.getSettings().setJavaScriptEnabled(
                FeedPersistenceKt.getFeedPrefs(this).getUseJavascriptInSearchScreen());
        view.setWebViewClient(new WebViewClient() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
                try {
                    String authority = request.getUrl().getAuthority() != null ? request.getUrl().getAuthority() : "example.com";
                    if (FeedPersistenceKt.getFeedPrefs(
                            WebViewScreen.this).getEnableHostsFilteringInWebView() && WebSafety.INSTANCE.shouldBlock(
                            authority, request.getUrl().toString())) {
                        return new WebResourceResponse("text/html", "utf8",
                                new ByteArrayInputStream(new byte[0]));
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        view.loadUrl(uri);
    }
}

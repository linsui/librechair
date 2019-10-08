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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;

import java.io.ByteArrayInputStream;
import java.util.function.Consumer;

import ch.deletescape.lawnchair.feed.adblock.WebSafety;
import ch.deletescape.lawnchair.persistence.FeedPersistenceKt;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class WebViewScreen extends ProviderScreen {
    private final String uri;
    private final Consumer<WebView> configurer;

    public WebViewScreen(Context base, String uri, Consumer<WebView> configurer) {
        super(base);
        this.uri = uri;
        this.configurer = configurer;
        addAction(new FeedProvider.Action(getDrawable(R.drawable.ic_open_in_browser_black_24dp),
                getString(R.string.title_button_open_externally),
                () -> Utilities.openURLinBrowser(this, uri)));
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
    protected void bindView(View viewO) {
        WebView view = viewO.findViewById(android.R.id.content);
        viewO.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            viewO.getLayoutParams().height = MATCH_PARENT;
            viewO.getLayoutParams().width = MATCH_PARENT;
            view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                view.getLayoutParams().height = MATCH_PARENT;
                view.getLayoutParams().width = MATCH_PARENT;
            });
        });
        view.setWebViewClient(new WebViewClient() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
                try {
                    if (FeedPersistenceKt.getFeedPrefs(
                            WebViewScreen.this).getEnableHostsFilteringInWebView() && WebSafety.INSTANCE.shouldBlock(
                            request.getUrl().getAuthority(), request.getUrl().toString())) {
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
        configurer.accept(view);
    }
}

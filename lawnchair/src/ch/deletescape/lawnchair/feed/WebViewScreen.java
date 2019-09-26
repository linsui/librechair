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
import android.webkit.WebView;
import android.widget.LinearLayout;

import java.util.function.Consumer;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class WebViewScreen extends ProviderScreen {
    private final String uri;
    private final Consumer<WebView> configurer;

    public WebViewScreen(Context base, String uri, Consumer<WebView> configurer) {
        super(base);
        this.uri = uri;
        this.configurer = configurer;
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
        view.loadUrl(uri);
        configurer.accept(view);
    }
}

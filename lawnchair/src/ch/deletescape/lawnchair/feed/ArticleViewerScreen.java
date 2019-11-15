/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.text.Html;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.util.FeedUtil;
import ch.deletescape.lawnchair.feed.web.WebViewScreen;
import io.github.cdimascio.essence.Essence;

public class ArticleViewerScreen extends ProviderScreen {

    private final String title;
    private final String categories;
    private final String url;
    private final String desc;
    private ScrollView sv;

    public ArticleViewerScreen(Context base, String title, String categories, String url,
                               String desc) {
        super(base);
        this.title = title;
        this.categories = categories;
        this.url = url;
        this.desc = desc;

        addAction(new FeedProvider.Action(getDrawable(R.drawable.ic_share_black_24dp),
                getString(getResources()
                        .getIdentifier("whichSendApplicationLabel", "string", "android")), () -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, url);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }));
    }

    @Override
    protected View getView(ViewGroup parent) {
        return LawnchairUtilsKt.inflate(parent, R.layout.overlay_article);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindView(View articleView) {
        TextView titleView = articleView.findViewById(R.id.title);
        TextView contentView = articleView.findViewById(R.id.content);
        sv = (ScrollView) contentView.getParent();
        SwipeRefreshLayout swipeRefreshLayout = articleView.findViewById(
                R.id.article_refresh_layout);
        if (getBoundFeed() != null) {
            swipeRefreshLayout.setColorSchemeColors(getBoundFeed()
                    .getTabColours()
                    .stream()
                    .mapToInt(it -> it)
                    .toArray());
        }
        swipeRefreshLayout.setRefreshing(true);
        Button openInBrowser = articleView.findViewById(R.id.open_externally);
        GestureDetector detector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        try {
                            WebViewScreen screen = WebViewScreen.obtain(ArticleViewerScreen.this,
                                    url);
                            screen.display(ArticleViewerScreen.this,
                                    StrictMath.round(LawnchairUtilsKt.getPostionOnScreen(
                                            openInBrowser).getFirst() + e.getX()),
                                    StrictMath.round(LawnchairUtilsKt.getPostionOnScreen(
                                            openInBrowser).getSecond() + e.getY()));
                        } catch (IllegalStateException e2) {
                            Utilities.openURLinBrowser(ArticleViewerScreen.this, url);
                        }
                        return super.onSingleTapUp(e);
                    }
                });
        openInBrowser.setOnTouchListener((v, event) -> detector.onTouchEvent(event));
        openInBrowser.setTextColor(FeedAdapter.Companion.getOverrideColor(articleView.getContext(),
                LawnchairUtilsKt.getColorEngineAccent(this)));
        titleView.setText(title);
        TextView categoriesView = articleView
                .findViewById(R.id.article_categories);
        categoriesView.setText(categories);
        swipeRefreshLayout.setOnRefreshListener(() -> FeedUtil.download(url, this, is -> {
            try {
                CharSequence text = Essence
                        .extract(IOUtils.toString(is, Charset.defaultCharset())).getText();
                if (text.toString().trim().isEmpty()) {
                    text = Html.fromHtml(desc, 0);
                }
                CharSequence finalText = text;
                contentView.post(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    contentView.setText(finalText);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, throwable -> {
            articleView.findViewById(R.id.article_viewer_error).setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }));
        ((ImageView) articleView.findViewById(R.id.article_viewer_error_icon)).setImageTintList(
                ColorStateList.valueOf(FeedAdapter.Companion.getOverrideColor(this)));
    }

    @Override
    public boolean onBackPressed() {
        if (sv.getScrollY() != 0) {
            sv.smoothScrollTo(0, 0);
            return true;
        }
        return false;
    }
}

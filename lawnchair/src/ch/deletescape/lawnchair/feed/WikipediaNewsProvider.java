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

/*
 * I spent 5 hours trying to think up an unbiased news provider and came up with this
 * Just proves how biased the world is, and how much we need WP:NoPointOfView
 */

package ch.deletescape.lawnchair.feed;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.webkit.WebView;
import ch.deletescape.lawnchair.theme.ThemeManager;
import com.android.launcher3.R;
import fastily.jwiki.core.Wiki;
import info.bliki.wiki.model.WikiModel;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class WikipediaNewsProvider extends FeedProvider {

    private Drawable newsIcon;
    private Wiki wikipedia;
    private String wikiText;

    public WikipediaNewsProvider(Context c) {
        super(c);
        this.newsIcon = c.getDrawable(R.drawable.ic_assessment_black_24dp).getConstantState()
                .newDrawable().mutate();
        this.newsIcon.setTint(c.getColor(R.color.colorAccent));
        Executors.newSingleThreadExecutor().submit(() -> {
                this.wikipedia = new Wiki("en.wikipedia.org");
                this.wikipedia.conf.userAgent = "Librechair";
            while (wikiText == null) {
                wikiText = wikipedia.getPageText("Template:In the news");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onFeedShown() {

    }

    @Override
    public void onFeedHidden() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public List<Card> getCards() {
        return wikiText == null ? Collections.emptyList() : Collections.singletonList(
                new Card(newsIcon, getContext().getString(R.string.title_feed_card_wikipedia_news),
                        item -> {
                            WebView webView = new WebView(getContext());
                            if (wikiText != null) {
                                webView.setBackgroundColor(Color.TRANSPARENT);
                                if (ThemeManager.Companion.getInstance(webView.getContext())
                                        .isDark()) {
                                    try {
                                        webView.loadData("<font color=\"white\">" + new WikiModel("https://commons.wikipedia.org",
                                                        "https://en.wikipedia.org").render(wikiText) + "</font>",
                                                "text/html", "utf-8");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        webView.loadData(
                                                new WikiModel("https://commons.wikipedia.org",
                                                        "https://en.wikipedia.org")
                                                        .render(wikiText),
                                                "text/html", "utf-8");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return webView;
                            }
                            return new View(getContext());
                        }, Card.Companion.getRAISE(), null));
    }
}

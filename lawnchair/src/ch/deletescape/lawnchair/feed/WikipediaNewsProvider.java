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
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import com.android.launcher3.R;

import ch.deletescape.lawnchair.feed.wikipedia.news.ITNAdapter;
import ch.deletescape.lawnchair.feed.wikipedia.news.News;
import fastily.jwiki.core.Wiki;
import info.bliki.wiki.model.WikiModel;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class WikipediaNewsProvider extends FeedProvider {

    private Drawable newsIcon;
    private ITNAdapter adapter;

    public WikipediaNewsProvider(Context c) {
        super(c);
        this.newsIcon = c.getDrawable(R.drawable.ic_assessment_black_24dp).getConstantState()
                .newDrawable().mutate();
        this.newsIcon.setTint(FeedAdapter.Companion.getOverrideColor(c));
        News.addListener(items -> {
            adapter = new ITNAdapter(items);
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
        return adapter == null ? Collections.emptyList() : Collections.singletonList(
                new Card(newsIcon, getContext().getString(R.string.title_feed_card_wikipedia_news),
                        item -> {
                            RecyclerView view = new RecyclerView(item.getContext());
                            view.setAdapter(adapter);
                            view.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
                            view.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            return view;
                        }, Card.Companion.getRAISE(), null, getContext().getString(R.string.title_feed_card_wikipedia_news).hashCode()));
    }
}

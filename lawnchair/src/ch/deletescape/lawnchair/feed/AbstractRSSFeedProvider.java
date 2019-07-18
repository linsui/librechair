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

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.prof.rssparser.Article;
import com.squareup.picasso.Picasso.Builder;
import java.util.Collections;
import java.util.List;

public abstract class AbstractRSSFeedProvider extends FeedProvider {

    private List<Article> articles;

    public AbstractRSSFeedProvider(Context c) {
        super(c);
        bindFeed(feed1 -> {
            Log.d(getClass().getName(), "constructor: bound to feed");
            articles = feed1;
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
        if (articles == null) {
            Log.d(getClass().getName(), "getCards: feed is null; returning empty list");
            return Collections.emptyList();
        } else {
            List<Card> cards = LawnchairUtilsKt.newList();
            Log.d(getClass().getName(),
                    "getCards: iterating through entries: " + articles.toString());
            for (Article entry : articles) {
                Log.d(getClass().getName(), "getCards: syndication entry: " + entry);
                cards.add(new Card(null, null, parent -> {
                    Log.d(getClass().getName(), "getCards: inflate syndication: " + entry);
                    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.rss_item, parent, false);
                    TextView title, description, date;
                    ImageView icon;
                    Button readMore;

                    title = v.findViewById(R.id.rss_item_title);
                    description = v.findViewById(R.id.rss_item_description);
                    icon = v.findViewById(R.id.rss_item_icon);
                    date = v.findViewById(R.id.rss_item_date);
                    readMore = v.findViewById(R.id.rss_item_read_more);

                    Log.d(getClass().getSimpleName(), "inflate: Image URL is " + entry.getImage());


                    if (entry.getImage() != null && entry.getImage().startsWith("http")) {
                        new Builder(parent.getContext()).build().load(entry.getImage())
                                .placeholder(R.mipmap.ic_launcher).into(icon);
                    } else {
                        new Builder(parent.getContext()).build().load("https:" + entry.getImage())
                                .placeholder(R.mipmap.ic_launcher).into(icon);
                    }

                    title.setText(String.format("%s: %s", FeedController.Companion
                            .getDisplayName(getClass().getName(), getContext()), entry.getTitle()));
                    String spanned = Html.fromHtml(entry.getDescription(), 0).toString();
                    if (spanned.length() > 256) {
                        spanned = spanned.subSequence(0, 256).toString() + "...";
                    }
                    description.setText(spanned);
                    readMore.setOnClickListener(v2 -> {
                        Utilities.openURLinBrowser(v2.getContext(), entry.getLink());
                    });

                    date.setText(entry.getPubDate());
                    return v;
                }, Card.Companion.getRAISE() | Card.Companion.getTEXT_ONLY(), null));
            }
            return cards;
        }
    }

    protected abstract void bindFeed(BindCallback callback);

    protected interface BindCallback {

        void onBind(List<Article> feed);
    }
}

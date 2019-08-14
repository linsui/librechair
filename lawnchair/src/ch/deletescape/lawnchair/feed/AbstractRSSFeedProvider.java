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
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.squareup.picasso.Picasso.Builder;
import io.github.cdimascio.essence.Essence;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import kotlin.Unit;
import org.apache.commons.io.IOUtils;

public abstract class AbstractRSSFeedProvider extends FeedProvider {

    private SyndFeed articles;
    private long lastUpdate;

    public AbstractRSSFeedProvider(Context c) {
        super(c);
        bindFeed(feed1 -> {
            Log.d(getClass().getName(), "constructor: bound to feed");
            lastUpdate = System.currentTimeMillis();
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
        if (System.currentTimeMillis() - lastUpdate > TimeUnit.MINUTES.toMinutes(15)) {
            bindFeed(feed1 -> {
                Log.d(getClass().getName(), "constructor: bound to feed");
                lastUpdate = System.currentTimeMillis();
                articles = feed1;
            });
        }
        if (articles == null) {
            Log.d(getClass().getName(), "getCards: feed is null; returning empty list");
            return Collections.emptyList();
        } else {
            List<Card> cards = LawnchairUtilsKt.newList();
            Log.d(getClass().getName(),
                    "getCards: iterating through entries: " + articles.toString());
            for (SyndEntry entry : articles.getEntries()) {
                Log.d(getClass().getName(), "getCards: syndication entry: " + entry);
                Card card = new Card(null, null, parent -> {
                    Log.d(getClass().getName(), "getCards: inflate syndication: " + entry);
                    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.rss_item, parent, false);
                    TextView title, description, date, categories;
                    ImageView icon;
                    Button readMore;

                    title = v.findViewById(R.id.rss_item_title);
                    description = v.findViewById(R.id.rss_item_description);
                    categories = v.findViewById(R.id.rss_item_categories);
                    icon = v.findViewById(R.id.rss_item_icon);
                    date = v.findViewById(R.id.rss_item_date);
                    readMore = v.findViewById(R.id.rss_item_read_more);

                    Log.d(getClass().getSimpleName(),
                            "inflate: Image URL is " + LawnchairUtilsKt.getThumbnailURL(entry));

                    if (LawnchairUtilsKt.getThumbnailURL(entry) != null && LawnchairUtilsKt
                            .getThumbnailURL(entry).startsWith("http")) {
                        new Builder(parent.getContext()).build()
                                .load(LawnchairUtilsKt.getThumbnailURL(entry))
                                .placeholder(R.mipmap.ic_launcher).into(icon);
                    } else {
                        new Builder(parent.getContext()).build()
                                .load("https:" + LawnchairUtilsKt.getThumbnailURL(entry))
                                .placeholder(R.mipmap.ic_launcher).into(icon);
                    }

                    title.setText(String.format("%s: %s", MainFeedController.Companion
                            .getDisplayName(getClass().getName(), getContext()), entry.getTitle()));
                    String spanned = Html.fromHtml(entry.getDescription().getValue(), 0).toString();
                    if (spanned.length() > 256) {
                        spanned = spanned.subSequence(0, 256).toString() + "...";
                    }
                    description.setText(spanned);
                    readMore.setOnClickListener(v2 -> {
                        if (getFeed() != null) {
                            getFeed().displayView((parent2) -> {
                                ViewGroup articleView = (ViewGroup) LayoutInflater
                                        .from(getContext())
                                        .inflate(R.layout.overlay_article, parent2, false);
                                articleView.setBackgroundColor(
                                        LawnchairUtilsKt.setAlpha(getBackgroundColor(), 255));
                                TextView titleView = articleView.findViewById(R.id.title);
                                TextView contentView = articleView.findViewById(R.id.content);
                                articleView.findViewById(R.id.open_externally)
                                        .setOnClickListener(v3 -> Utilities
                                                .openURLinBrowser(getContext(), entry.getUri()));
                                titleView.setText(entry.getTitle());
                                Executors.newSingleThreadExecutor().submit(() -> {
                                    try {
                                        String text = Essence.extract(IOUtils.toString(
                                                new URL(entry.getUri()).openStream(), Charset
                                                        .defaultCharset())).getText();
                                        contentView.post(() -> contentView.setText(text));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                return articleView;
                            });
                        }
                    });

                    if (entry.getCategories().isEmpty()) {
                        categories.setText("");
                    } else {
                        categories.setText(String.join(", ",
                                entry.getCategories().stream().map(entry2 -> entry2.getName())
                                        .collect(
                                                Collectors.toList())));
                    }

                    if (entry.getPublishedDate() != null) {
                        date.setText(entry.getPublishedDate().toLocaleString());
                    } else {
                        date.setText(null);
                    }
                    return v;
                }, Card.Companion.getRAISE() | Card.Companion.getTEXT_ONLY(), null,
                        entry.hashCode(), true,
                        entry.getCategories().stream().map(entry2 -> entry2.getName()).collect(
                                Collectors.toList()));
                cards.add(card);
                card.setActionName(getContext().getString(getContext().getResources()
                        .getIdentifier("whichSendApplicationLabel", "string", "android")));
                card.setActionListener(context -> {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, entry.getLink());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    return Unit.INSTANCE;
                });
            }
            return cards;
        }
    }

    protected abstract void bindFeed(BindCallback callback);

    protected interface BindCallback {

        void onBind(SyndFeed feed);
    }
}

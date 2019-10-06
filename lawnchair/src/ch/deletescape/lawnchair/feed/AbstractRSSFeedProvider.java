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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.squareup.picasso.Picasso.Builder;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.clickbait.ClickbaitRanker;
import ch.deletescape.lawnchair.feed.cache.CacheManager;
import ch.deletescape.lawnchair.persistence.FeedPersistence;
import kotlin.Unit;

public abstract class AbstractRSSFeedProvider extends FeedProvider {

    private SyndFeed articles;
    private List<Card> cardCache;
    private long lastUpdate;

    public AbstractRSSFeedProvider(Context c) {
        super(c);
        Executors.newSingleThreadExecutor().submit(() -> {
            byte[] cache;
            if ((cache = CacheManager.Companion.getInstance(c).getCachedBytes(getClass().getName(),
                    "cached_feed")).length >= 1) {
                try {
                    articles = new SyndFeedInput().build(
                            new InputSource(new ByteArrayInputStream(cache)));
                } catch (FeedException e) {
                    bindFeed(feed1 -> {
                        Log.d(AbstractRSSFeedProvider.this.getClass().getName(),
                                "constructor: bound to feed");
                        lastUpdate = System.currentTimeMillis();
                        articles = feed1;
                        ByteArrayOutputStream cachedFeed = new ByteArrayOutputStream();
                        SyndFeedOutput output = new SyndFeedOutput();
                        try {
                            output.output(feed1, new OutputStreamWriter(cachedFeed));
                            CacheManager.Companion.getInstance(
                                    AbstractRSSFeedProvider.this.getContext()).writeCache(
                                    cachedFeed.toByteArray(),
                                    AbstractRSSFeedProvider.this.getClass().getName(),
                                    "cache_feed", TimeUnit.HOURS.toMillis(1));
                        } catch (IOException | FeedException e2) {
                            e.printStackTrace();
                        }
                    });
                    e.printStackTrace();
                }
            }
            bindFeed(feed -> {
                Log.d(AbstractRSSFeedProvider.this.getClass().getName(),
                        "constructor: bound to feed");
                lastUpdate = System.currentTimeMillis();
                articles = feed;
                ByteArrayOutputStream cachedFeed = new ByteArrayOutputStream();
                SyndFeedOutput output = new SyndFeedOutput();
                try {
                    output.output(feed, new OutputStreamWriter(cachedFeed));
                    CacheManager.Companion.getInstance(
                            AbstractRSSFeedProvider.this.getContext()).writeCache(
                            cachedFeed.toByteArray(),
                            AbstractRSSFeedProvider.this.getClass().getName(), "cache_feed",
                            TimeUnit.HOURS.toMillis(1));
                } catch (IOException | FeedException e) {
                    e.printStackTrace();
                }
            });
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
            cardCache = null;
            Executors.newSingleThreadExecutor().submit(() -> {
                byte[] cache;
                if ((cache = CacheManager.Companion.getInstance(getContext()).getCachedBytes(
                        getClass().getName(),
                        "cached_feed")).length >= 1) {
                    try {
                        articles = new SyndFeedInput().build(
                                new InputSource(new ByteArrayInputStream(cache)));
                    } catch (FeedException e) {
                        bindFeed(feed1 -> {
                            Log.d(AbstractRSSFeedProvider.this.getClass().getName(),
                                    "constructor: bound to feed");
                            lastUpdate = System.currentTimeMillis();
                            articles = feed1;
                            ByteArrayOutputStream cachedFeed = new ByteArrayOutputStream();
                            SyndFeedOutput output = new SyndFeedOutput();
                            try {
                                output.output(feed1, new OutputStreamWriter(cachedFeed));
                                CacheManager.Companion.getInstance(
                                        AbstractRSSFeedProvider.this.getContext()).writeCache(
                                        cachedFeed.toByteArray(),
                                        AbstractRSSFeedProvider.this.getClass().getName(),
                                        "cache_feed", TimeUnit.HOURS.toMillis(1));
                            } catch (IOException | FeedException e2) {
                                e.printStackTrace();
                            }
                        });
                        e.printStackTrace();
                    }
                }
                bindFeed(feed -> {
                    Log.d(AbstractRSSFeedProvider.this.getClass().getName(),
                            "constructor: bound to feed");
                    lastUpdate = System.currentTimeMillis();
                    articles = feed;
                    ByteArrayOutputStream cachedFeed = new ByteArrayOutputStream();
                    SyndFeedOutput output = new SyndFeedOutput();
                    try {
                        output.output(feed, new OutputStreamWriter(cachedFeed));
                        CacheManager.Companion.getInstance(
                                AbstractRSSFeedProvider.this.getContext()).writeCache(
                                cachedFeed.toByteArray(),
                                AbstractRSSFeedProvider.this.getClass().getName(), "cache_feed",
                                TimeUnit.HOURS.toMillis(1));
                    } catch (IOException | FeedException e) {
                        e.printStackTrace();
                    }
                });
            });
        }
        if (articles == null) {
            Log.d(getClass().getName(), "getCards: feed is null; returning empty list");
            return Collections.emptyList();
        } else if (cardCache != null && !cardCache.isEmpty()) {
            return cardCache;
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

                    readMore.setTextColor(
                            FeedAdapter.Companion.getOverrideColor(parent.getContext()));

                    Log.d(getClass().getSimpleName(),
                            "inflate: Image URL is " + LawnchairUtilsKt.getThumbnailURL(entry));

                    if (LawnchairUtilsKt.getThumbnailURL(entry) != null && LawnchairUtilsKt
                            .getThumbnailURL(entry).startsWith("http")) {
                        new Builder(parent.getContext()).build()
                                .load(LawnchairUtilsKt.getThumbnailURL(entry))
                                .placeholder(R.drawable.work_tab_user_education).into(icon);
                    } else {
                        new Builder(parent.getContext()).build()
                                .load("https:" + LawnchairUtilsKt.getThumbnailURL(entry))
                                .placeholder(R.drawable.work_tab_user_education).into(icon);
                    }

                    title.setText(ClickbaitRanker.completePipeline(entry.getTitle()));
                    String spanned = entry.getDescription() != null ? Html.fromHtml(
                            entry.getDescription().getValue(), 0).toString() : "";
                    if (spanned.length() > 256) {
                        spanned = spanned.subSequence(0, 256).toString() + "...";
                    }
                    description.setText(spanned);
                    GestureDetector detector = new GestureDetector(getContext(),
                            new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onSingleTapUp(MotionEvent e) {
                                    if (!FeedPersistence.Companion.getInstance(getContext()).getDirectlyOpenLinksInBrowser()) {
                                        new ArticleViewerScreen(getContext(), entry.getTitle(),
                                                entry.getCategories().stream().map(
                                                        it -> it.getName())
                                                        .collect(Collectors.joining(", ")),
                                                entry.getLink(),
                                                entry.getDescription() != null ? entry.getDescription().getValue() : "")
                                                .display(AbstractRSSFeedProvider.this,
                                                        (LawnchairUtilsKt.getPostionOnScreen(
                                                                readMore).getFirst() + Math.round(
                                                                e.getX())),
                                                        (LawnchairUtilsKt.getPostionOnScreen(
                                                                readMore).getSecond() + Math.round(
                                                                e.getY())));
                                    } else {
                                        Utilities.openURLinBrowser(getContext(), entry.getLink());
                                    }
                                    return true;
                                }
                            });
                    readMore.setOnTouchListener((_v, e) -> detector.onTouchEvent(e));

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
            cardCache = cards;
            return cards;
        }
    }

    @Override
    public List<Card> getPreviewItems() {
        List<Card> cards = new ArrayList<>();
        SyndEntry entry = new SyndEntryImpl();
        entry.setUri("https://placeholder");
        entry.setTitle(getContext().getString(R.string.loading));
        entry.setDescription(new SyndContent() {
            @Override
            public String getType() {
                return null;
            }

            @Override
            public void setType(String type) {

            }

            @Override
            public String getMode() {
                return null;
            }

            @Override
            public void setMode(String mode) {

            }

            @Override
            public String getValue() {
                return "████████████████████████████████████████████████████\n\n" +
                        "█████████████████████████████████████████████" +
                        "██" +
                        "\n" +
                        "██████████████████████████" +
                        "███" +
                        "\n" +
                        "██████████████";
            }

            @Override
            public void setValue(String value) {

            }

            @Override
            public Class<? extends CopyFrom> getInterface() {
                return null;
            }

            @Override
            public void copyFrom(CopyFrom obj) {

            }

            @Override
            public Object clone() throws CloneNotSupportedException {
                return null;
            }
        });
        Card card = new Card(null, null, parent -> {
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

            readMore.setTextColor(FeedAdapter.Companion.getOverrideColor(parent.getContext()));

            Log.d(getClass().getSimpleName(),
                    "inflate: Image URL is " + LawnchairUtilsKt.getThumbnailURL(entry));

            if (LawnchairUtilsKt.getThumbnailURL(entry) != null && LawnchairUtilsKt
                    .getThumbnailURL(entry).startsWith("http")) {
                new Builder(parent.getContext()).build()
                        .load(LawnchairUtilsKt.getThumbnailURL(entry))
                        .placeholder(R.drawable.work_tab_user_education).into(icon);
            } else {
                new Builder(parent.getContext()).build()
                        .load("https:" + LawnchairUtilsKt.getThumbnailURL(entry))
                        .placeholder(R.drawable.work_tab_user_education).into(icon);
            }

            title.setText(ClickbaitRanker.completePipeline(entry.getTitle()));
            String spanned = entry.getDescription() != null ? Html.fromHtml(
                    entry.getDescription().getValue(), 0).toString() : "";
            if (spanned.length() > 256) {
                spanned = spanned.subSequence(0, 256).toString() + "...";
            }
            description.setText(spanned);
            readMore.setOnClickListener(v2 -> {
                new ArticleViewerScreen(getContext(), entry.getTitle(),
                        entry.getCategories().stream().map(it -> it.getName())
                                .collect(Collectors.joining(", ")),
                        entry.getLink(),
                        entry.getDescription() != null ? entry.getDescription().getValue() : "")
                        .display(this, (LawnchairUtilsKt.getPostionOnScreen(v2).getFirst()
                                        + v2.getWidth() / 2),
                                (LawnchairUtilsKt.getPostionOnScreen(v2).getSecond()
                                        + v2.getHeight() / 2));
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
        for (int i = 0; i < 2; ++i) {
            cards.add(card);
        }
        return cards;
    }

    protected abstract void bindFeed(BindCallback callback);

    protected interface BindCallback {
        void onBind(SyndFeed feed);
    }
}

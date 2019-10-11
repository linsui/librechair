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
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.squareup.picasso.Picasso.Builder;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.clickbait.ClickbaitRanker;
import ch.deletescape.lawnchair.feed.cache.CacheManager;
import ch.deletescape.lawnchair.feed.jobs.JobSchedulerService;
import ch.deletescape.lawnchair.feed.notifications.NotificationManager;
import ch.deletescape.lawnchair.persistence.FeedPersistence;
import ch.deletescape.lawnchair.persistence.FeedPersistenceKt;
import kotlin.Unit;

public abstract class AbstractRSSFeedProvider extends FeedProvider {

    private SyndFeed articles;
    private List<Card> cardCache;
    private JobScheduler scheduler;
    private long lastUpdate;

    public AbstractRSSFeedProvider(Context c) {
        super(c);
        scheduler = (JobScheduler) c.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobSchedulerService.Companion.getIdCallbacks().put(hashCode() << 1, unitFunction1 -> {
            refresh(c, () -> unitFunction1.invoke(false),
                    FeedPersistenceKt.getFeedPrefs(c).getNotifyUsersAboutNewArticlesOnFirstRun());
            JobSchedulerService.Companion.getIdCallbacks().remove(hashCode() << 1);
            return Unit.INSTANCE;
        });
        JobSchedulerService.Companion.getIdCallbacks().put(hashCode(), unitFunction1 -> {
            refresh(c, () -> unitFunction1.invoke(false), true);
            return Unit.INSTANCE;
        });
        scheduler.schedule(
                new JobInfo.Builder(hashCode(), new ComponentName(c, JobSchedulerService.class))
                        .setPersisted(false)
                        .setRequiresBatteryNotLow(true)
                        .setPeriodic(TimeUnit.HOURS.toMillis(1), TimeUnit.MINUTES.toMillis(10))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build());
        scheduler.schedule(
                new JobInfo.Builder(hashCode() << 1,
                        new ComponentName(c, JobSchedulerService.class))
                        .setPersisted(false)
                        .setRequiresBatteryNotLow(false)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setMinimumLatency(1)
                        .setOverrideDeadline(1)
                        .build());
    }

    private void refresh(Context c, Runnable finished, boolean diff) {
        Executors.newSingleThreadExecutor().submit(() -> {
            byte[] cache;
            if ((cache = CacheManager.Companion.getInstance(c).getCachedBytes(getClass().getName(),
                    "cached_feed")).length >= 1) {
                try {
                    SyndFeed old = articles;
                    articles = new SyndFeedInput().build(
                            new InputSource(new ByteArrayInputStream(cache)));
                    finished.run();
                    if (diff) {
                        showNotifications(old != null ? old : new SyndFeedImpl());
                    }
                } catch (FeedException e) {
                    bindFeed(feed1 -> {
                        Log.d(AbstractRSSFeedProvider.this.getClass().getName(),
                                "constructor: bound to feed");
                        lastUpdate = System.currentTimeMillis();
                        SyndFeed old = articles;
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
                        if (diff) {
                            showNotifications(old != null ? old : new SyndFeedImpl());
                        }
                        finished.run();
                    });
                    e.printStackTrace();
                }
            } else {
                bindFeed(feed -> {
                    Log.d(AbstractRSSFeedProvider.this.getClass().getName(),
                            "constructor: bound to feed");
                    lastUpdate = System.currentTimeMillis();
                    SyndFeed old = articles;
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
                    if (diff) {
                        showNotifications(old != null ? old : new SyndFeedImpl());
                    }
                    finished.run();
                });
            }
        });
    }

    private void showNotifications(SyndFeed original) {
        if (!articles.getEntries().isEmpty()) {
            for (SyndEntry notif : articles.getEntries().subList(
                    articles.getEntries().size() < 5 ? articles.getEntries().size() : articles.getEntries().size() - 5
                    , articles.getEntries().size() - 1)) {
                if (!original.getEntries().contains(
                        notif) && notif.getTitle() != null && notif.getDescription() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(notif.getLink()));
                    PendingIntent intent2 = PendingIntent.getActivity(getContext(), 0, intent, 0);
                    if (notif.getDescription().getValue().length() > 250) {
                        NotificationManager.getInstance(getContext())
                                .postNotification(this, R.drawable.ic_newspaper_24dp,
                                        notif.getTitle(),
                                        Html.fromHtml(notif.getDescription().getValue(), 0).toString().substring(0, 250) + "...",
                                        intent2);
                    } else {
                        NotificationManager.getInstance(getContext())
                                .postNotification(this, R.drawable.ic_newspaper_24dp,
                                        notif.getTitle(),
                                        Html.fromHtml(notif.getDescription().getValue(), 0).toString(), intent2);
                    }
                }
            }
        }
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
    public boolean isVolatile() {
        return cardCache == null || cardCache.isEmpty();
    }

    @Override
    public List<Card> getCards() {
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
                @SuppressLint("ClickableViewAccessibility") Card card = new Card(null, null,
                        parent -> {
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
                                    "inflate: Image URL is " + LawnchairUtilsKt.getThumbnailURL(
                                            entry));

                            if (LawnchairUtilsKt.getThumbnailURL(
                                    entry) != null && Objects.requireNonNull(
                                    LawnchairUtilsKt.getThumbnailURL(entry)).startsWith("http")) {
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
                                            if (!FeedPersistence.Companion.getInstance(
                                                    getContext()).getDirectlyOpenLinksInBrowser()) {
                                                new ArticleViewerScreen(getContext(),
                                                        entry.getTitle(),
                                                        entry.getCategories().stream().map(
                                                                SyndCategory::getName)
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
                                                Utilities.openURLinBrowser(getContext(),
                                                        entry.getLink());
                                            }
                                            return true;
                                        }
                                    });
                            readMore.setOnTouchListener((_v, e) -> detector.onTouchEvent(e));

                            if (entry.getCategories().isEmpty()) {
                                categories.setText("");
                            } else {
                                categories.setText(
                                        entry.getCategories().stream().map(SyndCategory::getName)
                                                .collect(Collectors.joining(", ")));
                            }

                            if (entry.getPublishedDate() != null) {
                                date.setText(entry.getPublishedDate().toString());
                            } else {
                                date.setText(null);
                            }
                            return v;
                        }, Card.Companion.getRAISE() | Card.Companion.getTEXT_ONLY(), null,
                        entry.hashCode(), true,
                        entry.getCategories().stream().map(SyndCategory::getName).collect(
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

    protected abstract void bindFeed(BindCallback callback);

    protected interface BindCallback {
        void onBind(SyndFeed feed);
    }
}

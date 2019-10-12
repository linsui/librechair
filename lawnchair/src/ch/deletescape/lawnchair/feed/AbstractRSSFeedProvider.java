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

import android.annotation.AnyThread;
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
import com.rometools.rome.feed.synd.SyndFeed;
import com.squareup.picasso.Picasso.Builder;

import org.jetbrains.annotations.Contract;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.clickbait.ClickbaitRanker;
import ch.deletescape.lawnchair.feed.jobs.JobSchedulerService;
import ch.deletescape.lawnchair.feed.news.NewsDb;
import ch.deletescape.lawnchair.feed.news.NewsEntry;
import ch.deletescape.lawnchair.feed.notifications.NotificationManager;
import ch.deletescape.lawnchair.persistence.FeedPersistence;
import ch.deletescape.lawnchair.persistence.FeedPersistenceKt;
import kotlin.Unit;

public abstract class AbstractRSSFeedProvider extends FeedProvider {

    private List<NewsEntry> articles;
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
        scheduler.cancel(hashCode());
        scheduler.cancel(hashCode() << 1);
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

    @AnyThread
    @Contract("null, null, _ -> fail")
    protected void refresh(Context c, Runnable finished, boolean diff) {
        Executors.newSingleThreadExecutor().submit(() -> {
            List<NewsEntry> entries;
            if ((entries = NewsDb.getDatabase(c, getId()).open().all()).isEmpty() ||
                    entries.stream().allMatch(
                            it -> it.date != null && it.date.getTime() - System.currentTimeMillis() > TimeUnit.HOURS.toMillis(
                                    2))) {
                bindFeed(feed -> {
                    Log.d(AbstractRSSFeedProvider.this.getClass().getName(),
                            "constructor: bound to feed");
                    lastUpdate = System.currentTimeMillis();
                    List<NewsEntry> old = articles;
                    articles = feed.getEntries().stream().map(entry -> {
                        NewsEntry newsEntry = new NewsEntry();
                        newsEntry.date = entry.getPublishedDate();
                        newsEntry.content = entry.getDescription().getValue();
                        newsEntry.url = entry.getLink();
                        newsEntry.title = entry.getTitle();
                        newsEntry.categories = entry.getCategories()
                                .stream()
                                .map(SyndCategory::getName)
                                .collect(Collectors.toList());
                        newsEntry.thumbnail = LawnchairUtilsKt.getThumbnailURL(entry);
                        return newsEntry;
                    }).collect(Collectors.toList());
                    NewsDb.getDatabase(c, getId()).open().purge();
                    articles.stream()
                            .peek(it -> it.order = articles.indexOf(it))
                            .forEach(it -> NewsDb.getDatabase(c, getId()).open().insert(it));
                    if (diff) {
                        showNotifications(old != null ? old : Collections.emptyList());
                    }
                    finished.run();
                });
            } else {
                List<NewsEntry> old = articles;
                articles = entries;
                if (diff) {
                    showNotifications(old != null ? old : Collections.emptyList());
                }
            }
        });
    }

    private void showNotifications(List<NewsEntry> original) {
        if (!articles.isEmpty()) {
            for (NewsEntry notif : articles.subList(
                    articles.size() < (int) Math.round(
                            FeedPersistence.Companion.getInstance(
                                    getContext()).getNotificationCount()) ? articles.size() : articles.size() - (int) Math.round(
                            FeedPersistence.Companion.getInstance(
                                    getContext()).getNotificationCount())
                    , articles.size() - 1)) {
                if (original.stream().noneMatch(entry -> entry.title.equals(
                        notif.title)) && notif != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(notif.url));
                    PendingIntent intent2 = PendingIntent.getActivity(getContext(), 0, intent, 0);
                    if (Html.fromHtml(notif.content,
                            0).toString().length() > 250) {
                        NotificationManager.getInstance(getContext())
                                .postNotification(this, R.drawable.ic_newspaper_24dp,
                                        notif.title,
                                        Html.fromHtml(notif.content,
                                                0).toString().substring(0, 250) + "...",
                                        intent2);
                    } else {
                        NotificationManager.getInstance(getContext())
                                .postNotification(this, R.drawable.ic_newspaper_24dp,
                                        notif.title,
                                        Html.fromHtml(notif.content,
                                                0).toString(), intent2);
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
        return articles == null || articles.isEmpty();
    }

    protected String getId() {
        return getClass().getName();
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
            boolean minicard = FeedPersistenceKt.getFeedPrefs(
                    getContext()).getUseRSSMinicard();
            for (NewsEntry entry : articles) {
                Log.d(getClass().getName(), "getCards: syndication entry: " + entry);
                @SuppressLint("ClickableViewAccessibility") Card card = new Card(null, null,
                        parent -> {
                            Log.d(getClass().getName(), "getCards: inflate syndication: " + entry);
                            View v = LayoutInflater.from(parent.getContext())
                                    .inflate(minicard ? R.layout.rss_miniitem : R.layout.rss_item,
                                            parent, false);
                            TextView title, description, date, categories;
                            ImageView icon;
                            Button readMore;

                            title = v.findViewById(R.id.rss_item_title);
                            description = v.findViewById(R.id.rss_item_description);
                            categories = v.findViewById(R.id.rss_item_categories);
                            icon = v.findViewById(R.id.rss_item_icon);
                            date = v.findViewById(R.id.rss_item_date);
                            readMore = v.findViewById(R.id.rss_item_read_more);

                            if (!minicard) {
                                readMore.setTextColor(
                                        FeedAdapter.Companion.getOverrideColor(
                                                parent.getContext()));
                            }

                            if (entry.thumbnail != null && Objects.requireNonNull(
                                    entry.thumbnail).startsWith("http")) {
                                new Builder(parent.getContext()).build()
                                        .load(entry.thumbnail)
                                        .placeholder(R.drawable.work_tab_user_education).into(
                                        icon);
                            } else {
                                new Builder(parent.getContext()).build()
                                        .load("https:" + entry.thumbnail)
                                        .placeholder(R.drawable.work_tab_user_education).into(
                                        icon);
                            }

                            title.setText(ClickbaitRanker.completePipeline(entry.title));
                            String spanned = entry.content != null ? Html.fromHtml(
                                    entry.content, 0).toString() : "";
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
                                                        entry.title,
                                                        entry.categories != null ?
                                                                String.join(", ",
                                                                        entry.categories) :
                                                                "",
                                                        entry.url,
                                                        entry.content != null ? entry.content : "")
                                                        .display(AbstractRSSFeedProvider.this,
                                                                (LawnchairUtilsKt.getPostionOnScreen(
                                                                        readMore).getFirst() + Math.round(
                                                                        e.getX())),
                                                                (LawnchairUtilsKt.getPostionOnScreen(
                                                                        readMore).getSecond() + Math.round(
                                                                        e.getY())));
                                            } else {
                                                Utilities.openURLinBrowser(getContext(),
                                                        entry.url);
                                            }
                                            return true;
                                        }
                                    });
                            (minicard ? v : readMore).setOnTouchListener(
                                    (_v, e) -> detector.onTouchEvent(e));

                            if (!minicard) {
                                if (entry.categories == null ||
                                        entry.categories.isEmpty()) {
                                    categories.setText("");
                                } else {
                                    categories.setText(
                                            String.join(", ", entry.categories));
                                }

                                if (entry.date != null) {
                                    date.setText(entry.date.toString());
                                } else {
                                    date.setText(null);
                                }
                            }
                            return v;
                        }, Card.Companion.getRAISE() | Card.Companion.getTEXT_ONLY(), null,
                        entry.hashCode(), true,
                        entry.categories != null ? entry.categories : Collections.emptyList());
                cards.add(card);
                card.setActionName(getContext().getString(getContext().getResources()
                        .getIdentifier("whichSendApplicationLabel", "string", "android")));
                card.setActionListener(context -> {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, entry.url);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    return Unit.INSTANCE;
                });
                if (minicard) {
                    card.globalClickListener = v -> {
                        if (!FeedPersistence.Companion.getInstance(
                                getContext()).getDirectlyOpenLinksInBrowser()) {
                            new ArticleViewerScreen(getContext(),
                                    entry.title,
                                    entry.categories != null ?
                                            String.join(", ",
                                                    entry.categories) :
                                            "",
                                    entry.url,
                                    entry.content != null ? entry.content : "")
                                    .display(AbstractRSSFeedProvider.this,
                                            (LawnchairUtilsKt.getPostionOnScreen(
                                                    v).getFirst() + v.getMeasuredWidth() / 2),
                                            (LawnchairUtilsKt.getPostionOnScreen(v).getSecond() + v.getMeasuredHeight() / 2));
                        } else {
                            Utilities.openURLinBrowser(getContext(),
                                    entry.url);
                        }
                        return Unit.INSTANCE;
                    };
                }
            }
            return cards;
        }
    }
    protected abstract void bindFeed(BindCallback callback);

    protected interface BindCallback {
        void onBind(SyndFeed feed);
    }
}

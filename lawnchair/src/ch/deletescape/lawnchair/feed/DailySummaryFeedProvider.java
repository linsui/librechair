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
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.android.launcher3.R;
import com.google.android.apps.nexuslauncher.graphics.IcuDateTextView;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.awareness.CalendarManager;
import ch.deletescape.lawnchair.awareness.SunriseSunsetManager;
import ch.deletescape.lawnchair.feed.util.FeedUtil;
import ch.deletescape.lawnchair.feed.util.Pairs;
import ch.deletescape.lawnchair.font.CustomFontManager;
import kotlin.Unit;

public class DailySummaryFeedProvider extends FeedProvider {
    private Pairs.Pair<ZonedDateTime, ZonedDateTime> sunriseSunset;
    private int calEvCount = 0;
    private long sunriseSunsetExpiry;

    @SuppressLint("MissingPermission")
    public DailySummaryFeedProvider(Context c) {
        super(c);
        CalendarManager.INSTANCE.subscribe(lst -> {
            calEvCount = (int) lst.stream().filter(
                    it -> it.getStartTime().isAfter(LocalDateTime.now()
                            .withHour(0)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0)) && it.getEndTime().isBefore(LocalDateTime.now()
                            .plusDays(1)
                            .withNano(0)
                            .withMinute(0)
                            .withHour(0)
                            .withSecond(0))).count();
            FeedUtil.runOnMainThread(this::markUnread);
            return Unit.INSTANCE;
        });
        SunriseSunsetManager.subscribe(ss -> {
            sunriseSunset = ss;
            markUnread();
        });
    }

    @Override
    public List<Card> getCards() {
        return Collections.singletonList(new Card(null, null,
                parent -> {
                    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.daily_summary, parent, false);
                    TextView dateTextView = v.findViewById(R.id.current_date);
                    dateTextView.setText(
                            IcuDateTextView.getDateFormat(getContext(), true, null, false)
                                    .format(System.currentTimeMillis()));
                    RecyclerView recyclerView = v.findViewById(R.id.daily_summary_information);
                    Adapter adapter = new Adapter(parent.getContext(), this);
                    recyclerView.setAdapter(adapter);
                    FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(
                            parent.getContext());
                    layoutManager.setFlexWrap(FlexWrap.WRAP);
                    layoutManager.setFlexDirection(FlexDirection.COLUMN);
                    layoutManager.setJustifyContent(JustifyContent.FLEX_START);
                    layoutManager.setAlignItems(AlignItems.CENTER);
                    recyclerView.setLayoutManager(
                            new GridLayoutManager(parent.getContext(),
                                    1));
                    adapter.refresh();
                    return v;
                },
                Card.RAISE | Card.NO_HEADER, "nosort,top",
                "dailySummary".hashCode()));
    }

    public static class Adapter extends RecyclerView.Adapter {

        private final Context context;
        private final DailySummaryFeedProvider feedProvider;
        private final List<DailySummaryItem> items = new ArrayList<>();

        public Adapter(Context context, DailySummaryFeedProvider feedProvider) {
            this.context = context;
            this.feedProvider = feedProvider;
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public void refresh() {
            items.clear();
            if (feedProvider.calEvCount > 0) {
                items.add(new DailySummaryItem(LawnchairUtilsKt
                        .tint(
                                Objects.requireNonNull(
                                        context.getDrawable(R.drawable.ic_event_black_24dp)),
                                FeedAdapter.Companion.getOverrideColor(context)),
                        context.getResources()
                                .getQuantityString(
                                        R.plurals.title_daily_briefing_calendar_events,
                                        feedProvider.calEvCount, feedProvider.calEvCount)));
            }

            if (feedProvider.sunriseSunset != null) {
                items.add(new DailySummaryItem(LawnchairUtilsKt
                        .tint(Objects.requireNonNull(
                                context.getDrawable(R.drawable.ic_sunrise_24dp)),
                                FeedAdapter.Companion.getOverrideColor(context)),
                        LawnchairUtilsKt
                                .formatTime(feedProvider.sunriseSunset.car(), context)));
                items.add(new DailySummaryItem(LawnchairUtilsKt
                        .tint(Objects.requireNonNull(
                                context.getDrawable(R.drawable.ic_sunset_24dp)),
                                FeedAdapter.Companion.getOverrideColor(context)),
                        LawnchairUtilsKt
                                .formatTime(feedProvider.sunriseSunset.cdr(), context)));
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.daily_summary_item, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DailySummaryItem item = items.get(position);
            ImageView imageView = holder.itemView.findViewById(R.id.daily_summary_icon);
            TextView title = holder.itemView.findViewById(R.id.daily_summary_information);
            imageView.setImageDrawable(item.icon);
            CustomFontManager.Companion.getInstance(context).loadFont(
                    CustomFontManager.FONT_FEED_CHIPS, title.getTypeface().getStyle(),
                    tf -> {
                        title.setTag("font_ignore");
                        title.setTypeface(tf);
                        return Unit.INSTANCE;
                    });
            title.setText(item.text);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

}

class DailySummaryItem {

    public Drawable icon;
    public String text;

    DailySummaryItem(Drawable icon, String text) {
        this.icon = icon;
        this.text = text;
    }
}
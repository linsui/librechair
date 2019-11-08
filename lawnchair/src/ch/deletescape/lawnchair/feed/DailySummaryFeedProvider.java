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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.util.Log;
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
import com.android.launcher3.util.Thunk;
import com.google.android.apps.nexuslauncher.graphics.IcuDateTextView;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import net.time4j.PlainDate;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.calendar.astro.Twilight;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import kotlin.Pair;
import kotlin.Unit;

public class DailySummaryFeedProvider extends FeedProvider {

    @Thunk
    Pair<ZonedDateTime, ZonedDateTime> sunriseSunset;
    private long sunriseSunsetExpiry;

    @SuppressLint("MissingPermission")
    public DailySummaryFeedProvider(Context c) {
        super(c);
        LawnchairUtilsKt.getLawnchairLocationManager(c).addCallback((lat, lon) -> {
            SolarTime solarTime = SolarTime.ofLocation(lat, lon);
            ZonedDateTime sunrise = ZonedDateTime.ofInstant(Instant.ofEpochSecond(
                    PlainDate.nowInSystemTime().get(
                            solarTime.sunrise(Twilight.ASTRONOMICAL)).inLocalView().getPosixTime()),
                    ZoneId.systemDefault());
            ZonedDateTime sunset = ZonedDateTime.ofInstant(Instant.ofEpochSecond(
                    PlainDate.nowInSystemTime().get(
                            solarTime.sunset(Twilight.ASTRONOMICAL)).inLocalView().getPosixTime()),
                    ZoneId.systemDefault());
            Log.d(getClass().getName(),
                    "init: sunrise and sunset times retrieved: " + sunrise + ", "
                            + sunset);
            sunriseSunset = new Pair<>(sunrise, sunset);
            sunriseSunsetExpiry = LawnchairUtilsKt.tomorrow(new Date()).getTime();
            return Unit.INSTANCE;
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
        if (sunriseSunsetExpiry < System.currentTimeMillis()) {
            Pair<Double, Double> location = LawnchairUtilsKt
                    .getLawnchairLocationManager(getContext()).getLocation();
            if (location != null) {
                SolarTime solarTime = SolarTime.ofLocation(location.getFirst(), location.getSecond());
                ZonedDateTime sunrise = ZonedDateTime.ofInstant(Instant.ofEpochSecond(
                        PlainDate.nowInSystemTime().get(
                                solarTime.sunrise(Twilight.ASTRONOMICAL)).inLocalView().getPosixTime()),
                        ZoneId.systemDefault());
                ZonedDateTime sunset = ZonedDateTime.ofInstant(Instant.ofEpochSecond(
                        PlainDate.nowInSystemTime().get(
                                solarTime.sunset(Twilight.ASTRONOMICAL)).inLocalView().getPosixTime()),
                        ZoneId.systemDefault());
                Log.d(getClass().getName(),
                        "init: sunrise and sunset times retrieved: " + sunrise + ", "
                                + sunset);
                sunriseSunset = new Pair<>(sunrise, sunset);
                sunriseSunsetExpiry = LawnchairUtilsKt.tomorrow(new Date()).getTime();
            }
        }
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
            Date currentTime = new Date();
            String query =
                    "(( " + CalendarContract.Events.DTSTART + " >= " + currentTime.getTime()
                            + " ) AND ( " + CalendarContract.Events.DTSTART + " <= "
                            + LawnchairUtilsKt
                            .tomorrow(currentTime).getTime() + " ))";
            if (context.checkSelfPermission(
                    Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                Cursor calendarEvents = context.getContentResolver()
                        .query(CalendarContract.Events.CONTENT_URI,
                                new String[]{CalendarContract.Instances.TITLE,
                                        CalendarContract.Instances.DTSTART,
                                        CalendarContract.Instances.DTEND,
                                        CalendarContract.Instances.DESCRIPTION,
                                        CalendarContract.Events._ID,
                                        CalendarContract.Instances.CUSTOM_APP_PACKAGE,
                                        CalendarContract.Events.EVENT_LOCATION}, query, null,
                                CalendarContract.Instances.DTSTART + " ASC");
                if (calendarEvents.getCount() > 0) {
                    items.add(new DailySummaryItem(LawnchairUtilsKt
                            .tint(
                                    Objects.requireNonNull(
                                            context.getDrawable(R.drawable.ic_event_black_24dp)),
                                    FeedAdapter.Companion.getOverrideColor(context)),
                            context.getResources()
                                    .getQuantityString(
                                            R.plurals.title_daily_briefing_calendar_events,
                                            calendarEvents.getCount(), calendarEvents.getCount())));
                }
            }
            Log.d(getClass().getName(),
                    "refresh: sunrise and sunset are " + feedProvider.sunriseSunset);
            if (feedProvider.sunriseSunset != null) {
                items.add(new DailySummaryItem(LawnchairUtilsKt
                        .tint(Objects.requireNonNull(
                                context.getDrawable(R.drawable.ic_sunrise_24dp)),
                                FeedAdapter.Companion.getOverrideColor(context)),
                        LawnchairUtilsKt
                                .formatTime(feedProvider.sunriseSunset.getFirst(), context)));
                items.add(new DailySummaryItem(LawnchairUtilsKt
                        .tint(Objects.requireNonNull(
                                context.getDrawable(R.drawable.ic_sunset_24dp)),
                                FeedAdapter.Companion.getOverrideColor(context)),
                        LawnchairUtilsKt
                                .formatTime(feedProvider.sunriseSunset.getSecond(), context)));
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

    public DailySummaryItem(Drawable icon, String text) {
        this.icon = icon;
        this.text = text;
    }
}
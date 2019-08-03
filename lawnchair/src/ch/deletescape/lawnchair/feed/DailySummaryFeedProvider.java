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
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import com.android.launcher3.R;
import com.google.android.apps.nexuslauncher.graphics.IcuDateTextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DailySummaryFeedProvider extends FeedProvider {

    public DailySummaryFeedProvider(Context c) {
        super(c);
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
        return Collections.singletonList(new Card(null, null,
                parent -> {
                    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.daily_summary, parent, false);
                    TextView dateTextView = v.findViewById(R.id.current_date);
                    dateTextView.setText(
                            IcuDateTextView.getDateFormat(getContext(), true, null, false)
                                    .format(System.currentTimeMillis()));
                    RecyclerView recyclerView = v.findViewById(R.id.daily_summary_information);
                    Adapter adapter = new Adapter(parent.getContext());
                    recyclerView.setAdapter(adapter);
                    GridLayoutManager layoutManager;
                    recyclerView.setLayoutManager(
                            layoutManager = new GridLayoutManager(parent.getContext(),
                                    1)); /* TODO proper grid layout span sizes */
                    adapter.refresh();
                    return v;
                },
                Card.Companion.getRAISE() | Card.Companion.getNO_HEADER(), "nosort,top",
                "dailySummary".hashCode()));
    }
}

class Adapter extends RecyclerView.Adapter {

    private final Context context;
    private final List<DailySummaryItem> items = new ArrayList<>();

    public Adapter(Context context) {
        this.context = context;
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
                        + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + LawnchairUtilsKt
                        .tomorrow(currentTime).getTime() + " ))";
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
                    .duplicateAndSetColour(
                            Objects.requireNonNull(
                                    context.getDrawable(R.drawable.ic_event_black_24dp)),
                            LawnchairUtilsKt
                                    .getColorAttrib(context.getTheme(), R.attr.colorAccent)),
                    context.getResources()
                            .getQuantityString(R.plurals.title_daily_briefing_calendar_events,
                                    calendarEvents.getCount(), calendarEvents.getCount())));
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

class DailySummaryItem {

    public Drawable icon;
    public String text;

    public DailySummaryItem(Drawable icon, String text) {
        this.icon = icon;
        this.text = text;
    }
}
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

package ch.deletescape.lawnchair.feed.widget;

/*
 * There must be a simpler, cleaner way to do this
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

import ch.deletescape.lawnchair.LawnchairApp;
import ch.deletescape.lawnchair.feed.FeedAdapter;
import ch.deletescape.lawnchair.feed.FeedJoinedWeatherProvider;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.CardData;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.Listener;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.WeatherData;

public class WeatherCardRecyclerView extends RecyclerView implements Listener {

    public WeatherCardRecyclerView(
            @NonNull Context context) {
        this(context, null, 0);
    }

    public WeatherCardRecyclerView(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherCardRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        setAdapter(new FeedAdapter(Collections.singletonList(new FeedJoinedWeatherProvider(context)), 0, context, null));
        setLayoutManager(new LinearLayoutManager(context));
        ((LawnchairApp) context.getApplicationContext()).getSmartspace().addListener(this);
        IntentFilter tick = new IntentFilter();
        tick.addAction(Intent.ACTION_TIME_TICK);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                WeatherCardRecyclerView.this.onReceive(context, intent);
            }
        }, tick);
    }

    @Override
    public void onDataUpdated(@org.jetbrains.annotations.Nullable WeatherData weather,
            @org.jetbrains.annotations.Nullable CardData card) {
        if (weather != null) {
            getAdapter().notifyDataSetChanged();
        }
    }

    public void onReceive(Context context, Intent intent) {
        getAdapter().notifyDataSetChanged();
    }
}

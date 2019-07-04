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

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import ch.deletescape.lawnchair.settings.ui.SettingsBaseActivity;
import com.android.launcher3.R;
import java.util.ArrayList;

public class FeedActivity extends SettingsBaseActivity {

    private RecyclerView feed;
    private FeedAdapter adapter;
    private ArrayList<FeedProvider> providerArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        feed = findViewById(R.id.feed_recycler);
        providerArrayList = new ArrayList<>();
        providerArrayList.add(new CalendarEventProvider(this));
        feed.setLayoutManager(new LinearLayoutManager(this));
        feed.setAdapter(adapter = new FeedAdapter(providerArrayList));
        adapter.notifyDataSetChanged();
    }
}

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.android.launcher3.R;
import com.google.android.apps.nexuslauncher.graphics.IcuDateTextView;
import java.util.Collections;
import java.util.List;

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
                    return v;
                },
                Card.Companion.getRAISE() | Card.Companion.getNO_HEADER(), "nosort,top",
                "dailySummary".hashCode()));
    }
}

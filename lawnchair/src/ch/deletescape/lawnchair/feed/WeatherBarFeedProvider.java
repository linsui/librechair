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
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import ch.deletescape.lawnchair.LawnchairApp;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.CardData;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.Listener;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.WeatherData;
import com.android.launcher3.Utilities;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class WeatherBarFeedProvider extends FeedProvider implements Listener {

    private Card card;

    public WeatherBarFeedProvider(Context c) {
        super(c);
        ((LawnchairApp) c.getApplicationContext()).getSmartspace().addListener(this);
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
        return card == null ? Collections.emptyList() : Collections.singletonList(card);
    }

    @Override
    public void onDataUpdated(@Nullable WeatherData weather, @Nullable CardData card) {
        if (weather != null) {
            this.card = new Card(new BitmapDrawable(getContext().getResources(), weather.getIcon()),
                    weather.getTitle(Utilities.getLawnchairPrefs(getContext()).getWeatherUnit()),
                    parent -> new View(parent.getContext()), Card.Companion.getTEXT_ONLY(),
                    "nosort,top", "weatherBar".hashCode());
        }
    }
}

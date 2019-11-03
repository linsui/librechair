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

import com.android.launcher3.Utilities;

import java.util.Collections;
import java.util.List;

import ch.deletescape.lawnchair.awareness.WeatherManager;
import ch.deletescape.lawnchair.feed.util.FeedUtil;
import ch.deletescape.lawnchair.feed.web.WebViewScreen;
import ch.deletescape.lawnchair.persistence.FeedPersistence;
import ch.deletescape.lawnchair.smartspace.weather.forecast.ForecastProvider;
import kotlin.Unit;

public class WeatherBarFeedProvider extends FeedProvider {

    private ForecastProvider.CurrentWeather weather;

    public WeatherBarFeedProvider(Context c) {
        super(c);
        WeatherManager.INSTANCE.subscribeWeather(currentWeather -> {
            weather = currentWeather;
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
        return weather == null ? Collections.emptyList() : Collections.singletonList(
                FeedUtil.apply(new Card(new BitmapDrawable(getContext().getResources(), weather.getIcon()),
                weather.getTemperature().toString(Utilities.getLawnchairPrefs(getContext()).getWeatherUnit()),
                parent -> new View(parent.getContext()), Card.TEXT_ONLY,
                "nosort,top", "weatherBar".hashCode()), it -> {
            if (weather.getUrl() != null) {
                it.setGlobalClickListener(v -> {
                    if (!FeedPersistence.Companion.getInstance(getContext()).getDirectlyOpenLinksInBrowser()) {
                        WebViewScreen.obtain(getContext(), weather.getUrl()).display(this, 0, 0, v);
                    } else {
                        FeedUtil.openUrl(getContext(), weather.getUrl(), v);
                    }
                    return Unit.INSTANCE;
                });
            }
        }));
    }
}

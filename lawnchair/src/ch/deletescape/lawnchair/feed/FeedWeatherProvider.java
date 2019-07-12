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
import android.util.Log;
import android.view.View;
import ch.deletescape.lawnchair.LawnchairAppKt;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.DataContainer;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.Listener;
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController.WeatherData;
import com.android.launcher3.R;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class FeedWeatherProvider extends FeedProvider implements Listener {

    private WeatherData weatherData;

    public FeedWeatherProvider(Context c) {
        super(c);
        LawnchairAppKt.getLawnchairApp(c.getApplicationContext()).getSmartspace().addListener(this);
    }

    @Override
    public void onFeedShown() {
        // TODO
    }

    @Override
    public void onFeedHidden() {
        // TODO
    }

    @Override
    public void onCreate() {
        // TODO
    }

    @Override
    public void onDestroy() {
        // TODO
    }

    @Override
    public List<Card> getCards() {
        Log.d(getClass().getName(), "getCards: " + weatherData);
        return weatherData != null ? Arrays.asList(new Card(new BitmapDrawable(getContext().getResources(),
                weatherData.getIcon()), getContext()
                .getString(R.string.title_card_weather_temperature,
                        weatherData.getTitle(LawnchairUtilsKt.getLawnchairPrefs(getContext()).getWeatherUnit())),
                parent -> new View(getContext()), Card.Companion.getTEXT_ONLY(), null)) : Collections.emptyList();
    }

    @Override
    public void onDataUpdated(@NotNull DataContainer data) {
        if (data.isWeatherAvailable() && !Objects.equals(data.getWeather(), weatherData)) {
            weatherData = data.getWeather();
            requestRefresh();
        } else {
            weatherData = data.getWeather();
        }
    }
}
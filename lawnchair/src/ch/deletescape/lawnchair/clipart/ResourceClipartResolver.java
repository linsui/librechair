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

package ch.deletescape.lawnchair.clipart;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.android.launcher3.R;
import java.util.Arrays;
import java.util.List;

public class ResourceClipartResolver implements ClipartResolver {

    private Context context;

    public ResourceClipartResolver(Context context) {

        this.context = context;
    }

    @Override
    public List<ClipartData> getAllClipart() {
        return Arrays.asList(new ClipartData() {
            @Override
            public String getToken() {
                return "resource_widgets";
            }

            @Override
            public String getUserFacingName() {
                return context.getString(R.string.title_pref_feed_widgets_tab);
            }

            @Override
            public Drawable resolveClipart() {
                return context.getDrawable(R.drawable.ic_widget);
            }
        }, new ClipartData() {
            @Override
            public String getToken() {
                return "resource_utility";
            }

            @Override
            public String getUserFacingName() {
                return context.getString(R.string.category_tools);
            }

            @Override
            public Drawable resolveClipart() {
                return context.getDrawable(R.drawable.ic_featured_play_list_black_24dp);
            }
        }, new ClipartData() {
            @Override
            public String getToken() {
                return "resource_news";
            }

            @Override
            public String getUserFacingName() {
                return context.getString(R.string.category_news);
            }

            @Override
            public Drawable resolveClipart() {
                return context.getDrawable(R.drawable.ic_assessment_black_24dp);
            }
        }, new ClipartData() {
            @Override
            public String getToken() {
                return "resources_events";
            }

            @Override
            public String getUserFacingName() {
                return context.getString(R.string.feed_category_events);
            }

            @Override
            public Drawable resolveClipart() {
                return context.getDrawable(R.drawable.ic_event_black_24dp);
            }
        }, new ClipartData() {
            @Override
            public String getToken() {
                return "resource_other";
            }

            @Override
            public String getUserFacingName() {
                return context.getString(R.string.pref_category_misc);
            }

            @Override
            public Drawable resolveClipart() {
                return context.getDrawable(R.drawable.ic_spa_black_24dp);
            }
        });
    }
}

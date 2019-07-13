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
import android.util.Log;
import com.prof.rssparser.Article;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;
import java.util.List;
import java.util.concurrent.Executors;
import org.jetbrains.annotations.NotNull;

public class WikinewsFeedProvider extends AbstractRSSFeedProvider {

    public WikinewsFeedProvider(Context c) {
        super(c);
    }

    @Override
    public void bindFeed(BindCallback callback) {
        Log.d(getClass().getCanonicalName(), "bindFeed: updating feed");
        Executors.newSingleThreadExecutor().submit(() -> {
            Parser parser = new Parser();
            parser.execute("https://en.wikinews.org/w/index.php?title=Special:NewsFeed&feed=rss&categories=Published&notcategories=No%20publish%7CArchived%7cAutoArchived%7cdisputed&namespace=0&count=15&ordermethod=categoryadd&stablepages=only");
            parser.onFinish(new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(@NotNull List<Article> list) {
                    Log.d(getClass().getCanonicalName(), "bindFeed: update complete");
                    callback.onBind(list);
                }

                @Override
                public void onError(@NotNull Exception e) {
                    Log.d(getClass().getCanonicalName(), "bindFeed: update failed", e);
                }
            });
        });
    }
}

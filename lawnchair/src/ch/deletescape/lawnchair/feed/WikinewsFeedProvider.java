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

import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import ch.deletescape.lawnchair.feed.util.FeedUtil;

public class WikinewsFeedProvider extends AbstractRSSFeedProvider {

    public WikinewsFeedProvider(Context c) {
        super(c);
    }

    @Override
    protected void onInit(Consumer<String> tokenCallback) {
        tokenCallback.accept(getId());
    }

    @Override
    protected void bindFeed(BindCallback callback, String token) {
        Log.d(getClass().getCanonicalName(), "bindFeed: updating feed");
        Executors.newSingleThreadExecutor().submit(() -> {
            Log.d(getClass().getCanonicalName(), "bindFeed: updating feed");
            Executors.newSingleThreadExecutor().submit(() -> {
                String feed;
                try {
                    InputStream is = FeedUtil.downloadDirect(
                            "https://en.wikinews.org/w/index.php?title=Special:NewsFeed&feed=rss&categories=Published&notcategories=No%20publish%7CArchived%7cAutoArchived%7cdisputed&namespace=0&count=35&ordermethod=categoryadd&stablepages=only",
                            getContext(),
                            null);
                    if (is == null) {
                        throw new IOException("failed to retrieve feed");
                    }
                    feed = IOUtils.toString(is, Charset
                                    .defaultCharset());
                    callback.onBind(new SyndFeedInput().build(new InputSource(
                            new CharSequenceInputStream(feed, Charset.defaultCharset()))));
                    is.close();
                } catch (FeedException | IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}

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

import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import ch.deletescape.lawnchair.feed.util.FeedUtil;
import ch.deletescape.lawnchair.util.okhttp.OkHttpClientBuilder;
import okhttp3.OkHttpClient;

public class TheVergeFeedProvider extends AbstractRSSFeedProvider {

    public TheVergeFeedProvider(Context c) {
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
            FeedUtil.download("https://www.theverge.com/rss/index.xml", getContext(), is -> {
                try {
                    callback.onBind(new SyndFeedInput().build(new InputSource(is)));
                } catch (FeedException e) {
                    e.printStackTrace();
                }
            }, null);
        });
    }
}

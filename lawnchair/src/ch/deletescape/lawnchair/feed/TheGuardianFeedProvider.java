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
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.xml.sax.InputSource;

public class TheGuardianFeedProvider extends AbstractRSSFeedProvider {

    public TheGuardianFeedProvider(Context c) {
        super(c);
    }

    @Override
    public void bindFeed(BindCallback callback) {
        Log.d(getClass().getCanonicalName(), "bindFeed: updating feed");
        Executors.newSingleThreadExecutor().submit(() -> {
            Log.d(getClass().getCanonicalName(), "bindFeed: updating feed");
            Executors.newSingleThreadExecutor().submit(() -> {
                String feed;
                try {
                    feed = IOUtils.toString(
                            new URL("https://www.theguardian.com/uk/rss").openConnection()
                                    .getInputStream(), Charset
                                    .defaultCharset());
                    callback.onBind(new SyndFeedInput().build(new InputSource(
                            new CharSequenceInputStream(feed, Charset.defaultCharset()))));
                } catch (FeedException | IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}

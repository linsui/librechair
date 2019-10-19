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

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

public class BBCFeedProvider extends AbstractLocationAwareRSSProvider {

    private static final HashMap<String, String> feeds = new HashMap<>();
    private static final String GB = "https://feeds.bbci.co.uk/news/england/rss.xml";
    private static final String US = "https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml";
    private static final String IL = "https://feeds.bbci.co.uk/news/northern_ireland/rss.xml";
    private static final String AS = "https://feeds.bbci.co.uk/news/world/asia/rss.xml";
    private static final String WD = "https://feeds.bbci.co.uk/news/world/rss.xml";
    private static final String AF = "http://feeds.bbci.co.uk/news/world/africa/rss.xml";

    static {
        feeds.put("GBR", GB);
        feeds.put("IRL", IL);
        feeds.put("USA", US);
        feeds.put("CAN", US);
        feeds.put("CHN", AS);
        feeds.put("JPN", AS);
        feeds.put("PRK", AS);
        feeds.put("KOR", AS);
        feeds.put("KEN", AF);
        feeds.put("ZAF", AF);
        feeds.put("CAF", AF);
        feeds.put("DZA", AF);
        feeds.put("WORLD", WD);
    }

    public BBCFeedProvider(Context c) {
        super(c);
    }

    @NotNull
    @Override
    public SyndFeed getLocationAwareFeed(@NotNull String country) {
        try {
            String feed = IOUtils.toString(
                    new URL(feeds.get(country)).openConnection()
                            .getInputStream(), Charset
                            .defaultCharset());
            return new SyndFeedInput().build(new InputSource(
                    new CharSequenceInputStream(feed, Charset.defaultCharset())));
        } catch (FeedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public SyndFeed getFallbackFeed() {
        return getLocationAwareFeed("WORLD");
    }
}

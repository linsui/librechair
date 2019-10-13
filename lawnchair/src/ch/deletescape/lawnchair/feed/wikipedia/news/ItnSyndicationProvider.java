/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.wikipedia.news;

import android.content.Context;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeedImpl;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;

import ch.deletescape.lawnchair.feed.AbstractRSSFeedProvider;

public class ItnSyndicationProvider extends AbstractRSSFeedProvider {

    public ItnSyndicationProvider(Context c) {
        super(c);
    }

    @Override
    protected void bindFeed(BindCallback callback) {
        Executors.newSingleThreadExecutor().submit(() -> {
            SyndFeedImpl feed = new SyndFeedImpl();
            feed.setTitle("Wikipedia");
            feed.setAuthor("Wikipedia contributors");
            ArrayList<SyndEntry> entries = new ArrayList<>();
            for (NewsItem item : News.requireEntries()) {
                SyndEntry entry = new SyndEntryImpl();
                entry.setUri(item.contentUrl);
                entry.setTitle(item.title);
                SyndContent content = new SyndContentImpl();
                content.setValue(item.story);
                entry.setDescription(content);
                entry.setPublishedDate(item.dt);
                entry.setForeignMarkup(Collections.singletonList(
                        new Element("media", "thumbnail", "https://xml/res-auto").setAttribute(
                                "url", item.thumbnail)));
                entries.add(entry);
            }
            feed.setEntries(entries);
            callback.onBind(feed);
        });
    }
}

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

package ch.deletescape.lawnchair.feed.news;

import android.content.Context;

import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

import org.xml.sax.InputSource;

import java.util.function.Consumer;

import ch.deletescape.lawnchair.feed.AbstractRSSFeedProvider;
import ch.deletescape.lawnchair.feed.util.FeedUtil;

public class NPRFeedProvider extends AbstractRSSFeedProvider {
    public NPRFeedProvider(Context c) {
        super(c);
    }

    @Override
    protected void onInit(Consumer<String> tokenCallback) {
        tokenCallback.accept(getId());
    }

    @Override
    protected void bindFeed(BindCallback callback, String token) {
        FeedUtil.download("https://www.npr.org/rss/rss.php", getContext(), is -> {
            try {
                callback.onBind(new SyndFeedInput().build(new InputSource(is)));
            } catch (FeedException e) {
                e.printStackTrace();
            }
        }, null);
    }
}

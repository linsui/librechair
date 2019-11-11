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

import com.rometools.rome.io.SyndFeedInput;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.Consumer;

import ch.deletescape.lawnchair.feed.AbstractRSSFeedProvider;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NPRFeedProvider extends AbstractRSSFeedProvider {
    public NPRFeedProvider(Context c) {
        super(c);
    }

    @Override
    protected void onInit(Consumer<String> tokenCallback) {
        tokenCallback.accept(getId());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void bindFeed(BindCallback callback, String token) {
        Flowable.fromCallable(() -> new SyndFeedInput().build(new InputStreamReader(new URL("https://www.npr.org/rss/rss.php").openStream())))
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .subscribe(callback::onBind);
    }
}

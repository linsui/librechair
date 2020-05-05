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

package ch.deletescape.lawnchair.persistence;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import ch.deletescape.lawnchair.feed.util.FeedUtil;
import ch.deletescape.lawnchair.persistence.db.StringDatabase;

public class SimplePersistence {

    private final AtomicReference<Context> context = new AtomicReference<>();
    private final HashMap<String, String> cache = new HashMap<>();


    private SimplePersistence(Context context) {
        this.context.set(context);
    }

    public String get(String key, String defaultValue) {
        if (cache.get(key) != null && !InvalidationTracker.isInvalidated(key)) {
            return cache.get(key);
        } else {
            String result = getInternal(key, defaultValue);
            InvalidationTracker.validate(key);
            cache.put(key, result);
            return result;
        }
    }

    private String getInternal(String key, String defaultValue) {
        String query;
        if ((query = StringDatabase.getInstance(context.get()).dao().getSafe(key)) != null) {
            return query;
        } else {
            return legacyGet(key, defaultValue);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String legacyGet(String key, String defaultValue) {
        File file = new File(context.get().getFilesDir(), "persist_" + key);
        if (!file.exists()) {
            return defaultValue;
        }
        try {
            FileInputStream stream = new FileInputStream(file);
            String value = IOUtils.toString(stream, Charset.defaultCharset());
            file.delete();
            this.put(key, value);
            return value;
        } catch (IOException e) {
            return defaultValue;
        }
    }

    public synchronized void put(String key, String value) {
        FeedUtil.runOnMainThread(() -> InvalidationTracker.invalidate(key, context.get()));
        StringDatabase.getInstance(context.get()).dao().put(key, value);
    }

    public static class InstanceHolder {
        private volatile static SimplePersistence sInstance;

        public static SimplePersistence getInstance(Context context) {
            return sInstance != null ? sInstance :
                    (sInstance = new SimplePersistence(context));
        }
    }
}

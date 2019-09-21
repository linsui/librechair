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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class SimplePersistence {

    private final Context context;

    public SimplePersistence(Context context) {
        this.context = context;
    }

    public String get(String key, String defaultValue) {
        File file = new File(context.getFilesDir(), "persist_" + key);
        try {
            FileInputStream stream = new FileInputStream(file);
            return IOUtils.toString(stream, Charset.defaultCharset());
        } catch (IOException e) {
            return defaultValue;
        }
    }

    public synchronized void put(String key, String value) {
        File file = new File(context.getFilesDir(), "persist_" + key);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(value.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class InstanceHolder {
        private volatile transient static SimplePersistence sInstance;

        public static SimplePersistence getInstance(Context context) {
            return sInstance != null ? sInstance :
                    (sInstance = new SimplePersistence(context));
        }
    }
}

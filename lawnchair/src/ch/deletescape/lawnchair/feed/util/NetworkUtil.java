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

package ch.deletescape.lawnchair.feed.util;

import android.annotation.WorkerThread;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

public final class NetworkUtil {
    private NetworkUtil() {
        throw new RuntimeException("");
    }

    @Nonnull
    @WorkerThread
    public static String resolveRedirects(@Nonnull String url) throws IOException {
        return resolveRedirects(url, 0, System.currentTimeMillis());
    }

    @WorkerThread
    @Nonnull
    private static String resolveRedirects(@Nonnull String _url, int count, long startTime)
            throws IOException {
        String url = _url.replace("http://", "https://");
        if (System.currentTimeMillis() - startTime > TimeUnit.SECONDS.toMillis(25)) {
            return url;
        }
        if (count > 6) {
            return url;
        } else {
            URL urlV = new URL(url);
            URLConnection conn = urlV.openConnection();
            conn.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5));
            conn.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));
            try {
                conn.connect();
            } catch (IOException e) {
                return url;
            }
            if (conn instanceof HttpURLConnection) {
                if (((HttpURLConnection) conn).getResponseCode() > 300 &&
                        ((HttpURLConnection) conn).getResponseCode() < 400) {
                    if (conn.getHeaderField("location") != null) {
                        return resolveRedirects(conn.getHeaderField("location"), count + 1,
                                startTime);
                    } else if (conn.getHeaderField("content-location") != null) {
                        return resolveRedirects(conn.getHeaderField("content-location"), count + 1,
                                startTime);
                    } else if (conn.getHeaderField("Location") != null) {
                        return resolveRedirects(conn.getHeaderField("Location"), count + 1,
                                startTime);
                    } else if (conn.getHeaderField("Content-Location") != null) {
                        return resolveRedirects(conn.getHeaderField("Content-Location"), count + 1,
                                startTime);
                    } else if (conn.getHeaderField("Content-location") != null) {
                        return resolveRedirects(conn.getHeaderField("Content-location"), count + 1,
                                startTime);
                    }
                }
                return url;
            } else {
                return url;
            }
        }
    }
}

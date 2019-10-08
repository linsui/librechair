/*
 * Copyright 2018 Azilet B.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kg.net.bazi.gsb4j.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to split URLs into host, port, path, and query string parts.
 *
 * @author azilet
 */
public class UrlSplitter {

    private static final Pattern URL_PATTERN = Pattern.compile("(https?://)([^/:?]+)(:\\d+)?(/[^?]*)?(\\?.*)?");

    /**
     * Splits supplied URL into parts.
     *
     * @param url URL string to split
     * @return parts of the supplied URL if parsing succeeded; {@code null} otherwise
     */
    public UrlParts split(String url) {
        Matcher matcher = URL_PATTERN.matcher(url.trim());
        if (matcher.matches()) {
            UrlParts parts = new UrlParts();
            parts.scheme = matcher.group(1);
            parts.host = matcher.group(2);
            parts.port = matcher.group(3);
            parts.path = matcher.group(4);
            parts.query = matcher.group(5);
            return parts;
        }
        return null;
    }

    /**
     * POJO class to hold URL parts. Parts include their related separators like "/" for paths and "?" for query
     * strings. So directly merging all parts should return the original URL from which parts were extracted.
     */
    public static class UrlParts {

        private String scheme;
        private String host;
        private String port;
        private String path;
        private String query;

        /**
         * Gets scheme of the URL. It is either "http://" or "https://".
         *
         * @return scheme of the URL
         */
        public String getScheme() {
            return scheme;
        }

        /**
         * Gets host part of the URL.
         *
         * @return host part
         */
        public String getHost() {
            return host;
        }

        /**
         * Gets port of the URL. Port part starts with ":".
         *
         * @return port of the URL if exists; {@code null} otherwise
         */
        public String getPort() {
            return port;
        }

        /**
         * Gets path of the URL. Path part starts with "/".
         *
         * @return path of the URL if exists; {@code null} otherwise
         */
        public String getPath() {
            return path;
        }

        /**
         * Gets query of the URL. Query string starts with "?".
         *
         * @return query string of the URL; {@code null} otherwise
         */
        public String getQuery() {
            return query;
        }
    }

}

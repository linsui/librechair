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

package kg.net.bazi.gsb4j.api;

import kg.net.bazi.gsb4j.data.ThreatMatch;

/**
 * Safe Browsing API interface.
 * <p>
 * As of APIv4, two implementations are available: Lookup API and Update API.
 *
 * @author azilet
 */
public interface SafeBrowsingApi {

    /**
     * Checks the supplied URL if it is in the threat lists of the Google Safe Browsing API.
     *
     * @param url URL to check
     * @return threat match if URL is found in one of threat lists; {@code null} otherwise which means URL is safe
     */
    ThreatMatch check(String url);

    public class Type {

        /**
         * Name to identify Lookup API client implementation.
         */
        public static final String LOOKUP_API = "lookup";
        /**
         * Name to identify Update API client implementation.
         */
        public static final String UPDATE_API = "update";

        private Type() {
            // not to be initialized
        }
    }

}

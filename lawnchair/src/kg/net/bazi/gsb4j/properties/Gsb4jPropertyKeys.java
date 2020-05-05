/*
 * Copyright 2019 Azilet B.
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

package kg.net.bazi.gsb4j.properties;

/**
 * Property names used by Gsb4j.
 *
 * @author azilet
 */
public class Gsb4jPropertyKeys {

    /**
     * Configuration property name for API key.
     */
    public static final String API_KEY = "api.key";

    /**
     * Configuration property name for HTTP Referrer value for the API key.
     */
    public static final String API_HTTP_REFERRER = "api.http.referrer";

    /**
     * Configuration property name for Gsb4j data directory.
     */
    public static final String DATA_DIRECTORY = "data.dir";

    private Gsb4jPropertyKeys() {
        // not to be initialized
    }

}

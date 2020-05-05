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

package kg.net.bazi.gsb4j;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.deletescape.lawnchair.LawnchairApp;
import ch.deletescape.lawnchair.util.okhttp.OkHttpClientBuilder;
import kg.net.bazi.gsb4j.properties.Gsb4jProperties;
import okhttp3.OkHttpClient;

/**
 * Provider of HTTP client to be used for making requests to Safe Browsing API.
 *
 * @author azilet
 */
class HttpClientProvider implements Provider<OkHttpClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientProvider.class);

    @Inject
    Gsb4jProperties properties;

    @Override
    public OkHttpClient get() {
        return new OkHttpClientBuilder().build(LawnchairApp.localizationContext);
    }
}

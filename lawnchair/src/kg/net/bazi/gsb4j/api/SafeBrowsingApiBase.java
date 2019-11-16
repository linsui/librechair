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

import com.google.gson.Gson;
import com.google.inject.Inject;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kg.net.bazi.gsb4j.Gsb4j;
import kg.net.bazi.gsb4j.Gsb4jBinding;
import kg.net.bazi.gsb4j.data.PlatformType;
import kg.net.bazi.gsb4j.data.ThreatMatch;
import kg.net.bazi.gsb4j.properties.Gsb4jClientInfoProvider;
import kg.net.bazi.gsb4j.properties.Gsb4jProperties;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Abstract base class for Safe Browsing API implementation classes.
 *
 * @author azilet
 */
abstract class SafeBrowsingApiBase {

    @Inject
    @Gsb4jBinding
    OkHttpClient httpClient;

    @Inject
    Gsb4jClientInfoProvider clientInfoProvider;

    @Inject
    @Gsb4jBinding
    Gson gson;

    private String apiKey;

    abstract Logger getLogger();

    @Inject
    void setApiKey(Gsb4jProperties properties) {
        apiKey = properties.getApiKey();
    }

    /**
     * Selects threat among supplied matches that has a broader impact. A threat to all or any of platforms has a
     * broader impact than a treat to a specific platform.
     *
     * @param matches list of matches to select from; should not be empty
     * @return a match that has more generic impact; or first one if there is no such a threat
     */
    ThreatMatch selectMoreGenericThreat(List<ThreatMatch> matches) {
        if (matches.size() > 1) {
            StringBuilder sb = new StringBuilder();
            for (ThreatMatch match : matches) {
                sb.append(System.lineSeparator()).append(gson.toJson(match));
            }
            getLogger().info("Multiple threat matches found: {}", sb);

            for (ThreatMatch match : matches) {
                if (match.getPlatformType() == PlatformType.ALL_PLATFORMS) {
                    return match;
                }
            }
            for (ThreatMatch match : matches) {
                if (match.getPlatformType() == PlatformType.ANY_PLATFORM) {
                    return match;
                }
            }
        }
        return matches.get(0);
    }

    /**
     * Makes up an HTTP request based on passed arguments.
     *
     * @param httpMethod HTTP method like GET, POST, etc.
     * @param endpoint endpoint to build URL
     * @param payload payload to include in request; maybe {@code null}
     * @return HTTP request
     */
    Request makeRequest(String httpMethod, String endpoint, Object payload) {
        Request.Builder builder = new Request.Builder()
            .url(Gsb4j.API_BASE_URL + endpoint + "?api_key=" + apiKey)
            .addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        if (payload != null) {
            builder.post(new RequestBody() {
                @NotNull
                @Override
                public MediaType contentType() {
                    return MediaType.get("application/json");
                }

                @Override
                public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                    bufferedSink.write(gson.toJson(payload).getBytes());
                }
            });
        }
        return builder.build();
    }

    /**
     * Gets input stream of the http response. Stream is returned only if the response is OK and a valid content stream
     * exists. Exception is thrown otherwise.
     *
     * @param response http response to get input stream from
     * @return input stream if valid stream exists; never {@code null}
     * @throws IOException when no valid response stream was found; for example when the response status is not HTTP
     * 200, or there is no response entity at all
     */
    InputStream getInputStream(HttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException("Response status code is not OK: " + response.getStatusLine());
        }
        if (response.getEntity() == null) {
            throw new IOException("No message entity found in response");
        }
        return response.getEntity().getContent();
    }

    /**
     * Gets {@link Reader} for the HTTP response. This method wraps input stream returned by
     * {@link #getInputStream(org.apache.http.HttpResponse)} by a reader implementation.
     *
     * @param resp HTTP response to get reader for
     * @return input stream reader; never {@code null}
     * @throws IOException same as {@link #getInputStream(org.apache.http.HttpResponse)}
     */
    Reader getResponseReader(CloseableHttpResponse resp) throws IOException {
        InputStream is = getInputStream(resp);
        return new InputStreamReader(is, StandardCharsets.UTF_8);
    }

    /**
     * Wraps payload into a map together with client info. Client info is necessary for all API requests.
     *
     * @param name name of the payload data
     * @param payload payload
     * @return map of payload and client info data
     */
    Map<String, Object> wrapPayload(String name, Object payload) {
        Map<String, Object> map = new HashMap<>();
        map.put("client", clientInfoProvider.make());
        map.put(name, payload);
        return map;
    }

}

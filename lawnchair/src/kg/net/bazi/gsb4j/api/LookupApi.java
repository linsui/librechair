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

import com.google.inject.Inject;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import kg.net.bazi.gsb4j.data.ThreatEntry;
import kg.net.bazi.gsb4j.data.ThreatInfo;
import kg.net.bazi.gsb4j.data.ThreatMatch;

/**
 * Lookup API interface.
 *
 * @author azilet
 */
class LookupApi extends SafeBrowsingApiBase implements SafeBrowsingApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(LookupApi.class);

    @Inject
    private LookupApiCache cache;

    @Override
    public ThreatMatch check(String url) {
        ThreatMatch cached = cache.get(url);
        if (cached != null) {
            LOGGER.info("Cached URL found: {}", url);
            return cached;
        }
        return requestApi(url);
    }

    @Override
    Logger getLogger() {
        return LOGGER;
    }

    private ThreatMatch requestApi(String url) {
        ThreatInfo threatInfo = new ThreatInfo();
        threatInfo.getThreatEntries().add(makeThreatEntry(url));

        Map<String, Object> body = wrapPayload("threatInfo", threatInfo);
        HttpUriRequest req = makeRequest(HttpPost.METHOD_NAME, "threatMatches:find", body);

        try ( CloseableHttpResponse resp = httpClient.execute(req);
             Reader reader = getResponseReader(resp)) {
            ApiResponse apiResponse = gson.fromJson(reader, ApiResponse.class);
            if (apiResponse.matches != null && !apiResponse.matches.isEmpty()) {
                ThreatMatch match = selectMatch(url, apiResponse.matches);
                if (match != null) {
                    cache.put(match);
                    return match;
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to query Lookup API", ex);
        }
        return null;
    }

    private ThreatEntry makeThreatEntry(String url) {
        ThreatEntry threatEntry = new ThreatEntry();
        threatEntry.setUrl(url);
        return threatEntry;
    }

    /**
     * Selects a threat match for the URL.
     *
     * @param url URL to select a match for
     * @param matches list of threat matches
     * @return threat match for the supplied URL if there is such a match; {@code null} otherwise
     */
    private ThreatMatch selectMatch(String url, List<ThreatMatch> matches) {
        for (ThreatMatch match : matches) {
            if (match.getThreat().getUrl().equals(url)) {
                return match;
            }
        }
        return null;
    }

    private static class ApiResponse {

        List<ThreatMatch> matches;
    }

}

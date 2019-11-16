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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kg.net.bazi.gsb4j.Gsb4j;
import kg.net.bazi.gsb4j.cache.ThreatListDescriptorsCache;
import kg.net.bazi.gsb4j.data.ThreatEntryType;
import kg.net.bazi.gsb4j.data.ThreatListDescriptor;
import kg.net.bazi.gsb4j.data.updates.CompressionType;
import kg.net.bazi.gsb4j.data.updates.Constraints;
import kg.net.bazi.gsb4j.data.updates.ListUpdateRequest;
import kg.net.bazi.gsb4j.data.updates.ListUpdateResponse;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class updates Safe Browsing lists in the local database.
 *
 * @author azilet
 */
class ThreatListUpdater extends SafeBrowsingApiBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreatListUpdater.class);

    @Inject
    private StateHolder stateHolder;

    @Inject
    private UpdateResponseHandler updateResponseHandler;

    @Inject
    private ThreatListDescriptorsCache descriptorsCache;

    /**
     * Performs a list update request to API.
     *
     * @throws IOException when connections problems occur
     */
    public void requestUpdate() throws IOException {
        if (!stateHolder.isUpdateAllowed()) {
            LOGGER.info("Update request skipped due to minimum wait duration.");
            return;
        }

        LOGGER.info("Starting list update...");

        Collection<ThreatListDescriptor> descriptors = descriptorsCache.getRefreshed();
        if (descriptors.isEmpty()) {
            LOGGER.warn("No threat list descriptors known. Skipping!");
            return;
        }

        List<ListUpdateRequest> updateRequests = makeListUpdateRequests(descriptors);
        Map<String, Object> payload = wrapPayload("listUpdateRequests", updateRequests);
        HttpUriRequest req = makeRequest(HttpPost.METHOD_NAME, "threatListUpdates:fetch", payload);

        ApiResponse apiResp;
        try {
            try (Response resp = httpClient.newCall(
                    new Request.Builder().url(req.getURI().toURL()).build()).execute();
                 Reader reader = Objects.requireNonNull(resp.body()).charStream()) {
                apiResp = gson.fromJson(reader, ApiResponse.class);
            }
        } catch (NullPointerException e) {
            throw new IOException("failed to update local database", e);
        }
        // no null check here - parser returns null only when input is at EOF which is really an exceptional case
        if (apiResp.listUpdateResponses != null) {
            int successful = updateResponseHandler.apply(apiResp.listUpdateResponses);
            int total = apiResp.listUpdateResponses.size();

            LOGGER.info("{} of {} updates successfully applied to local database", successful, total);
            LOGGER.info("=========================================================");
        }
        // update min wait duration *only after* we have handled all updates
        if (apiResp.minimumWaitDuration != null) {
            long duration = Gsb4j.durationToMillis(apiResp.minimumWaitDuration);
            stateHolder.setMinWaitDurationForUpdates(duration);
        }
    }

    @Override
    Logger getLogger() {
        return LOGGER;
    }

    private List<ListUpdateRequest> makeListUpdateRequests(Collection<ThreatListDescriptor> descriptors) {
        Constraints constraints = makeConstraints();
        List<ListUpdateRequest> updateRequests = new ArrayList<>(descriptors.size());

        Iterator<ThreatListDescriptor> it = descriptors.stream()
            .filter(d -> d.getThreatEntryType() == ThreatEntryType.URL).iterator();
        while (it.hasNext()) {
            ThreatListDescriptor descriptor = it.next();

            ListUpdateRequest req = new ListUpdateRequest();
            req.setThreatType(descriptor.getThreatType());
            req.setPlatformType(descriptor.getPlatformType());
            req.setThreatEntryType(descriptor.getThreatEntryType());
            req.setState(stateHolder.getState(descriptor));
            req.setConstraints(constraints);
            updateRequests.add(req);
        }
        return updateRequests;
    }

    private Constraints makeConstraints() {
        Constraints constraints = new Constraints();
        constraints.setRegion("US");
        constraints.setSupportedCompressions(new CompressionType[] {
            CompressionType.RAW
        });
        // NOTE: here we do not set max__Entries fields, hence they are serialized with default 0 value - it works!
        return constraints;
    }

    private static class ApiResponse {

        private List<ListUpdateResponse> listUpdateResponses;
        private String minimumWaitDuration;
    }

}

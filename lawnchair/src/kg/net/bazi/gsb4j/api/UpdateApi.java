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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import kg.net.bazi.gsb4j.Gsb4j;
import kg.net.bazi.gsb4j.cache.ThreatListDescriptorsCache;
import kg.net.bazi.gsb4j.data.ThreatEntry;
import kg.net.bazi.gsb4j.data.ThreatInfo;
import kg.net.bazi.gsb4j.data.ThreatListDescriptor;
import kg.net.bazi.gsb4j.data.ThreatMatch;
import kg.net.bazi.gsb4j.db.LocalDatabase;
import kg.net.bazi.gsb4j.url.Canonicalization;
import kg.net.bazi.gsb4j.url.Hashing;
import kg.net.bazi.gsb4j.url.SuffixPrefixExpressions;

/**
 * Interface to Update API.
 *
 * @author azilet
 */
class UpdateApi extends SafeBrowsingApiBase implements SafeBrowsingApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateApi.class);

    @Inject
    private Canonicalization canonicalizer;

    @Inject
    private SuffixPrefixExpressions expressionGenerator;

    @Inject
    private Hashing hashing;

    @Inject
    private LocalDatabase localDatabase;

    @Inject
    private ThreatListDescriptorsCache descriptorsCache;

    @Inject
    private UpdateApiCache cache;

    @Inject
    private StateHolder stateHolder;

    @Override
    public ThreatMatch check(String url) {
        try {
            Set<UrlHashCollision> collisions = findHashPrefixCollisions(url);
            if (!collisions.isEmpty()) {
                List<ThreatMatch> threats = checkCollisions(collisions);
                if (!threats.isEmpty()) {
                    return selectMoreGenericThreat(threats);
                }
            } else {
                LOGGER.info("URL hash not in local database: {}", url);
            }
        } catch (IOException | DecoderException ex) {
            LOGGER.error("Failed to check in Safe Browsing lists", ex);
        }
        return null;
    }

    @Override
    Logger getLogger() {
        return LOGGER;
    }

    private List<ThreatMatch> checkCollisions(Set<UrlHashCollision> collisions) throws IOException, DecoderException {
        List<ThreatMatch> threats = new ArrayList<>();

        // first, check for unexpired positive cache hits
        for (UrlHashCollision collision : collisions) {
            ThreatMatch match = cache.getPositive(collision.fullHash, collision.descriptor, false);
            if (match != null) {
                threats.add(match);
            }
        }
        // if there are unexpired positive cache entries, then it is unsafe URL
        if (!threats.isEmpty()) {
            LOGGER.info("Unexpired positive cache hit found");
            return threats;
        }

        // check if we have expired positive cache entries
        for (UrlHashCollision collision : collisions) {
            ThreatMatch match = cache.getPositive(collision.fullHash, collision.descriptor, true);
            if (match != null) {
                LOGGER.info("Expired positive cache hit found. Sending full hash request.");
                return requestFullHashes(collisions);
            }
        }

        Iterator<UrlHashCollision> it = collisions.iterator();
        while (it.hasNext()) {
            UrlHashCollision collision = it.next();
            if (cache.hasNegative(collision.hashPrefix, collision.descriptor)) {
                it.remove();
            }
        }
        if (collisions.isEmpty()) {
            LOGGER.info("Hash prefixes are in a negative cache. Considering URL as safe.");
            return Collections.emptyList();
        }
        return requestFullHashes(collisions);
    }

    /**
     * This method checks hash prefixes of the URL whether they are listed in the local database and returns all
     * existing collisions.
     *
     * @param url URL string to check
     * @return collection of hash prefix collisions in local database; never {@code null}
     * @throws IOException when local database IO error occurs
     */
    private Set<UrlHashCollision> findHashPrefixCollisions(String url) throws IOException {
        String canonicalized = canonicalizer.canonicalize(url);
        Set<String> expressions = expressionGenerator.makeExpressions(canonicalized);

        Set<UrlHashCollision> collisions = new HashSet<>();
        for (int n = Hashing.MIN_SIGNIFICANT_BYTES; n < Hashing.MAX_SIGNIFICANT_BYTES; n++) {
            for (String expression : expressions) {
                String prefix = hashing.computeHashPrefix(expression, n);
                List<ThreatListDescriptor> lists = presentInThreatLists(prefix);
                for (ThreatListDescriptor descriptor : lists) {
                    UrlHashCollision collision = new UrlHashCollision();
                    collision.hashPrefix = prefix;
                    collision.fullHash = hashing.computeFullHash(expression);
                    collision.descriptor = descriptor;
                    collisions.add(collision);
                }
            }
        }
        return collisions;
    }

    private List<ThreatListDescriptor> presentInThreatLists(String prefix) throws IOException {
        List<ThreatListDescriptor> lists = new ArrayList<>();
        for (ThreatListDescriptor descriptor : descriptorsCache.get()) {
            if (localDatabase.contains(prefix, descriptor)) {
                lists.add(descriptor);
            }
        }
        return lists;
    }

    private List<ThreatMatch> requestFullHashes(Set<UrlHashCollision> collisions) throws DecoderException, IOException {
        if (!stateHolder.isFindAllowed()) {
            LOGGER.info("Skipping full hash find requests to API due to wait duration");
            return Collections.emptyList();
        }

        List<String> clientStates = new ArrayList<>();
        for (ThreatListDescriptor descriptor : descriptorsCache.get()) {
            clientStates.add(stateHolder.getState(descriptor));
        }

        // prepare set of included threat lists to be used for negative caching
        Set<ThreatListDescriptor> lists = new HashSet<>();
        for (UrlHashCollision collision : collisions) {
            lists.add(collision.descriptor);
        }

        ThreatInfo threatInfo = new ThreatInfo();
        threatInfo.getThreatTypes().clear();
        threatInfo.getPlatformTypes().clear();
        for (UrlHashCollision collision : collisions) {
            threatInfo.getThreatTypes().add(collision.descriptor.getThreatType());
            threatInfo.getPlatformTypes().add(collision.descriptor.getPlatformType());
            threatInfo.getThreatEntries().add(makeThreatEntry(collision.hashPrefix));
        }

        Map<String, Object> payload = wrapPayload("clientStates", clientStates);
        payload.put("threatInfo", threatInfo);

        ApiResponse apiResponse = null;
        HttpUriRequest req = makeRequest(HttpPost.METHOD_NAME, "fullHashes:find", payload);
        try ( CloseableHttpResponse resp = httpClient.execute(req);
             Reader reader = getResponseReader(resp)) {
            // TODO: back-off on status codes other than 200
            apiResponse = gson.fromJson(reader, ApiResponse.class);
        } finally {
            if (apiResponse != null && apiResponse.minimumWaitDuration != null) {
                long duration = Gsb4j.durationToMillis(apiResponse.minimumWaitDuration);
                stateHolder.setMinWaitDurationForFinds(duration);
            }
        }
        if (apiResponse == null) {
            throw new IllegalStateException("Invalid payload from API");
        }

        List<ThreatMatch> responseMatches = Optional.ofNullable(apiResponse.matches).orElse(Collections.emptyList());
        List<ThreatMatch> matches = new ArrayList<>();
        for (ThreatMatch match : responseMatches) {
            boolean fullHashMatchFound = collisions.removeIf(c -> c.matches(match));
            if (fullHashMatchFound) {
                matches.add(match);
                cache.putPositive(match);
            }
        }

        if (apiResponse.negativeCacheDuration != null) {
            long duration = Gsb4j.durationToMillis(apiResponse.negativeCacheDuration);
            collisions.forEach(c -> cache.putNegative(c.hashPrefix, duration, lists));
        }

        LOGGER.info("Response to full hash request: {}", gson.toJson(matches));
        return matches;
    }

    private ThreatEntry makeThreatEntry(String prefix) throws DecoderException {
        byte[] bytes = Hex.decodeHex(prefix.toCharArray());
        String base64encoded = Base64.getEncoder().encodeToString(bytes);

        ThreatEntry threatEntry = new ThreatEntry();
        threatEntry.setHash(base64encoded);
        return threatEntry;
    }

    /**
     * Class to represent a hit in the local database for a hash prefix.
     */
    private static class UrlHashCollision {

        String hashPrefix;
        String fullHash;
        ThreatListDescriptor descriptor;

        @Override
        public int hashCode() {
            return Objects.hash(this.fullHash, this.descriptor);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof UrlHashCollision) {
                UrlHashCollision other = (UrlHashCollision) obj;
                return Objects.equals(this.fullHash, other.fullHash)
                    && Objects.equals(this.descriptor, other.descriptor);
            }
            return false;
        }

        boolean matches(ThreatMatch match) {
            boolean descriptorMatch = match.getThreatType() == descriptor.getThreatType()
                && match.getPlatformType() == descriptor.getPlatformType()
                && match.getThreatEntryType() == descriptor.getThreatEntryType();
            if (descriptorMatch) {
                byte[] bytes = Base64.getDecoder().decode(match.getThreat().getHash());
                String fullHashRemote = Hex.encodeHexString(bytes);
                return fullHash.equals(fullHashRemote);
            }
            return false;
        }

    }

    private static class ApiResponse {

        List<ThreatMatch> matches;
        String minimumWaitDuration;
        String negativeCacheDuration;
    }

}

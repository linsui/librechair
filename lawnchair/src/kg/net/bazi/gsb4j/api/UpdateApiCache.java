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
import com.google.inject.Singleton;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kg.net.bazi.gsb4j.Gsb4jBinding;
import kg.net.bazi.gsb4j.data.ThreatListDescriptor;
import kg.net.bazi.gsb4j.data.ThreatMatch;

/**
 * Cache for threat matches from Update API.
 *
 * @author azilet
 */
@Singleton
class UpdateApiCache extends ApiResponseCacheBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateApiCache.class);

    private ConcurrentMap<PositiveCacheKey, ThreatMatch> positiveCache = new ConcurrentHashMap<>();
    private ConcurrentMap<String, NegativeCacheEntry> negativeCache = new ConcurrentHashMap<>();

    @Inject
    UpdateApiCache(@Gsb4jBinding ScheduledExecutorService scheduler) {
        startMe(scheduler, 1, 5, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        int removedNegative = 0;
        Iterator<NegativeCacheEntry> negativeIt = negativeCache.values().iterator();
        while (negativeIt.hasNext()) {
            NegativeCacheEntry entry = negativeIt.next();
            if (entry.timestamp + entry.duration < System.currentTimeMillis()) {
                negativeIt.remove();
                removedNegative++;
            }
        }
        if (removedNegative > 0) {
            LOGGER.info("Removed {} negative cache entries", removedNegative);
        }

        int removedPositive = 0;
        Iterator<ThreatMatch> positiveIt = positiveCache.values().iterator();
        while (positiveIt.hasNext()) {
            ThreatMatch match = positiveIt.next();
            if (isExpired(match)) {
                positiveIt.remove();
                removedPositive++;
            }
        }
        if (removedPositive > 0) {
            LOGGER.info("Removed {} positive cache entries", removedPositive);
        }
    }

    /**
     * Gets positive cache entry for the full hash.
     *
     * @param fullHash full hash to get cached threat match for
     * @param descriptor the value of descriptor
     * @param expired indicates if selected cache entry shall be unexpired or expired
     * @return the kg.net.bazi.gsb4j.data.ThreatMatch
     */
    public ThreatMatch getPositive(String fullHash, ThreatListDescriptor descriptor, boolean expired) {
        PositiveCacheKey key = new PositiveCacheKey(fullHash, descriptor);
        ThreatMatch match = positiveCache.get(key);
        if (match != null) {
            if (expired && isExpired(match)) {
                return match;
            }
            if (!expired && !isExpired(match)) {
                return match;
            }
        }
        return null;
    }

    /**
     * Puts threat match as a positive cache entry. Positive cache entries are created for full hashes and expiration
     * depends on "cacheDuration" field.
     *
     * @param match match to create cache entry for
     */
    public void putPositive(ThreatMatch match) {
        byte[] bytes = Base64.getDecoder().decode(match.getThreat().getHash());
        String hexFullHash = Hex.encodeHexString(bytes);

        ThreatListDescriptor descriptor = makeDesceiptor(match);
        PositiveCacheKey key = new PositiveCacheKey(hexFullHash, descriptor);

        match.setTimestamp(System.currentTimeMillis());
        positiveCache.put(key, match);
    }

    /**
     * Checks if there is a negative cache entry for the hash prefix.
     *
     * @param hashPrefix hash prefix to check
     * @param threatlist threat list for which hash prefix should be checked against
     * @return {@code true} if there is a negative cache entry; {@code false} otherwise
     */
    public boolean hasNegative(String hashPrefix, ThreatListDescriptor threatlist) {
        NegativeCacheEntry ce = negativeCache.get(hashPrefix);
        return ce != null && ce.timestamp + ce.duration > System.currentTimeMillis() && ce.lists.contains(threatlist);
    }

    /**
     * Puts a negative cache entry for the hash prefix.
     *
     * @param hashPrefix hash prefix create negative cache entry for
     * @param duration duration of how long full hashes with the supplied prefix are to be considered safe
     * @param lists threat lists for which full hashes with the supplied prefix are to be considered safe
     */
    public void putNegative(String hashPrefix, long duration, Set<ThreatListDescriptor> lists) {
        negativeCache.put(hashPrefix, new NegativeCacheEntry(duration, lists));
    }

    private ThreatListDescriptor makeDesceiptor(ThreatMatch match) {
        ThreatListDescriptor descriptor = new ThreatListDescriptor();
        descriptor.setThreatType(match.getThreatType());
        descriptor.setPlatformType(match.getPlatformType());
        descriptor.setThreatEntryType(match.getThreatEntryType());
        return descriptor;
    }

    private static class PositiveCacheKey {

        private final String fullHash;
        private final ThreatListDescriptor descriptor;

        public PositiveCacheKey(String fullHash, ThreatListDescriptor descriptor) {
            this.fullHash = fullHash;
            this.descriptor = descriptor;
        }

        @Override
        public int hashCode() {
            return Objects.hash(fullHash, descriptor);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof PositiveCacheKey) {
                PositiveCacheKey other = (PositiveCacheKey) obj;
                return fullHash.equals(other.fullHash) && descriptor.equals(other.descriptor);
            }
            return false;
        }
    }

    private static class NegativeCacheEntry {

        long duration;
        long timestamp = System.currentTimeMillis();
        Set<ThreatListDescriptor> lists;

        public NegativeCacheEntry(long duration, Set<ThreatListDescriptor> lists) {
            this.duration = duration;
            this.lists = lists;
        }
    }

}

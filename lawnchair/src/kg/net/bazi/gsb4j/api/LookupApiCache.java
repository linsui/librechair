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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kg.net.bazi.gsb4j.Gsb4jBinding;
import kg.net.bazi.gsb4j.data.ThreatMatch;

/**
 * Cache for threat matches from Lookup API.
 *
 * @author azilet
 */
@Singleton
class LookupApiCache extends ApiResponseCacheBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(LookupApiCache.class);

    private ConcurrentMap<String, ThreatMatch> items = new ConcurrentHashMap<>();

    @Inject
    LookupApiCache(@Gsb4jBinding ScheduledExecutorService scheduler) {
        startMe(scheduler, 10, 60, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        Iterator<ThreatMatch> it = items.values().iterator();
        while (it.hasNext()) {
            ThreatMatch match = it.next();
            if (isExpired(match)) {
                it.remove();
                LOGGER.info("Cached threat removed: {}", match.getThreat().getUrl());
            }
        }
    }

    /**
     * Checks if the supplied URL is in the cache.
     *
     * @param url URL to check
     * @return threat match if found in the cache; {@code null} otherwise
     */
    public ThreatMatch get(String url) {
        ThreatMatch match = items.get(url);
        return match != null && !isExpired(match) ? match : null;
    }

    /**
     * Puts supplied threat match into this cache.
     *
     * @param match threat match to cache
     */
    public void put(ThreatMatch match) {
        match.setTimestamp(System.currentTimeMillis());
        items.put(match.getThreat().getUrl(), match);
    }

}

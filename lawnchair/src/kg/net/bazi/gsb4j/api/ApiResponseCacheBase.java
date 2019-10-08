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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kg.net.bazi.gsb4j.Gsb4j;
import kg.net.bazi.gsb4j.data.ThreatMatch;

/**
 * Base class for threats cache implementations.
 *
 * @author azilet
 */
abstract class ApiResponseCacheBase implements Runnable {

    final void startMe(ScheduledExecutorService scheduler, long initialDelay, long delay, TimeUnit unit) {
        scheduler.scheduleWithFixedDelay(this, initialDelay, delay, unit);
    }

    /**
     * Checks if threat match has expired.
     *
     * @param match threat match to check
     * @return {@code true} if match has expired; {@code false} otherwise
     */
    boolean isExpired(ThreatMatch match) {
        String duration = match.getCacheDuration();
        return match.getTimestamp() + Gsb4j.durationToMillis(duration) < System.currentTimeMillis();
    }
}

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
import com.google.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kg.net.bazi.gsb4j.Gsb4jBinding;

/**
 * Threat list update runner that periodically tries to make update requests.
 *
 * @author azilet
 */
class ThreatListUpdateRunner implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreatListUpdateRunner.class);

    @Inject
    private Provider<ThreatListUpdater> updateProvider;

    @Inject
    public ThreatListUpdateRunner(@Gsb4jBinding ScheduledExecutorService scheduler) {
        startMe(scheduler);
    }

    @Override
    public void run() {
        ThreatListUpdater updater = updateProvider.get();
        try {
            updater.requestUpdate();
        } catch (IOException | RuntimeException ex) {
            LOGGER.error("Failed to perform list update request", ex);
        }
    }

    private void startMe(ScheduledExecutorService scheduler) {
        scheduler.scheduleWithFixedDelay(this, 5, 600, TimeUnit.SECONDS);
    }

}

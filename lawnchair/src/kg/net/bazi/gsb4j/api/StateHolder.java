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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import kg.net.bazi.gsb4j.data.ThreatListDescriptor;
import kg.net.bazi.gsb4j.properties.Gsb4jProperties;

/**
 * Client state holder for Update API.
 *
 * @author azilet
 */
@Singleton
class StateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateHolder.class);
    private static final String UPDATE_MIN_WAIT_DURATION_KEY = "update_min_wait_duration";
    private static final String UPDATE_MIN_WAIT_TIMESTAMP_KEY = "update_min_wait_timestamp";
    private static final String FIND_MIN_WAIT_DURATION_KEY = "find_min_wait_duration";
    private static final String FIND_MIN_WAIT_TIMESTAMP_KEY = "find_min_wait_timestamp";

    private final Gsb4jProperties properties;
    private final Properties states = new Properties();

    @Inject
    public StateHolder(Gsb4jProperties properties) {
        this.properties = properties;

        File file = getStatesFile(properties.getDataDirectory());
        if (file.exists()) {
            try (InputStream is = new FileInputStream(file)) {
                states.load(is);
            } catch (IOException ex) {
                LOGGER.info("Failed to load client states", ex);
            }
        } else {
            LOGGER.info("No file to read states from");
        }
    }

    /**
     * Gets client state.
     *
     * @param descriptor descriptor to get client state for
     * @return client state; empty string if client state is undefined
     */
    public String getState(ThreatListDescriptor descriptor) {
        String key = descriptor.toString();
        return states.getProperty(key, "");
    }

    /**
     * Sets client state for the list.
     *
     * @param descriptor threat list descriptor to set state for
     * @param state client state to set; use {@code null} to clear state for the description
     */
    public void setState(ThreatListDescriptor descriptor, String state) {
        String key = descriptor.toString();
        if (state != null) {
            states.setProperty(key, state);
        } else {
            states.remove(key);
        }
        try {
            dumpToFile();
            LOGGER.info("State for {} set to '{}'", key, state);
        } catch (IOException ex) {
            LOGGER.error("Failed to persist state for {}: {}", key, state, ex);
        }
    }

    /**
     * Sets minimum wait duration after which list update requests can be sent.
     *
     * @param minimumWaitDuration minimum wait duration in millis
     */
    public void setMinWaitDurationForUpdates(long minimumWaitDuration) {
        states.setProperty(UPDATE_MIN_WAIT_DURATION_KEY, Long.toString(minimumWaitDuration));
        states.setProperty(UPDATE_MIN_WAIT_TIMESTAMP_KEY, Long.toString(System.currentTimeMillis()));
        try {
            dumpToFile();
        } catch (IOException ex) {
            LOGGER.error("Failed to persist minimum wait duration", ex);
        }
    }

    /**
     * Sets minimum wait duration after which full hash find requests can be sent.
     *
     * @param minimumWaitDuration minimum wait duration in millis
     */
    public void setMinWaitDurationForFinds(long minimumWaitDuration) {
        states.setProperty(FIND_MIN_WAIT_DURATION_KEY, Long.toString(minimumWaitDuration));
        states.setProperty(FIND_MIN_WAIT_TIMESTAMP_KEY, Long.toString(System.currentTimeMillis()));
        try {
            dumpToFile();
        } catch (IOException ex) {
            LOGGER.error("Failed to persist minimum wait duration", ex);
        }
    }

    /**
     * Checks if list update requests are allowed based on the minimum wait duration value.
     *
     * @return {@code true} if list update requests are allowed; {@code false} otherwise
     */
    public boolean isUpdateAllowed() {
        long minimumWaitDuration = Long.parseLong(states.getProperty(UPDATE_MIN_WAIT_DURATION_KEY, "0"));
        if (minimumWaitDuration > 0) {
            long timestamp = Long.parseLong(states.getProperty(UPDATE_MIN_WAIT_TIMESTAMP_KEY));
            return minimumWaitDuration + timestamp < System.currentTimeMillis();
        }
        return true;
    }

    /**
     * Checks if full hash requests are allowed based on the minimum wait duration value.
     *
     * @return {@code true} if full hash requests are allowed; {@code false} otherwise
     */
    public boolean isFindAllowed() {
        long minWaitDuration = Long.parseLong(states.getProperty(FIND_MIN_WAIT_DURATION_KEY, "0"));
        if (minWaitDuration > 0) {
            long timestamp = Long.parseLong(states.getProperty(FIND_MIN_WAIT_TIMESTAMP_KEY));
            return minWaitDuration + timestamp < System.currentTimeMillis();
        }
        return true;
    }

    private File getStatesFile(Path parent) {
        if (!parent.toFile().exists()) {
            try {
                Files.createDirectories(parent);
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to create parent directory", ex);
            }
        }
        return parent.resolve("states").toFile();
    }

    private synchronized void dumpToFile() throws IOException {
        File file = getStatesFile(properties.getDataDirectory());
        try (OutputStream os = new FileOutputStream(file)) {
            states.store(os, "");
        }
    }

}

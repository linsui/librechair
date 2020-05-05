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

package kg.net.bazi.gsb4j.properties;

import com.google.inject.ImplementedBy;

import java.nio.file.Path;
import java.nio.file.Paths;

import kg.net.bazi.gsb4j.Gsb4j;

/**
 * Interface to get application specific properties.
 *
 * @author <a href="https://github.com/bazi">bazi</a>
 */
@ImplementedBy(Gsb4jSystemProperties.class)
public interface Gsb4jProperties {

    /**
     * Gets API key.
     *
     * @return API key
     */
    String getApiKey();

    /**
     * Gets HTTP referrer value accepted by API.
     *
     * @return HTTP referrer value as specified in project console for the API key; may be {@code null} if not specified
     */
    String getApiHttpReferrer();

    /**
     * Gets directory for local data files.
     *
     * @return path to local data directory
     */
    Path getDataDirectory();

    /**
     * Gets default local data directory. This is directory in user's home directory.
     *
     * @return path to default local data directory
     */
    static Path getDefaultDataDirectory() {
        String homeDir = System.getProperty("user.home");
        return Paths.get(homeDir, Gsb4j.GSB4J);
    }

}

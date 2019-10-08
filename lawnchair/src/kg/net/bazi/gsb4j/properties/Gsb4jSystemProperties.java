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

import com.google.inject.ProvisionException;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementation of {@link Gsb4jProperties} which uses system properties as the source for values.
 *
 * @author azilet
 */
class Gsb4jSystemProperties implements Gsb4jProperties {

    public Gsb4jSystemProperties() {
        if (getApiKey() == null) {
            throw new ProvisionException("API key not supplied");
        }
    }

    @Override
    public String getApiKey() {
        return System.getProperty(Gsb4jPropertyKeys.API_KEY);
    }

    @Override
    public String getApiHttpReferrer() {
        return System.getProperty(Gsb4jPropertyKeys.API_HTTP_REFERRER);
    }

    @Override
    public Path getDataDirectory() {
        String dir = System.getProperty(Gsb4jPropertyKeys.DATA_DIRECTORY);
        if (dir != null && !dir.isEmpty()) {
            return Paths.get(dir);
        }
        return Gsb4jProperties.getDefaultDataDirectory();
    }
}

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

import com.google.inject.Inject;
import com.google.inject.ProvisionException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import kg.net.bazi.gsb4j.Gsb4jBinding;

/**
 * Implementation of {@link Gsb4jProperties} which uses supplied properties file as the source for values.
 *
 * @author <a href="https://github.com/bazi">bazi</a>
 */
class Gsb4jFileProperties implements Gsb4jProperties {

    final Properties properties;

    @Inject
    public Gsb4jFileProperties(@Gsb4jBinding Properties properties) {
        if (!properties.containsKey(Gsb4jPropertyKeys.API_KEY)) {
            throw new ProvisionException("API key not supplied");
        }
        this.properties = properties;
    }

    @Override
    public String getApiKey() {
        return properties.getProperty(Gsb4jPropertyKeys.API_KEY);
    }

    @Override
    public String getApiHttpReferrer() {
        return properties.getProperty(Gsb4jPropertyKeys.API_HTTP_REFERRER);
    }

    @Override
    public Path getDataDirectory() {
        String dataDir = properties.getProperty(Gsb4jPropertyKeys.DATA_DIRECTORY);
        if (dataDir != null && !dataDir.isEmpty()) {
            return Paths.get(dataDir);
        }
        return Gsb4jProperties.getDefaultDataDirectory();
    }

}

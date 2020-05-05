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

package kg.net.bazi.gsb4j.data.updates;

import java.util.Arrays;

/**
 * Update request constraints POJO.
 *
 * @author azilet
 */
public class Constraints {

    private int maxUpdateEntries;
    private int maxDatabaseEntries;
    private String region;
    private CompressionType[] supportedCompressions;

    public int getMaxUpdateEntries() {
        return maxUpdateEntries;
    }

    public void setMaxUpdateEntries(int maxUpdateEntries) {
        this.maxUpdateEntries = maxUpdateEntries;
    }

    public int getMaxDatabaseEntries() {
        return maxDatabaseEntries;
    }

    public void setMaxDatabaseEntries(int maxDatabaseEntries) {
        this.maxDatabaseEntries = maxDatabaseEntries;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public CompressionType[] getSupportedCompressions() {
        return Arrays.copyOf(supportedCompressions, supportedCompressions.length);
    }

    public void setSupportedCompressions(CompressionType[] supportedCompressions) {
        this.supportedCompressions = Arrays.copyOf(supportedCompressions, supportedCompressions.length);
    }

}

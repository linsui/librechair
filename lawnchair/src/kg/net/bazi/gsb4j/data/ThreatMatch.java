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

package kg.net.bazi.gsb4j.data;

import java.util.List;

/**
 * POJO to represent a match when checking a threat entry in the Safe Browsing threat lists.
 *
 * @author azilet
 */
public class ThreatMatch {

    private ThreatType threatType;
    private PlatformType platformType;
    private ThreatEntryType threatEntryType;
    private ThreatEntry threat;
    private ThreatEntryMetadata threatEntryMetadata;
    private String cacheDuration;

    private transient long timestamp;

    public ThreatType getThreatType() {
        return threatType;
    }

    public void setThreatType(ThreatType threatType) {
        this.threatType = threatType;
    }

    public PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(PlatformType platformType) {
        this.platformType = platformType;
    }

    public ThreatEntryType getThreatEntryType() {
        return threatEntryType;
    }

    public void setThreatEntryType(ThreatEntryType threatEntryType) {
        this.threatEntryType = threatEntryType;
    }

    public ThreatEntry getThreat() {
        return threat;
    }

    public void setThreat(ThreatEntry threat) {
        this.threat = threat;
    }

    public ThreatEntryMetadata getThreatEntryMetadata() {
        return threatEntryMetadata;
    }

    public void setThreatEntryMetadata(ThreatEntryMetadata threatEntryMetadata) {
        this.threatEntryMetadata = threatEntryMetadata;
    }

    public String getCacheDuration() {
        return cacheDuration;
    }

    public void setCacheDuration(String cacheDuration) {
        this.cacheDuration = cacheDuration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * The metadata associated with a specific threat entry.
     */
    public static class ThreatEntryMetadata {

        private List<MetadataEntry> entries;

        public List<MetadataEntry> getEntries() {
            return entries;
        }

        public void setEntries(List<MetadataEntry> entries) {
            this.entries = entries;
        }
    }

    /**
     * A single metadata entry.
     */
    public static class MetadataEntry {

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}

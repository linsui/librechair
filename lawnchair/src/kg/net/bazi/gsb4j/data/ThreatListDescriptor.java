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

import java.util.Objects;

/**
 * Describes an individual threat list.
 *
 * @author azilet
 */
public class ThreatListDescriptor {

    private ThreatType threatType;
    private PlatformType platformType;
    private ThreatEntryType threatEntryType;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.threatType);
        hash = 29 * hash + Objects.hashCode(this.platformType);
        hash = 29 * hash + Objects.hashCode(this.threatEntryType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ThreatListDescriptor) {
            ThreatListDescriptor other = (ThreatListDescriptor) obj;
            return this.threatType == other.threatType
                && this.platformType == other.platformType
                && this.threatEntryType == other.threatEntryType;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(enumShortener(threatType));
        sb.append("_");
        sb.append(enumShortener(platformType));
        sb.append("_");
        sb.append(enumShortener(threatEntryType));
        return sb.toString();
    }

    private <T extends Enum> String enumShortener(T item) {
        return item.name().substring(0, 3);
    }

}

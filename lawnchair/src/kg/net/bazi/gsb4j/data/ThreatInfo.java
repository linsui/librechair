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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * The information regarding one or more threats that a client submits when checking for matches in threat lists.
 *
 * @author azilet
 */
public class ThreatInfo {

    private Set<ThreatType> threatTypes = ThreatType.getAll();
    private Set<PlatformType> platformTypes = EnumSet.of(PlatformType.ANY_PLATFORM);
    private Set<ThreatEntryType> threatEntryTypes = EnumSet.of(ThreatEntryType.URL);
    private Set<ThreatEntry> threatEntries = new HashSet<>();

    public Set<ThreatType> getThreatTypes() {
        return threatTypes;
    }

    public void setThreatTypes(Set<ThreatType> threatTypes) {
        this.threatTypes = threatTypes;
    }

    public Set<PlatformType> getPlatformTypes() {
        return platformTypes;
    }

    public void setPlatformTypes(Set<PlatformType> platformTypes) {
        this.platformTypes = platformTypes;
    }

    public Set<ThreatEntryType> getThreatEntryTypes() {
        return threatEntryTypes;
    }

    public void setThreatEntryTypes(Set<ThreatEntryType> threatEntryTypes) {
        this.threatEntryTypes = threatEntryTypes;
    }

    public Set<ThreatEntry> getThreatEntries() {
        return threatEntries;
    }

    public void setThreatEntries(Set<ThreatEntry> threatEntries) {
        this.threatEntries = threatEntries;
    }

}

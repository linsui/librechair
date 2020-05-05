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

import kg.net.bazi.gsb4j.data.PlatformType;
import kg.net.bazi.gsb4j.data.ThreatEntryType;
import kg.net.bazi.gsb4j.data.ThreatType;

/**
 * A single list update request POJO.
 *
 * @author azilet
 */
public class ListUpdateRequest {

    private ThreatType threatType;
    private PlatformType platformType;
    private ThreatEntryType threatEntryType;
    private String state;
    private Constraints constraints;

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Constraints getConstraints() {
        return constraints;
    }

    public void setConstraints(Constraints constraints) {
        this.constraints = constraints;
    }

}

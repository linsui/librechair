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

import java.util.List;

import kg.net.bazi.gsb4j.data.PlatformType;
import kg.net.bazi.gsb4j.data.ThreatEntryType;
import kg.net.bazi.gsb4j.data.ThreatType;

/**
 * An update to an individual list.
 *
 * @author azilet
 */
public class ListUpdateResponse {

    private ThreatType threatType;
    private ThreatEntryType threatEntryType;
    private PlatformType platformType;
    private ResponseType responseType;
    private List<ThreatEntrySet> additions;
    private List<ThreatEntrySet> removals;
    private String newClientState;
    private Checksum checksum;

    public ThreatType getThreatType() {
        return threatType;
    }

    public void setThreatType(ThreatType threatType) {
        this.threatType = threatType;
    }

    public ThreatEntryType getThreatEntryType() {
        return threatEntryType;
    }

    public void setThreatEntryType(ThreatEntryType threatEntryType) {
        this.threatEntryType = threatEntryType;
    }

    public PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(PlatformType platformType) {
        this.platformType = platformType;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public List<ThreatEntrySet> getAdditions() {
        return additions;
    }

    public void setAdditions(List<ThreatEntrySet> additions) {
        this.additions = additions;
    }

    public List<ThreatEntrySet> getRemovals() {
        return removals;
    }

    public void setRemovals(List<ThreatEntrySet> removals) {
        this.removals = removals;
    }

    public String getNewClientState() {
        return newClientState;
    }

    public void setNewClientState(String newClientState) {
        this.newClientState = newClientState;
    }

    public Checksum getChecksum() {
        return checksum;
    }

    public void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }

    /**
     * The type of response sent to the client.
     */
    public enum ResponseType {
        RESPONSE_TYPE_UNSPECIFIED, PARTIAL_UPDATE, FULL_UPDATE
    }

    /**
     * The expected state of a client's local database.
     */
    public static class Checksum {

        private String sha256;

        public String getSha256() {
            return sha256;
        }

        public void setSha256(String sha256) {
            this.sha256 = sha256;
        }

    }

}

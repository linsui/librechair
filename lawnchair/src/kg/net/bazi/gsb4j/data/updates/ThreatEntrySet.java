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

/**
 * A set of threats that should be added or removed from a client's local database.
 *
 * @author azilet
 */
public class ThreatEntrySet {

    private CompressionType compressionType;
    private RawHashes rawHashes;
    private RawIndices rawIndices;
    private RiceDeltaEncoding riceHashes;
    private RiceDeltaEncoding riceIndices;

    public CompressionType getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(CompressionType compressionType) {
        this.compressionType = compressionType;
    }

    public RawHashes getRawHashes() {
        return rawHashes;
    }

    public void setRawHashes(RawHashes rawHashes) {
        this.rawHashes = rawHashes;
    }

    public RawIndices getRawIndices() {
        return rawIndices;
    }

    public void setRawIndices(RawIndices rawIndices) {
        this.rawIndices = rawIndices;
    }

    public void setRiceHashes(RiceDeltaEncoding riceHashes) {
        this.riceHashes = riceHashes;
    }

    public RiceDeltaEncoding getRiceHashes() {
        return riceHashes;
    }

    public RiceDeltaEncoding getRiceIndices() {
        return riceIndices;
    }

    public void setRiceIndices(RiceDeltaEncoding riceIndices) {
        this.riceIndices = riceIndices;
    }

    /**
     * The uncompressed threat entries in hash format of a particular prefix length.
     */
    public static class RawHashes {

        private int prefixSize;
        private String rawHashes;

        public int getPrefixSize() {
            return prefixSize;
        }

        public void setPrefixSize(int prefixSize) {
            this.prefixSize = prefixSize;
        }

        public String getRawHashes() {
            return rawHashes;
        }

        public void setRawHashes(String rawHashes) {
            this.rawHashes = rawHashes;
        }

    }

    /**
     * A set of raw indices to remove from a local list.
     */
    public static class RawIndices {

        private List<Integer> indices;

        public List<Integer> getIndices() {
            return indices;
        }

        public void setIndices(List<Integer> indices) {
            this.indices = indices;
        }

    }

    /**
     * The Rice-Golomb encoded data. Used for sending compressed 4-byte hashes or compressed removal indices.
     */
    public static class RiceDeltaEncoding {

        private String firstValue;
        private int riceParameter;
        private int numEntries;
        private String encodedData;

        public String getFirstValue() {
            return firstValue;
        }

        public void setFirstValue(String firstValue) {
            this.firstValue = firstValue;
        }

        public int getRiceParameter() {
            return riceParameter;
        }

        public void setRiceParameter(int riceParameter) {
            this.riceParameter = riceParameter;
        }

        public int getNumEntries() {
            return numEntries;
        }

        public void setNumEntries(int numEntries) {
            this.numEntries = numEntries;
        }

        public String getEncodedData() {
            return encodedData;
        }

        public void setEncodedData(String encodedData) {
            this.encodedData = encodedData;
        }
    }

}

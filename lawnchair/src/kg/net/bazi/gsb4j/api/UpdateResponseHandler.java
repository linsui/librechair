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

package kg.net.bazi.gsb4j.api;

import com.google.inject.Inject;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import kg.net.bazi.gsb4j.data.ThreatListDescriptor;
import kg.net.bazi.gsb4j.data.updates.CompressionType;
import kg.net.bazi.gsb4j.data.updates.ListUpdateResponse;
import kg.net.bazi.gsb4j.data.updates.ThreatEntrySet;
import kg.net.bazi.gsb4j.db.LocalDatabase;
import kg.net.bazi.gsb4j.util.RiceCompression;

/**
 * Parses and applies threat list updates from API to local database.
 *
 * @author azilet
 */
class UpdateResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateResponseHandler.class);

    @Inject
    LocalDatabase localDatabase;

    @Inject
    StateHolder stateHolder;

    @Inject
    RiceCompression riceCompression;

    /**
     * Applies list updates to local database.
     *
     * @param updateResponses list updates
     * @return number of list updates successfully updated and applied to local database
     * @throws IOException when database read/write errors occur
     */
    public int apply(List<ListUpdateResponse> updateResponses) throws IOException {
        AtomicInteger counter = new AtomicInteger();
        for (ListUpdateResponse updateResponse : updateResponses) {
            ThreatListDescriptor descriptor = makeDescriptor(updateResponse);
            if (updateResponse.getResponseType() == ListUpdateResponse.ResponseType.FULL_UPDATE) {
                LOGGER.info("===== Applying FULL update for {} =====", descriptor);
                List<String> updatedHashes = doFullUpdate(updateResponse);
                verifyAndSave(updatedHashes, updateResponse, counter);
            } else if (updateResponse.getResponseType() == ListUpdateResponse.ResponseType.PARTIAL_UPDATE) {
                LOGGER.info("===== Applying PARTIAL update for {} =====", descriptor);
                List<String> updatedHashes = doPartialUpdate(updateResponse);
                verifyAndSave(updatedHashes, updateResponse, counter);
            } else {
                LOGGER.warn("Unknown response type: {}", updateResponse.getResponseType());
            }
            LOGGER.info("");
        }
        return counter.get();
    }

    /**
     * Applies list update to local database.
     *
     * @param updateResponse update to apply
     * @return list of updated hash prefixes; if no updates are applied empty list is returned
     * @throws IOException when database access errors occur
     */
    private List<String> doPartialUpdate(ListUpdateResponse updateResponse) throws IOException {
        final List<ThreatEntrySet> empty = Collections.emptyList();
        List<ThreatEntrySet> removals = Optional.ofNullable(updateResponse.getRemovals()).orElse(empty);
        List<ThreatEntrySet> additions = Optional.ofNullable(updateResponse.getAdditions()).orElse(empty);
        if (removals.isEmpty() && additions.isEmpty()) {
            return Collections.emptyList();
        }

        ThreatListDescriptor descriptor = makeDescriptor(updateResponse);
        List<String> hashes = localDatabase.load(descriptor);

        for (ThreatEntrySet removal : removals) {
            if (removal.getCompressionType() == CompressionType.RICE && removal.getRiceIndices() != null) {
                removeItemsByRiceIndices(removal.getRiceIndices(), hashes);
            } else if (removal.getCompressionType() == CompressionType.RAW && removal.getRawIndices() != null) {
                removeItemsByRawIndices(removal.getRawIndices().getIndices(), hashes);
            }
        }
        for (ThreatEntrySet addition : additions) {
            if (addition.getCompressionType() == CompressionType.RICE && addition.getRiceHashes() != null) {
                addItemsByRiceHashes(addition.getRiceHashes(), hashes);
            } else if (addition.getCompressionType() == CompressionType.RAW && addition.getRawHashes() != null) {
                addItemsByRawHashes(addition.getRawHashes(), hashes);
            }
        }
        return hashes;
    }

    private List<String> doFullUpdate(ListUpdateResponse updateResponse) {
        List<String> hashes = new LinkedList<>();
        for (ThreatEntrySet addition : updateResponse.getAdditions()) {
            if (addition.getCompressionType() == CompressionType.RICE) {
                addItemsByRiceHashes(addition.getRiceHashes(), hashes);
            } else if (addition.getCompressionType() == CompressionType.RAW) {
                addItemsByRawHashes(addition.getRawHashes(), hashes);
            }
        }
        return hashes;
    }

    private void verifyAndSave(List<String> hashes, ListUpdateResponse updateResponse, AtomicInteger counter) throws IOException {
        if (!hashes.isEmpty()) {
            ThreatListDescriptor descriptor = makeDescriptor(updateResponse);
            boolean verified = sortAndVerify(hashes, updateResponse.getChecksum());
            if (verified) {
                LOGGER.info("Client state SUCCESSFULLY verified for {}", descriptor);
                localDatabase.clear(descriptor);
                localDatabase.persist(descriptor, hashes);
                stateHolder.setState(descriptor, updateResponse.getNewClientState());
                counter.incrementAndGet();
            } else {
                LOGGER.info("FAILED to verify client state for {}", descriptor);
                // protocol says to clean local database and send update request again
                // but we keep local database until next successful update
                stateHolder.setState(descriptor, null);
            }
        } else {
            LOGGER.info("No changes applied. Skipping.");
            counter.incrementAndGet();
        }
    }

    private ThreatListDescriptor makeDescriptor(ListUpdateResponse updateResponse) {
        ThreatListDescriptor descriptor = new ThreatListDescriptor();
        descriptor.setThreatType(updateResponse.getThreatType());
        descriptor.setPlatformType(updateResponse.getPlatformType());
        descriptor.setThreatEntryType(updateResponse.getThreatEntryType());
        return descriptor;
    }

    /**
     * Removes items from a list by indices given in another list.
     * <p>
     * This is an optimized implementation for all standard implementations of {@link List}. To be precise: (1)
     * implementation of indices list does not have any effect on the performance of this method; (2) for target list,
     * if it is a linked list then this method will perform at its utmost performance, if it is an array list then this
     * method will perform just like a straightforward implementation where items are removed from the end (there is no
     * better way for array lists).
     *
     * @param indices list of indices to be removed (implementation of list does not matter)
     * @param hashes target list from which items are to be removed (linked list will perform best)
     */
    private void removeItemsByRawIndices(List<Integer> indices, List<String> hashes) {
        int removed = 0;
        Set<Integer> indicesToRemove = new HashSet<>(indices);

        Collections.sort(hashes);
        ListIterator<String> it = hashes.listIterator(hashes.size());
        while (it.hasPrevious() && removed < indicesToRemove.size()) {
            if (indicesToRemove.contains(it.previousIndex())) {
                it.previous();
                it.remove();
                removed++;
            } else {
                it.previous();
            }
        }
        LOGGER.info("Removed {} prefixes", removed);
    }

    private void removeItemsByRiceIndices(ThreatEntrySet.RiceDeltaEncoding riceIndices, List<String> hashes) {
        LOGGER.info("Rice first  : {}", riceIndices.getFirstValue());
        LOGGER.info("Rice param  : {}", riceIndices.getRiceParameter());
        LOGGER.info("Rice entries: {}", riceIndices.getNumEntries());
        LOGGER.info("Rice indices: {}", riceIndices.getEncodedData());

        Integer first = riceIndices.getFirstValue() != null ? Integer.parseInt(riceIndices.getFirstValue()) : 0;
        if (riceIndices.getNumEntries() > 0) {
            byte[] bytes = Base64.getDecoder().decode(riceIndices.getEncodedData());
            List<Integer> deltas = riceCompression.decompress(riceIndices.getRiceParameter(), bytes);

            if (deltas.size() == riceIndices.getNumEntries()) {
                List<Integer> indices = deltas.stream().map(d -> first + d).collect(Collectors.toList());
                removeItemsByRawIndices(indices, hashes);
            } else {
                LOGGER.error("Decompressed indices: {}; expected: {}", deltas.size(), riceIndices.getNumEntries());
            }
        } else if (first < hashes.size()) {
            hashes.remove(first.intValue());
        }
    }

    private void addItemsByRawHashes(ThreatEntrySet.RawHashes rawHashes, List<String> hashes) {
        // API encodes hashes in base64
        byte[] bytes = Base64.getDecoder().decode(rawHashes.getRawHashes());
        int bytesCount = rawHashes.getPrefixSize();

        int added = 0;
        for (int i = 0; i < bytes.length; i += bytesCount) {
            byte[] hash = Arrays.copyOfRange(bytes, i, i + bytesCount);
            hashes.add(Hex.encodeHexString(hash));
            added++;
        }
        LOGGER.info("Added {} prefixes", added);
    }

    private void addItemsByRiceHashes(ThreatEntrySet.RiceDeltaEncoding riceHashes, List<String> hashes) {
        LOGGER.info("Rice first  : {}", riceHashes.getFirstValue());
        LOGGER.info("Rice param  : {}", riceHashes.getRiceParameter());
        LOGGER.info("Rice entries: {}", riceHashes.getNumEntries());
        LOGGER.info("Rice hashes : {}", riceHashes.getEncodedData());

        Integer first = riceHashes.getFirstValue() != null ? Integer.parseUnsignedInt(riceHashes.getFirstValue()) : 0;
        if (riceHashes.getNumEntries() > 0) {
            byte[] bytes = Base64.getDecoder().decode(riceHashes.getEncodedData());
            List<Integer> deltas = riceCompression.decompress(riceHashes.getRiceParameter(), bytes);
            if (deltas.size() == riceHashes.getNumEntries()) {
                deltas.stream().mapToInt(d -> first + d).forEach(i -> hashes.add(Integer.toHexString(i)));
            } else {
                LOGGER.error("Decompressed hashes: {}; expected: {}", deltas.size(), riceHashes.getNumEntries());
            }
        } else if (first != 0) {
            hashes.add(Integer.toHexString(first));
        }
    }

    private boolean sortAndVerify(List<String> hashes, ListUpdateResponse.Checksum checksum) {
        // sort hashes in lexicographic order
        Collections.sort(hashes);

        if (checksum != null && checksum.getSha256() != null) {
            MessageDigest sha256 = null;
            try {
                sha256 = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            try {
                for (String hash : hashes) {
                    byte[] decoded = Hex.decodeHex(hash.toCharArray());
                    sha256.update(decoded);
                }
            } catch (DecoderException ex) {
                LOGGER.error("Shall not happen", ex);
            }

            byte[] bytes = sha256.digest();
            String computed = Base64.getEncoder().encodeToString(bytes);

            LOGGER.info("Expected checksum: {}", checksum.getSha256());
            LOGGER.info("Computed checksum: {}", computed);

            return checksum.getSha256().equals(computed);
        }
        return true;
    }

}

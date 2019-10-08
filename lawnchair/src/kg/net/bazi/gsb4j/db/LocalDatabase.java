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

package kg.net.bazi.gsb4j.db;

import java.io.IOException;
import java.util.List;

import kg.net.bazi.gsb4j.data.ThreatListDescriptor;

/**
 * Local threats list database.
 *
 * @author azilet
 */
public interface LocalDatabase {

    /**
     * Loads hashes for the threat list descriptor.
     *
     * @param descriptor descriptor to load hashes for
     * @return hashes for the descriptor
     * @throws IOException when I/O errors occur
     */
    List<String> load(ThreatListDescriptor descriptor) throws IOException;

    /**
     * Saves hashes for the threat list descriptor.
     *
     * @param descriptor descriptor for the threat list hashes
     * @param hashes hashes to save
     * @throws IOException when I/O errors occur
     */
    void persist(ThreatListDescriptor descriptor, List<String> hashes) throws IOException;

    /**
     * Checks if the hash exists in the local database for the supplied descriptor.
     *
     * @param hash hash prefix to check
     * @param descriptor descriptor to look in
     * @return {@code true} if hash exists in the local database; {@code false} otherwise
     *
     * @throws IOException when IO errors occur
     */
    boolean contains(String hash, ThreatListDescriptor descriptor) throws IOException;

    /**
     * Clears local database data for the descriptor.
     *
     * @param descriptor descriptor to clear data for
     * @throws IOException when I/O errors occur
     */
    void clear(ThreatListDescriptor descriptor) throws IOException;

}

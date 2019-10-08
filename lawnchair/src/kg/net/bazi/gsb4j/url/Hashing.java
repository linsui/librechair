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

package kg.net.bazi.gsb4j.url;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Class that computes hash prefixes of the suffix/prefix expressions.
 *
 * @author azilet
 */
public class Hashing {

    /**
     * Min number of most significant bytes a hash prefix can be made out of a full-length hash.
     */
    public static final int MIN_SIGNIFICANT_BYTES = 4;
    /**
     * Max number of most significant bytes a hash prefix can be made out of a full-length hash.
     */
    public static final int MAX_SIGNIFICANT_BYTES = 32;

    /**
     * Computes hash prefix for the expression that includes supplied number of most significant bytes.
     *
     * @param expression expression to compute hash prefix for
     * @param significantBytes significant bytes to make prefix
     * @return hash prefix encoded in hex
     */
    public String computeHashPrefix(String expression, int significantBytes) {
        checkSignificantBytes(significantBytes);

        int hexDigitsLength = significantBytes * 2;
        return DigestUtils.sha256Hex(expression).substring(0, hexDigitsLength);
    }

    /**
     * Computed full hash for the expression.
     *
     * @param expression expression to compute full hash for
     * @return full hash encoded in hex
     */
    public String computeFullHash(String expression) {
        return DigestUtils.sha256Hex(expression);
    }

    private void checkSignificantBytes(int significantBytes) {
        if (significantBytes < MIN_SIGNIFICANT_BYTES || significantBytes > MAX_SIGNIFICANT_BYTES) {
            String msg = String.format("Significant bytes of a hash prefix shall be between %d and %d bytes",
                MIN_SIGNIFICANT_BYTES, MAX_SIGNIFICANT_BYTES);
            throw new IllegalArgumentException(msg);
        }
    }

}

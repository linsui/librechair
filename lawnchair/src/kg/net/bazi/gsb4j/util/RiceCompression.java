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

package kg.net.bazi.gsb4j.util;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Rice compression handler.
 *
 * @author azilet
 */
public class RiceCompression {

    public static final int BYTE_BITS_SIZE = 8;

    /**
     * Decompresses Rice coded bytes to integers. Note that bytes shall represent 4-byte values. That is the reason
     * decompressed values are returned as integers.
     *
     * @param riceParameter Rice parameter
     * @param data compressed bytes
     * @return decompressed data as integers
     */
    public List<Integer> decompress(int riceParameter, byte[] data) {
        BitSet bits = new BitSet(data.length * 8);

        for (int i = 0; i < data.length; i++) {
            BitSet bitsOfByte = BitSet.valueOf(makeArrayOfByte(data[i]));
            for (int j = 0; j < bitsOfByte.size(); j++) {
                bits.set(i * BYTE_BITS_SIZE + j, bitsOfByte.get(j));
            }
        }

        List<Integer> result = new LinkedList<>();
        for (int n = 0; n < bits.size();) {
            int quotient = 0;
            while (bits.get(n++)) {
                quotient++;
            }

            int remainder = 0;
            BitSet range = bits.get(n, n + riceParameter);
            for (int i = 0; i < range.size(); i++) {
                if (range.get(i)) {
                    remainder += (1 << range.size() - i - 1);
                }
            }
            n += riceParameter;

            int value = (quotient << riceParameter) + remainder;
            result.add(value);
        }

        // remove last elements that are zero
        for (int i = result.size() - 1; i >= 0; --i) {
            if (result.get(i) == 0) {
                result.remove(i);
            }
        }
        return result;
    }

    private byte[] makeArrayOfByte(byte byteVal) {
        return new byte[] {
            byteVal
        };
    }

}

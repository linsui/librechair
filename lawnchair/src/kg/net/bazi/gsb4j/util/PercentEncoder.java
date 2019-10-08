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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Simple percent encoding class. This percent encoding is basically a URL encoding that is performed by
 * {@link URLEncoder} with the exception of space character. Spaces are represented using "+" in
 * "application/x-www-form-urlencoded" specs, whereas this class represents spaces using "%20".
 *
 * @author azilet
 */
public class PercentEncoder {

    /**
     * Encodes supplied string in percent encoding.
     * <p>
     * NOTE: hexadecimal digits are in upper case
     *
     * @param str string to encode
     * @return encoded string
     */
    public String encode(String str) {
        try {
            String urlEncoded = URLEncoder.encode(str, StandardCharsets.UTF_8.name());
            return urlEncoded.replace("+", "%20");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Shall not happen", ex);
        }
    }

    /**
     * Decodes percent encoded string.
     *
     * @param str string to decode
     * @return decoded string
     */
    public String decode(String str) {
        try {
            return URLDecoder.decode(str, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Shall not happen", ex);
        }
    }

}

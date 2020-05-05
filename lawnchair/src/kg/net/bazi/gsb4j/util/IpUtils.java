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

import com.google.common.net.InetAddresses;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP address utility class.
 *
 * @author azilet
 */
public class IpUtils {

    /**
     * IP address pattern encoded in decimal.
     */
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");

    /**
     * IP address pattern encoded in binary.
     */
    private static final Pattern BIN_PATTERN = Pattern.compile("[01]{32}");

    /**
     * IP address pattern encoded in octal.
     */
    private static final Pattern OCTAL_PATTERN = Pattern.compile(
        "([0-7]{1,4})\\.([0-7]{1,4})\\.([0-7]{1,4})\\.([0-7]{1,4})");

    /**
     * IP address pattern encoded in hex.
     */
    private static final Pattern HEX_PATTERN = Pattern.compile(
        "0x([0-9a-f]{1,2})\\.0x([0-9a-f]{1,2})\\.0x([0-9a-f]{1,2})\\.0x([0-9a-f]{1,2})", Pattern.CASE_INSENSITIVE);

    /**
     * Checks if supplied string is in a valid IP address format encoded in either decimal, binary, octal, or
     * hexadecimal. Strings for which this method returns {@code true} are not guaranteed to represent valid IP
     * addresses.
     *
     * @param ip IP address string to check
     * @return {@code true} if supplied string can be parsed as IP address; {@code false} otherwise
     */
    public boolean isIpAddress(String ip) {
        return isDecimalIpAddress(ip)
            || isBinaryIpAddress(ip)
            || isOctalEncodedIpAddress(ip)
            || isHexEncodedIpAddress(ip)
            || isNumericIpAddress(ip);
    }

    /**
     * Checks if supplied string is in a valid IP address format encoded in decimal. Strings for which this method
     * returns {@code true} are not guaranteed to represent valid IP addresses.
     *
     * @param ip IP address string to check
     * @return {@code true} if supplied string can be parsed as IP address; {@code false} otherwise
     */
    public boolean isDecimalIpAddress(String ip) {
        return DECIMAL_PATTERN.matcher(ip).matches();
    }

    /**
     * Checks if supplied string is in a valid IP address format encoded in binary. Strings for which this method
     * returns {@code true} are not guaranteed to represent valid IP addresses.
     *
     * @param ip IP address string to check
     * @return {@code true} if supplied string can be parsed as IP address; {@code false} otherwise
     */
    public boolean isBinaryIpAddress(String ip) {
        return BIN_PATTERN.matcher(ip).matches();
    }

    /**
     * Checks if supplied string is in a valid IP address format encoded in octal. Strings for which this method returns
     * {@code true} are not guaranteed to represent valid IP addresses.
     *
     * @param ip IP address string to check
     * @return {@code true} if supplied string can be parsed as IP address; {@code false} otherwise
     */
    public boolean isOctalEncodedIpAddress(String ip) {
        return OCTAL_PATTERN.matcher(ip).matches();
    }

    /**
     * Checks if supplied string is in a valid IP address format encoded in hex. Strings for which this method returns
     * {@code true} are not guaranteed to represent valid IP addresses.
     *
     * @param ip IP address string to check
     * @return {@code true} if supplied string can be parsed as IP address; {@code false} otherwise
     */
    public boolean isHexEncodedIpAddress(String ip) {
        return HEX_PATTERN.matcher(ip).matches();
    }

    /**
     * Checks if supplied string is a numeric representation of an IP address.
     *
     * @param ip IP address to check
     * @return {@code true} if supplied string can be parsed as IP address; {@code false} otherwise
     */
    public boolean isNumericIpAddress(String ip) {
        try {
            Integer.parseUnsignedInt(ip);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Parses and converts supplied string to IP address instance. This method returns an IP address instance only if
     * {@link IpUtils#isIpAddress(String)} returns {@code true}.
     *
     * @param ip IP address string to convert
     * @return IP address instance if successfully parsed; {@code null} otherwise
     */
    public InetAddress toInetAddress(String ip) {
        if (isDecimalIpAddress(ip)) {
            return InetAddresses.forString(ip);
        }

        Matcher matcherBin = BIN_PATTERN.matcher(ip);
        if (matcherBin.matches()) {
            String bin = matcherBin.group();
            int numericIpValue = Integer.parseUnsignedInt(bin, 2);
            return InetAddresses.fromInteger(numericIpValue);
        }

        Matcher matcherOct = OCTAL_PATTERN.matcher(ip);
        if (matcherOct.matches()) {
            String delim = "";
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= 4; i++) {
                int dec = Integer.parseInt(matcherOct.group(i), 8);
                sb.append(delim).append(dec);
                delim = ".";
            }
            return InetAddresses.forString(sb.toString());
        }

        Matcher matcherHex = HEX_PATTERN.matcher(ip);
        if (matcherHex.matches()) {
            String delim = "";
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= 4; i++) {
                int dec = Integer.parseInt(matcherHex.group(i), 16);
                sb.append(delim).append(dec);
                delim = ".";
            }
            return InetAddresses.forString(sb.toString());
        }

        if (isNumericIpAddress(ip)) {
            return InetAddresses.fromInteger(Integer.parseUnsignedInt(ip));
        }

        return null;
    }

}

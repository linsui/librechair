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

import com.google.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kg.net.bazi.gsb4j.util.IpUtils;
import kg.net.bazi.gsb4j.util.UrlSplitter;

/**
 * This class generates suffix/prefix expressions for URLs.
 *
 * @author azilet
 */
public class SuffixPrefixExpressions {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuffixPrefixExpressions.class);

    /**
     * Max number of host name components to use to generate host name suffixes.
     */
    static final int MAX_HOST_COMPONENTS = 5;

    /**
     * Max number of path components to use to generate path prefixes.
     */
    static final int MAX_PATH_COMPONENTS = 4;

    @Inject
    UrlSplitter urlSplitter;

    @Inject
    IpUtils ipUtils;

    /**
     * Generates suffix/prefix expressions for the supplied URL string.
     *
     * @param url URL to generate expressions for
     * @return set of suffix/prefix expressions; empty set if expressions could not be generated
     */
    public Set<String> makeExpressions(String url) {
        UrlSplitter.UrlParts parts = urlSplitter.split(url);
        if (parts == null) {
            LOGGER.info("URL cannot be split into parts: {}", url);
            return Collections.emptySet();
        }

        Set<String> suffixExprSet = extractSuffixExpressions(parts.getHost());
        Set<String> prefixExprSet = extractPrefixExpressions(parts);

        Set<String> result = new HashSet<>();
        for (String hostSuffix : suffixExprSet) {
            for (String pathPrefix : prefixExprSet) {
                result.add(hostSuffix + pathPrefix);
            }
        }
        return result;
    }

    private Set<String> extractSuffixExpressions(String host) {
        Set<String> set = new HashSet<>();
        set.add(host);

        if (!ipUtils.isIpAddress(host)) {
            String[] arr = host.split("\\.");
            int startIndex = arr.length > MAX_HOST_COMPONENTS ? arr.length - MAX_HOST_COMPONENTS : 0;
            // the top-level domain can be skipped -- see the upper bound
            for (int i = startIndex; i < arr.length - 1; i++) {
                String additionalHost = Stream.of(arr).skip(i).collect(Collectors.joining("."));
                set.add(additionalHost);
            }
        }
        return set;
    }

    private Set<String> extractPrefixExpressions(UrlSplitter.UrlParts parts) {
        Set<String> set = new HashSet<>();
        set.add(parts.getPath());
        if (parts.getQuery() != null) {
            set.add(parts.getPath() + parts.getQuery());
        }

        // (1) path string starts with "/" and thus split will return empty string as first item
        // this makes path prefixes start with "/" as we always have empty string as first item
        // (2) we request trailing empty strings -- those are important
        int slashMatchCount = StringUtils.countMatches(parts.getPath(), '/');
        String[] arr = parts.getPath().split("/", slashMatchCount + 1);

        int upperBound = Math.min(arr.length, MAX_PATH_COMPONENTS);
        for (int i = 0; i < upperBound; i++) {
            String pathPrefix = Stream.of(arr).limit(i + 1).collect(Collectors.joining("/"));
            // trailing slash shall be included
            if (i != upperBound - 1) {
                pathPrefix += "/";
            }
            set.add(pathPrefix);
        }
        return set;
    }

}

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

package kg.net.bazi.gsb4j.data;

import java.util.EnumSet;
import java.util.Set;

/**
 * Types of threats.
 *
 * @author azilet
 */
public enum ThreatType {
    THREAT_TYPE_UNSPECIFIED,
    MALWARE,
    SOCIAL_ENGINEERING,
    UNWANTED_SOFTWARE,
    POTENTIALLY_HARMFUL_APPLICATION;

    /**
     * Gets all known threat types.
     *
     * @return set of known threat types
     */
    public static Set<ThreatType> getAll() {
        Set<ThreatType> all = EnumSet.allOf(ThreatType.class);
        all.remove(THREAT_TYPE_UNSPECIFIED);
        return all;
    }
}

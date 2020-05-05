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

/**
 * Types of entries that pose threats. Threat lists are collections of entries of a single type.
 *
 * @author azilet
 */
public enum ThreatEntryType {
    THREAT_ENTRY_TYPE_UNSPECIFIED,
    URL,
    EXECUTABLE,
    IP_RANGE,
}

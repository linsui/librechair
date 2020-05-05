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

package kg.net.bazi.gsb4j.cache;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import kg.net.bazi.gsb4j.api.ThreatListGetter;
import kg.net.bazi.gsb4j.data.ThreatListDescriptor;

/**
 * Holds currently available threat list descriptors. Updated on every update request to API.
 *
 * @author azilet
 */
@Singleton
public class ThreatListDescriptorsCache {

    @Inject
    private Provider<ThreatListGetter> threatListGetterProvider;

    private final Set<ThreatListDescriptor> cache = new HashSet<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Gets cached threat list descriptors.
     *
     * @return descriptors collection
     */
    public Collection<ThreatListDescriptor> get() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            if (!cache.isEmpty()) {
                return Collections.unmodifiableCollection(cache);
            }
        } finally {
            readLock.unlock();
        }
        return getRefreshed();
    }

    /**
     * Gets threat list descriptors first refreshing the cache.
     *
     * @return descriptors
     */
    public Collection<ThreatListDescriptor> getRefreshed() {
        ThreatListGetter threatListGetter = threatListGetterProvider.get();
        List<ThreatListDescriptor> ls = threatListGetter.getLists();

        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            cache.clear();
            cache.addAll(ls);
        } finally {
            writeLock.unlock();
        }
        return Collections.unmodifiableCollection(cache);
    }

}

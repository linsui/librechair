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

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Guice module to initialize API specific bindings.
 * <p>
 * This module is not supposed to be used directly! To bootstrap, consider bootstrap methods in
 * {@link kg.net.bazi.gsb4j.Gsb4j} or methods that return list of all necessary modules.
 *
 * @author azilet
 */
public class SafeBrowsingApiModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ThreatListUpdateRunner.class).asEagerSingleton();

        bind(SafeBrowsingApi.class).annotatedWith(Names.named(SafeBrowsingApi.Type.LOOKUP_API)).to(LookupApi.class);
        bind(SafeBrowsingApi.class).annotatedWith(Names.named(SafeBrowsingApi.Type.UPDATE_API)).to(UpdateApi.class);
        bind(SafeBrowsingApi.class).to(UpdateApi.class);
    }
}

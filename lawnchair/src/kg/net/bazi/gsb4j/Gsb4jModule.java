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

package kg.net.bazi.gsb4j;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Guice module to initialize common bindings used in Gsb4j.
 * <p>
 * This module is not supposed to be used directly! To bootstrap, consider bootstrap methods in {@link Gsb4j} or methods
 * that return list of all necessary modules.
 *
 * @author azilet
 */
public class Gsb4jModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CloseableHttpClient.class)
            .annotatedWith(Gsb4jBinding.class)
            .toProvider(HttpClientProvider.class)
            .asEagerSingleton();
    }

    @Provides
    @Gsb4jBinding
    @Singleton
    Gson makeGson(EnumTypeAdapterFactory factory) {
        return new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .registerTypeAdapterFactory(factory)
            .create();
    }

    @Provides
    @Gsb4jBinding
    @Singleton
    ScheduledExecutorService makeScheduler() {
        Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        ThreadFactory tf = new BasicThreadFactory.Builder()
            .namingPattern("gsb4j-pool-%d")
            .uncaughtExceptionHandler((thread, ex) -> logger.error("Thread {} failed", thread.getName(), ex))
            .build();
        return Executors.newScheduledThreadPool(4, tf);
    }

}

/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package kg.net.bazi.gsb4j;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.name.Names;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;

import javax.sql.DataSource;

import kg.net.bazi.gsb4j.api.SafeBrowsingApi;
import kg.net.bazi.gsb4j.api.SafeBrowsingApiModule;
import kg.net.bazi.gsb4j.db.LocalDatabaseModule;
import kg.net.bazi.gsb4j.properties.Gsb4jClientInfoProvider;
import kg.net.bazi.gsb4j.properties.Gsb4jProperties;
import kg.net.bazi.gsb4j.properties.Gsb4jPropertiesModule;

/**
 * Main entry point to GSB4J application. Besides being a facade class, this class is a place for application wide
 * constant values and utility methods, {@link #bootstrap()} being the most important one.
 *
 * @author <a href="https://github.com/bazi">bazi</a>
 */
public class Gsb4j {

    /**
     * Constant string of "gsb4j". Previously primarily used to name Gsb4j specific Guice bindings which later replaces
     * by {@link Gsb4jBinding} annotation.
     */
    public static final String GSB4J = "gsb4j";

    /**
     * Base URL for Google Safe Browsing API. Note that API requests have their own paths to be resolved relative to
     * this base URL. This URL has a trailing slash.
     */
    public static final String API_BASE_URL = "https://safebrowsing.googleapis.com/v4/";

    private static final Logger LOGGER = LoggerFactory.getLogger(Gsb4j.class);

    @Inject
    private Injector injector;

    Gsb4j() {
        // not to be constructed by users
    }

    /**
     * Bootstraps Gsb4j and returns an instance of {@link Gsb4j} which is used to get API implementations. When using
     * this method, configuration parameters are expected to be supplied through system parameters.
     *
     * @return {@link Gsb4j} instance
     * @see #bootstrap(Properties)
     */
    public static Gsb4j bootstrap() {
        List<Module> modules = new ArrayList<>();
        modules.add(new Gsb4jModule());
        modules.add(new LocalDatabaseModule());
        modules.add(new SafeBrowsingApiModule());

        Injector injector = Guice.createInjector(Stage.PRODUCTION, modules);
        dumpGsb4jInfo(injector);
        return injector.getInstance(Gsb4j.class);
    }

    /**
     * Bootstraps Gsb4j and returns an instance of {@link Gsb4j} which is used to get API implementations.
     *
     * @param properties properties to be used as a source of configuration
     * @return {@link Gsb4j} instance
     * @see #bootstrap()
     */
    public static Gsb4j bootstrap(Properties properties) {
        List<Module> modules = new ArrayList<>();
        modules.add(new Gsb4jModule());
        modules.add(new LocalDatabaseModule());
        modules.add(new SafeBrowsingApiModule());
        modules.add(new Gsb4jPropertiesModule(properties));

        Injector injector = Guice.createInjector(Stage.PRODUCTION, modules);
        dumpGsb4jInfo(injector);
        return injector.getInstance(Gsb4j.class);
    }

    /**
     * Parses and converts duration strings from API to milliseconds. API returns durations in seconds with up to nine
     * fractional digits, terminated by 's' like "593.44s".
     *
     * @param duration duration string to parse
     * @return duration in milliseconds
     */
    public static long durationToMillis(String duration) {
        double seconds = !duration.isEmpty()
            ? Double.parseDouble(duration.substring(0, duration.length() - 1))
            : 0;
        return Math.round(seconds * 1000);
    }

    /**
     * Gets {@link Injector} instance to be used for cases when client users want to create their own child injectors
     * with their additional modules.
     *
     * @return injector instance
     */
    public Injector getInjector() {
        return injector;
    }

    /**
     * Gets Safe Browsing API client implementation instance.
     *
     * @param name name of the API implementation type, implementation names are defined as constants in
     * {@link SafeBrowsingApi}
     * @return API implementation instance
     */
    public SafeBrowsingApi getApiClient(String name) {
        List<String> validNames = Arrays.asList(SafeBrowsingApi.Type.LOOKUP_API, SafeBrowsingApi.Type.UPDATE_API);
        if (!validNames.contains(name)) {
            String names = String.join(", ", validNames);
            throw new IllegalArgumentException("Invalid API impl client name: " + name + ". Valid names: " + names);
        }

        Key<SafeBrowsingApi> key = Key.get(SafeBrowsingApi.class, Names.named(name));
        return injector.getInstance(key);
    }

    /**
     * Shuts down Gsb4j. This method releases all resources related to Gsb4j.
     */
    public void shutdown() {
        // stop all scheduled tasks
        Key<ScheduledExecutorService> scedulerKey = Key.get(ScheduledExecutorService.class, Gsb4jBinding.class);
        ScheduledExecutorService scheduler = injector.getInstance(scedulerKey);
        scheduler.shutdown();

        // cleanup HTTP client resources
        Key<CloseableHttpClient> httpClientKey = Key.get(CloseableHttpClient.class, Gsb4jBinding.class);
        CloseableHttpClient httpClient = injector.getInstance(httpClientKey);
        close(httpClient, "HTTP client");

        // close db connections
        Key<DataSource> dataSourceKey = Key.get(DataSource.class, Gsb4jBinding.class);
        DataSource dataSource = injector.getInstance(dataSourceKey);
        if (dataSource instanceof Closeable) {
            close((Closeable) dataSource, "DB pool");
        }
    }

    private void close(Closeable closeable, String objectType) {
        LOGGER.info("Closing {}", objectType);
        try {
            closeable.close();
        } catch (IOException ex) {
            LOGGER.error("Failed to close {}: {}", objectType, ex.getMessage());
        }
    }

    private static void dumpGsb4jInfo(Injector injector) {
        Gsb4jClientInfoProvider clientInfoProvider = injector.getInstance(Gsb4jClientInfoProvider.class);
        Gsb4jProperties properties = injector.getInstance(Gsb4jProperties.class);
        String httpReferrer = properties.getApiHttpReferrer() != null ? properties.getApiHttpReferrer() : "-";

        LOGGER.info("================== Gsb4j setup info ===================");
        LOGGER.info("Client ID     : {}", clientInfoProvider.getClientId());
        LOGGER.info("Client version: {}", clientInfoProvider.getClientVersion());
        LOGGER.info("Data directory: {}", properties.getDataDirectory());
        LOGGER.info("API key       : {}", maskApiKey(properties.getApiKey()));
        LOGGER.info("HTTP Referrer : {}", httpReferrer);
        LOGGER.info("=======================================================");
    }

    private static String maskApiKey(String apiKey) {
        int length = apiKey.length();
        StringBuilder sb = new StringBuilder();
        sb.append(apiKey.substring(0, 4));
        sb.append(StringUtils.repeat("*", length - 8));
        sb.append(apiKey.substring(length - 4, length));
        return sb.toString();
    }

}

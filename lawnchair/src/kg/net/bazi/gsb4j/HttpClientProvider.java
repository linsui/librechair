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

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import kg.net.bazi.gsb4j.properties.Gsb4jProperties;

/**
 * Provider of HTTP client to be used for making requests to Safe Browsing API.
 *
 * @author azilet
 */
class HttpClientProvider implements Provider<CloseableHttpClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientProvider.class);

    @Inject
    Gsb4jProperties properties;

    @Override
    public CloseableHttpClient get() {
        PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager();
        pcm.setMaxTotal(50);
        pcm.setDefaultMaxPerRoute(5);

        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(15_000)
            .build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .disableCookieManagement()
            .addInterceptorFirst(new GzipCompressionRequestInterceptor())
            .addInterceptorFirst(new GzipCompressionResponseInterceptor())
            .setConnectionManager(pcm);

        String referer = properties.getApiHttpReferrer();
        if (referer != null && !referer.isEmpty()) {
            httpClientBuilder.addInterceptorLast(makeRequestRefererInterceptor(referer));
        }
        if (LOGGER.isDebugEnabled()) {
            httpClientBuilder.addInterceptorFirst(makeRequestPayloadLogger());
            httpClientBuilder.addInterceptorFirst(makeResponsePayloadLogger());
        }

        return httpClientBuilder.build();
    }

    private HttpRequestInterceptor makeRequestRefererInterceptor(String referer) {
        return new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                request.addHeader(HttpHeaders.REFERER, referer);
            }
        };
    }

    private HttpRequestInterceptor makeRequestPayloadLogger() {
        return new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                LOGGER.debug("===> {}", request.getRequestLine());
            }
        };
    }

    private HttpResponseInterceptor makeResponsePayloadLogger() {
        return new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
                LOGGER.debug("<=== {}", response.getStatusLine());
            }
        };
    }

    /**
     * HTTP request interceptor that adds headers to requests indicating responses can be compressed using gzip.
     *
     * @author azilet
     */
    static class GzipCompressionRequestInterceptor implements HttpRequestInterceptor {

        @Override
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            if (!request.containsHeader(HttpHeaders.ACCEPT_ENCODING)) {
                request.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip,deflate");
            }
        }
    }

    /**
     * HTTP response interceptor that makes possible to read compressed responses in gzip, deflate, etc.
     *
     * @author azilet
     */
    static class GzipCompressionResponseInterceptor implements HttpResponseInterceptor {

        @Override
        public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
            HttpEntity entity = response.getEntity();
            // immediately return for responses that have no body, e.g. response to HEAD requests
            if (entity == null) {
                return;
            }
            Header header = entity.getContentEncoding();
            if (header != null) {
                for (HeaderElement he : header.getElements()) {
                    if (he.getName().equalsIgnoreCase("gzip")) {
                        response.setEntity(new GzipDecompressingEntity(entity));
                        return;
                    }
                    if (he.getName().equalsIgnoreCase("deflate")) {
                        response.setEntity(new DeflateDecompressingEntity(entity));
                        return;
                    }
                }
            }
        }
    }

}

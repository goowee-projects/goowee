/*
 * Copyright 2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package goowee.commons.http

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.apache.hc.client5.http.classic.methods.*
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy
import org.apache.hc.client5.http.ssl.HostnameVerificationPolicy
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier
import org.apache.hc.client5.http.ssl.TrustAllStrategy
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import org.apache.hc.core5.ssl.SSLContextBuilder
import org.apache.hc.core5.util.Timeout

import javax.net.ssl.SSLContext

/**
 * Utility class for executing HTTP requests using Apache HttpClient 5.
 * We use Apache HTTP Client since it is compatible with SAP Cloud SDK
 * (https://sap.github.io/cloud-sdk/).
 *
 * <p>This class provides static methods to perform standard REST operations:
 * GET, POST, PUT, PATCH, DELETE. It supports JSON payloads and responses,
 * and allows setting custom headers such as authorization tokens.</p>
 *
 * <p>Typical usage:
 * <pre>{@code
 * def client = HttpClient.create(30)
 * def headers = ['Authorization': 'Bearer myToken']
 * def response = HttpClient.get(client, "https://api.example.com/data", headers)
 *}</pre>
 * </p>
 *
 * <p>Features:
 * <ul>
 *   <li>Configurable timeouts (connection request and response) via `create()`</li>
 *   <li>Automatic JSON serialization/deserialization for request/response bodies</li>
 *   <li>Support for custom headers including Authorization tokens</li>
 *   <li>Exception handling for non-2xx HTTP responses</li>
 * </ul>
 * </p>
 *
 * <p>This class is designed for general-purpose REST API interactions
 * and is not tied to any specific backend or service.</p>
 *
 * @author Gianluca Sartori
 */
@CompileStatic
class HttpClient {

    /**
     * Creates a reusable CloseableHttpClient with configurable timeouts.
     *
     * @param timeoutSeconds Timeout in seconds for connection request and response
     * @return CloseableHttpClient ready for use
     */
    static CloseableHttpClient create(Integer timeoutSeconds = 30, Boolean forceCertificateVerification = false) {
        return forceCertificateVerification
                ? createValidCertHttpClient(timeoutSeconds)
                : createNoCertHttpClient(timeoutSeconds)

    }

    private static CloseableHttpClient createValidCertHttpClient(Integer timeoutSeconds) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(timeoutSeconds))
                .setResponseTimeout(Timeout.ofSeconds(timeoutSeconds))
                .build()

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()
    }

    private static CloseableHttpClient createNoCertHttpClient(Integer timeoutSeconds) {
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                .build()

        DefaultClientTlsStrategy tlsStrategy = new DefaultClientTlsStrategy(
                sslContext,
                HostnameVerificationPolicy.BOTH,
                NoopHostnameVerifier.INSTANCE
        )

        PoolingHttpClientConnectionManager connManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(tlsStrategy)
                .build()

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(timeoutSeconds))
                .setResponseTimeout(Timeout.ofSeconds(timeoutSeconds))
                .build()

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connManager)
                .build()

        return httpClient
    }

    /**
     * Executes an HTTP GET request and returns the response as a String.
     *
     * @param client HttpClient instance
     * @param uri URI of the request
     * @param headers Optional map of headers
     * @return The response body as String
     * @throws Exception if the HTTP response status is not 2xx
     */
    static String get(CloseableHttpClient client, String uri, Map<String, String> headers = [:]) {
        return callAsString(client, new HttpGet(uri), headers)
    }

    /**
     * Executes an HTTP DELETE request and returns the response as a String.
     *
     * @param client HttpClient instance
     * @param uri URI of the request
     * @param headers Optional map of headers
     * @return The response body as String
     * @throws Exception if the HTTP response status is not 2xx
     */
    static String delete(CloseableHttpClient client, String uri, Map<String, String> headers = [:]) {
        return callAsString(client, new HttpDelete(uri), headers)
    }

    /**
     * Executes an HTTP POST request with a String body and returns the response as a String.
     *
     * @param client HttpClient instance
     * @param uri URI of the request
     * @param body String representing the JSON body
     * @param headers Optional map of headers
     * @return The response body as String
     * @throws Exception if the HTTP response status is not 2xx
     */
    static String post(CloseableHttpClient client, String uri, String body, Map<String, String> headers = [:]) {
        HttpPost request = new HttpPost(uri)
        request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON))
        return callAsString(client, request, headers)
    }

    /**
     * Executes an HTTP POST request with a JSON body and returns the response as a String.
     *
     * @param client HttpClient instance
     * @param uri URI of the request
     * @param body Map representing the JSON body
     * @param headers Optional map of headers
     * @return The response body as String
     * @throws Exception if the HTTP response status is not 2xx
     */
    static String post(CloseableHttpClient client, String uri, Map body = [:], Map<String, String> headers = [:]) {
        String jsonBody = JsonOutput.toJson(body)
        return post(client, uri, jsonBody, headers)
    }

    /**
     * Executes an HTTP PATCH request with a String body and returns the response as a String.
     *
     * @param client HttpClient instance
     * @param uri URI of the request
     * @param body String representing the JSON body
     * @param headers Optional map of headers
     * @return The response body as String
     * @throws Exception if the HTTP response status is not 2xx
     */
    static String patch(CloseableHttpClient client, String uri, String body, Map<String, String> headers = [:]) {
        HttpPatch request = new HttpPatch(uri)
        request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON))
        return callAsString(client, request, headers)
    }

    /**
     * Executes an HTTP PATCH request with a JSON body and returns the response as a String.
     *
     * @param client HttpClient instance
     * @param uri URI of the request
     * @param body Map representing the JSON body
     * @param headers Optional map of headers
     * @return The response body as String
     * @throws Exception if the HTTP response status is not 2xx
     */
    static String patch(CloseableHttpClient client, String uri, Map body = [:], Map<String, String> headers = [:]) {
        String jsonBody = JsonOutput.toJson(body)
        return patch(client, uri, jsonBody, headers)
    }

/**
 * Executes an HTTP PUT request with a JSON body and returns the response as a String.
 *
 * @param client HttpClient instance
 * @param uri URI of the request
 * @param body Map representing the JSON body
 * @param headers Optional map of headers
 * @return The response body as String
 * @throws Exception if the HTTP response status is not 2xx
 */
    static String put(CloseableHttpClient client, String uri, Map body = [:], Map<String, String> headers = [:]) {
        HttpPut request = new HttpPut(uri)
        request.setEntity(new StringEntity(JsonOutput.toJson(body), ContentType.APPLICATION_JSON))
        return callAsString(client, request, headers)
    }

    /**
     * Parses a JSON string into a Map.
     * <p>
     * Useful for converting the string response of HTTP calls into structured data.
     * </p>
     *
     * @param json The JSON string to parse
     * @return A Map representing the JSON content; empty map if input is null or invalid
     */
    static Map jsonToMap(String json) {
        if (!json) {
            return [:]
        }

        try {
            return new JsonSlurper().parseText(json) as Map
        } catch (Exception ignore) {
            return [:]
        }
    }

    /**
     * Converts a Map into a JSON string.
     * <p>
     * Useful for preparing request bodies for HTTP POST, PUT, or PATCH calls.
     * Automatically returns "{}" if the input is null or invalid.
     * </p>
     *
     * @param map The Map to convert to JSON
     * @return A JSON string representation of the Map; "{}" if input is null or conversion fails
     */
    static String mapToJson(Map map) {
        if (!map) {
            return "{}"
        }

        try {
            return JsonOutput.toJson(map)
        } catch (Exception ignore) {
            return "{}"
        }
    }

    /**
     * Executes an HTTP GET request and returns the raw response body as a byte array.
     * <p>
     * This method is typically used for downloading binary content (e.g., PDFs, images, files).
     * The "Accept" header is automatically set to "*\*" if not already provided.
     * </p>
     *
     * @param client the {@link CloseableHttpClient} instance to use for executing the request
     * @param uri the target URI to call (absolute URL)
     * @param headers optional map of HTTP headers to include in the request (e.g., Authorization)
     * @return a byte array containing the raw response body
     * @throws Exception if the HTTP response status code is not 2xx
     */
    static byte[] getBytes(CloseableHttpClient client, String uri, Map<String, String> headers = [:]) {
        HttpGet request = new HttpGet(uri)
        if (!headers.containsKey("Accept")) headers["Accept"] = "*/*"
        return callAsByteArray(client, request, headers)
    }

    /**
     * Executes an HTTP request and returns the response body as a String.
     * <p>
     * This method is used internally by higher-level HTTP methods (GET, POST, PUT, PATCH, DELETE)
     * to handle requests expecting textual or JSON responses. It automatically applies default
     * "Content-Type" and "Accept" headers for JSON if not already present.
     * </p>
     *
     * @param client the {@link CloseableHttpClient} used to perform the HTTP request
     * @param request the HTTP request to execute (e.g., {@link HttpGet}, {@link HttpPost})
     * @param headers optional map of additional HTTP headers
     * @return the response body as a String; an empty string if the response has no body
     * @throws Exception if the HTTP status code is not in the 2xx range
     */
    private static String callAsString(CloseableHttpClient client, HttpUriRequestBase request, Map<String, String> headers = [:]) {
        headers.each { k, v -> request.setHeader(k, v) }

        if (!headers.containsKey("Content-Type")) request.setHeader("Content-Type", "application/json")
        if (!headers.containsKey("Accept")) request.setHeader("Accept", "application/json")

        client.execute(request) { ClassicHttpResponse response ->
            int status = response.code
            String bodyText = response.entity ? EntityUtils.toString(response.entity) : ""

            if (status >= 200 && status < 300) {
                return bodyText
            } else {
                throw new Exception("REST API Error: HTTP ${status} - ${bodyText}")
            }
        }
    }

    /**
     * Executes an HTTP request and returns the raw response body as a byte array.
     * <p>
     * This method is suitable for binary responses such as file downloads.
     * It automatically applies a generic "Accept: *\*" header if not provided.
     * In case of an error (non-2xx status code), the method attempts to decode
     * the response body as text for debugging and includes it in the exception message.
     * </p>
     *
     * @param client the {@link CloseableHttpClient} used to perform the HTTP request
     * @param request the HTTP request to execute (e.g., {@link HttpGet}, {@link HttpPost})
     * @param headers optional map of additional HTTP headers
     * @return a byte array containing the raw response body
     * @throws Exception if the HTTP status code is not in the 2xx range
     */
    private static byte[] callAsByteArray(CloseableHttpClient client, HttpUriRequestBase request, Map<String, String> headers = [:]) {
        headers.each { k, v -> request.setHeader(k, v) }

        if (!headers.containsKey("Accept")) request.setHeader("Accept", "*/*")

        client.execute(request) { ClassicHttpResponse response ->
            int status = response.code
            byte[] bytes = response.entity ? EntityUtils.toByteArray(response.entity) : new byte[0]

            if (status >= 200 && status < 300) {
                return bytes
            } else {
                String errorText = ""
                try {
                    if (response.entity)
                        errorText = new String(bytes)
                } catch (ignored) {
                }
                throw new Exception("REST API Error: HTTP ${status} - ${errorText}")
            }
        }
    }

}

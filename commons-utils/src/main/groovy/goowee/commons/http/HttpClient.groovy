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
import org.apache.hc.client5.http.classic.methods.HttpDelete
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpPatch
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.classic.methods.HttpPut
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.StringEntity
import org.apache.hc.core5.util.Timeout

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
 * }</pre>
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
 */
class HttpClient {

    /**
     * Creates a reusable CloseableHttpClient with configurable timeouts.
     *
     * @param timeoutSeconds Timeout in seconds for connection request and response
     * @return CloseableHttpClient ready for use
     */
    static CloseableHttpClient create(int timeoutSeconds = 30) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(timeoutSeconds))
                .setResponseTimeout(Timeout.ofSeconds(timeoutSeconds))
                .build()

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()
    }

    /**
     * Executes an HTTP GET request.
     *
     * @param client HttpClient instance
     * @param uri URI of the request
     * @param headers Optional map of request headers
     * @return Map representing the JSON response body
     * @throws Exception if the HTTP response status is not 2xx
     */
    static Map get(CloseableHttpClient client, String uri, Map<String, String> headers = [:]) {
        call(client, new HttpGet(uri), headers)
    }

    /**
     * Executes an HTTP DELETE request.
     *
     * @param client HttpClient instance
     * @param uri URI of the request
     * @param headers Optional map of request headers
     * @return Map representing the JSON response body
     * @throws Exception if the HTTP response status is not 2xx
     */
    static Map delete(CloseableHttpClient client, String uri, Map<String, String> headers = [:]) {
        call(client, new HttpDelete(uri), headers)
    }

    /**
     * Executes an HTTP POST request with a JSON body.
     *
     * @param client HttpClient instance
     * @param uri URI of the request
     * @param body Map representing the JSON body
     * @param headers Optional map of request headers
     * @return Map representing the JSON response body
     * @throws Exception if the HTTP response status is not 2xx
     */
    static Map post(CloseableHttpClient client, String uri, Map body = [:], Map<String, String> headers = [:]) {
        HttpPost request = new HttpPost(uri)
        request.setEntity(new StringEntity(JsonOutput.toJson(body), ContentType.APPLICATION_JSON))
        call(client, request, headers)
    }

    /**
     * Executes an HTTP PATCH request with a JSON body.
     *
     * @param client HttpClient instance
     * @param uri URI of the request
     * @param body Map representing the JSON body
     * @param headers Optional map of request headers
     * @return Map representing the JSON response body
     * @throws Exception if the HTTP response status is not 2xx
     */
    static Map patch(CloseableHttpClient client, String uri, Map body = [:], Map<String, String> headers = [:]) {
        HttpPatch request = new HttpPatch(uri)
        request.setEntity(new StringEntity(JsonOutput.toJson(body), ContentType.APPLICATION_JSON))
        call(client, request, headers)
    }

    /**
     * Executes an HTTP PUT request with a JSON body.
     *
     * @param client HttpClient instance
     * @param uri URI of the request
     * @param body Map representing the JSON body
     * @param headers Optional map of request headers
     * @return Map representing the JSON response body
     * @throws Exception if the HTTP response status is not 2xx
     */
    static Map put(CloseableHttpClient client, String uri, Map body = [:], Map<String, String> headers = [:]) {
        HttpPut request = new HttpPut(uri)
        request.setEntity(new StringEntity(JsonOutput.toJson(body), ContentType.APPLICATION_JSON))
        call(client, request, headers)
    }

    /**
     * Internal method to execute the HTTP request and parse the JSON response.
     *
     * @param client HttpClient instance
     * @param request HttpUriRequestBase object (GET, POST, PUT, PATCH, DELETE)
     * @param headers Optional map of request headers
     * @return Map representing the JSON response body
     * @throws Exception if the HTTP response status is not 2xx
     */
    private static Map call(CloseableHttpClient client, HttpUriRequestBase request, Map<String, String> headers = [:]) {
        // Add custom headers
        headers.each { k, v -> request.setHeader(k, v) }

        // Set default headers if not provided
        if (!headers.containsKey("Content-Type")) request.setHeader("Content-Type", "application/json")
        if (!headers.containsKey("Accept")) request.setHeader("Accept", "application/json")

        client.execute(request) { ClassicHttpResponse response ->
            int status = response.code
            String bodyText = response.entity ? org.apache.hc.core5.http.io.entity.EntityUtils.toString(response.entity) : null
            Map body = bodyText ? new JsonSlurper().parseText(bodyText) as Map : [:]

            if (status >= 200 && status < 300) {
                return body
            } else {
                String msg = body?.message ?: "HTTP ${status}"
                throw new Exception("REST API Error: ${msg}")
            }
        }
    }
}

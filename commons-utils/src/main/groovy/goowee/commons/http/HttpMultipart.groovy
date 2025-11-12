package goowee.commons.http

import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.HttpEntity

import java.nio.charset.Charset

/**
 * Builder-style utility for constructing multipart/form-data requests.
 * <p>
 * Provides a fluent API for adding text, JSON, binary or file parts
 * to an HTTP request.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * def multipart = HttpMultipart.create()
 *     .addText("description", "Upload di test")
 *     .addJson("metadata", [id: 42])
 *     .addFile("file", new File("/tmp/test.pdf"))
 *
 * def response = HttpClient.post(client, "https://example.com/api/upload", multipart)
 * }</pre>
 *
 * @author Gianluca Sartori
 */
@CompileStatic
class HttpMultipart {

    private final MultipartEntityBuilder builder

    private HttpMultipart() {
        builder = MultipartEntityBuilder.create()
        builder.setMode(HttpMultipartMode.STRICT)
        builder.setCharset(Charset.forName("UTF-8"))
    }

    /**
     * Creates a new {@link HttpMultipart} builder instance.
     *
     * @return a new {@link HttpMultipart} ready for configuration
     */
    static HttpMultipart create() {
        return new HttpMultipart()
    }

    /**
     * Adds a plain text field to the multipart request.
     *
     * @param name the name of the form field
     * @param value the text value (empty string if null)
     * @return this {@link HttpMultipart} instance for chaining
     */
    HttpMultipart addText(String name, String value) {
        builder.addTextBody(name, value ?: "", ContentType.TEXT_PLAIN.withCharset("UTF-8"))
        return this
    }

    /**
     * Adds a JSON field to the multipart request.
     *
     * @param name the name of the form field
     * @param value the object to serialize as JSON
     * @return this {@link HttpMultipart} instance for chaining
     */
    HttpMultipart addJson(String name, Object value) {
        String json = JsonOutput.toJson(value)
        builder.addTextBody(name, json, ContentType.APPLICATION_JSON.withCharset("UTF-8"))
        return this
    }

    /**
     * Adds a file field to the multipart request.
     *
     * @param name the name of the form field
     * @param file the {@link File} to upload
     * @param contentType optional {@link ContentType} (defaults to binary)
     * @return this {@link HttpMultipart} instance for chaining
     */
    HttpMultipart addFile(String name, File file, ContentType contentType = ContentType.DEFAULT_BINARY) {
        builder.addBinaryBody(name, file, contentType, file.name)
        return this
    }

    /**
     * Adds a binary field from a byte array.
     *
     * @param name the name of the form field
     * @param data the raw bytes to send
     * @param filename the name to assign to the uploaded file
     * @param contentType optional {@link ContentType} (defaults to binary)
     * @return this {@link HttpMultipart} instance for chaining
     */
    HttpMultipart addBytes(String name, byte[] data, String filename, ContentType contentType = ContentType.DEFAULT_BINARY) {
        builder.addBinaryBody(name, data, contentType, filename)
        return this
    }

    /**
     * Builds and returns the Apache {@link HttpEntity} representing this multipart content.
     *
     * @return a fully constructed {@link HttpEntity} ready for an HTTP request
     */
    HttpEntity build() {
        return builder.build()
    }
}

package io.pivotal.gateway.exception;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;

public class ProxyClientResponseException extends RuntimeException {

	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	@Nullable
	private final int rawStatusCode;

	private final String statusText;

	@Nullable
	private final HttpHeaders responseHeaders;

	/**
	 * Construct a new instance of with the given response data.
	 * @param statusCode the raw status code value
	 * @param statusText the status text
	 * @param responseHeaders the response headers (may be {@code null})
	 */
	public ProxyClientResponseException(String message, int statusCode, String statusText,
			@Nullable HttpHeaders responseHeaders) {

		super(message);
		this.rawStatusCode = statusCode;
		this.statusText = statusText;
		this.responseHeaders = responseHeaders;
	}

	/**
	 * Return the raw HTTP status code value.
	 */
	public int getRawStatusCode() {
		return this.rawStatusCode;
	}

	/**
	 * Return the HTTP status text.
	 */
	public String getStatusText() {
		return this.statusText;
	}

	/**
	 * Return the HTTP response headers.
	 */
	@Nullable
	public HttpHeaders getResponseHeaders() {
		return this.responseHeaders;
	}

}

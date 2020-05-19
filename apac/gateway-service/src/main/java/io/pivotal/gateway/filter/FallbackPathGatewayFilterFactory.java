package io.pivotal.gateway.filter;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class FallbackPathGatewayFilterFactory
		extends AbstractGatewayFilterFactory<FallbackPathGatewayFilterFactory.Config> {

	private final Logger logger = LoggerFactory
			.getLogger(FallbackPathGatewayFilterFactory.class);

	public FallbackPathGatewayFilterFactory() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			// Pre-processing
			Set<URI> uris = exchange.getAttributeOrDefault(
					GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());
			String originalUri = (uris.isEmpty())
					? exchange.getRequest().getURI().getRawPath().toString()
					: uris.iterator().next().getRawPath().toString();
			if (config.isRetainRequestPath()) {
				logger.info("Setting original request URL: " + originalUri);
				exchange.getAttributes().computeIfAbsent(config.getName(),
						key -> originalUri);
			}
			if (config.isSuffixPath()) {
				URI uri = exchange
						.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
				String fallbackPath = uri.getRawPath()
						+ (String) exchange.getAttribute(config.getName());
				logger.info(
						"Adding original URL to the forward:/path-suffix" + fallbackPath);
				ServerHttpRequest request = exchange.getRequest().mutate()
						.path(fallbackPath).build();
				exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, request.getURI());
				return chain.filter(exchange.mutate().request(request).build());
			}
			return chain.filter(exchange);
		};
	}

	public static class Config {
		private String name = "originalRequestPath";
		private boolean suffixPath;
		private boolean retainRequestPath;

		boolean isRetainRequestPath() {
			return retainRequestPath;
		}

		void setRetainRequestPath(boolean retainRequestPath) {
			this.retainRequestPath = retainRequestPath;
		}

		String getName() {
			return name;
		}

		void setName(String name) {
			this.name = name;
		}

		boolean isSuffixPath() {
			return suffixPath;
		}

		void setSuffixPath(boolean suffixPath) {
			this.suffixPath = suffixPath;
		}
	}

}

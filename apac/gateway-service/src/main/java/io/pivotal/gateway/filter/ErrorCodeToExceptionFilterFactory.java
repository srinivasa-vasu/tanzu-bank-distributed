package io.pivotal.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import io.pivotal.gateway.exception.ProxyClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class ErrorCodeToExceptionFilterFactory
		extends AbstractGatewayFilterFactory<ErrorCodeToExceptionFilterFactory.Config>
		implements Ordered {

	private final Logger logger = LoggerFactory
			.getLogger(ErrorCodeToExceptionFilterFactory.class);

	public ErrorCodeToExceptionFilterFactory() {
		super(ErrorCodeToExceptionFilterFactory.Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> chain.filter(exchange).then(Mono.defer(() -> {
			ServerHttpResponse response = exchange.getResponse();
			logger.info("Response status code: " + response.getRawStatusCode());
			if (response.getStatusCode() != null && response.getStatusCode().isError()) {
				response.getHeaders().clear();
				return Mono.error(new ProxyClientResponseException(
						response.getStatusCode().getReasonPhrase(),
						response.getStatusCode().value(),
						response.getStatusCode().getReasonPhrase(),
						response.getHeaders()));
			}
			return Mono.empty();
		}));
	}

	@Override
	public String name() {
		return "SMErrorCodeToException";
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	public static class Config {
		// do nothing
	}

}

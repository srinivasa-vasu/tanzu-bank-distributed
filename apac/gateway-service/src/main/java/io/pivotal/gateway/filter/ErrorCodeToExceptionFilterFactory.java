package io.pivotal.gateway.filter;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.pivotal.gateway.exception.ProxyClientResponseException;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.removeAlreadyRouted;

//@Component
//public class ErrorCodeToExceptionGatewayFilter implements GlobalFilter, Ordered {
//
//	private final Logger logger = LoggerFactory
//			.getLogger(ErrorCodeToExceptionGatewayFilter.class);
//
//	@Override
//	public int getOrder() {
//		return Ordered.LOWEST_PRECEDENCE;
//	}
//
//	@Override
//	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//			ServerHttpResponse response = exchange.getResponse();
//			logger.info("Response status code: " + response.getRawStatusCode());
//			if (response.getStatusCode() != null
//					&& !response.getStatusCode().is2xxSuccessful()) {
//				throw new ProxyClientResponseException(
//						response.getStatusCode().getReasonPhrase(),
//						response.getStatusCode().value(),
//						response.getStatusCode().getReasonPhrase(),
//						response.getHeaders());
//			}
//		}));
//	}
//}


//@Component
public class ErrorCodeToExceptionFilterFactory
		extends AbstractGatewayFilterFactory<ErrorCodeToExceptionFilterFactory.Config> implements Ordered {

	private final Logger logger = LoggerFactory
			.getLogger(FallbackPathGatewayFilterFactory.class);

	public ErrorCodeToExceptionFilterFactory() {
		super(ErrorCodeToExceptionFilterFactory.Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
		ServerHttpResponse response = exchange.getResponse();
		logger.info("Response status code: " + response.getRawStatusCode());
		if (response.getStatusCode() != null
				&& response.getStatusCode().isError() ) {
			throw new ProxyClientResponseException(
					response.getStatusCode().getReasonPhrase(),
					response.getStatusCode().value(),
					response.getStatusCode().getReasonPhrase(),
					response.getHeaders());
		}
		}));
	}

	@Override
	public String name() {
		return "ErrorCodeToException";
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	public static class Config {
		//Put the configuration properties for your filter here
	}
	
}

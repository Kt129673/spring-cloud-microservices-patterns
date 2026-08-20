package com.kiran.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global logging filter that logs every incoming request and response.
 * Demonstrates Spring Cloud Gateway's filter mechanism.
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        log.info("===== GATEWAY REQUEST =====");
        log.info("Path: {}", request.getPath());
        log.info("Method: {}", request.getMethod());
        log.info("URI: {}", request.getURI());
        log.info("===========================");

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("===== GATEWAY RESPONSE =====");
            log.info("Status: {}", exchange.getResponse().getStatusCode());
            log.info("Path: {}", request.getPath());
            log.info("============================");
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Execute first
    }
}

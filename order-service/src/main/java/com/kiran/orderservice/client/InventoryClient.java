package com.kiran.orderservice.client;

import com.kiran.orderservice.dto.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for synchronous communication with inventory-service.
 * Name must match inventory-service's spring.application.name in Eureka.
 * Fallback class handles circuit breaker scenarios.
 */
@FeignClient(
        name = "inventory-service",
        fallback = InventoryFallback.class
)
public interface InventoryClient {

    @GetMapping("/inventory/{product}")
    InventoryResponse checkStock(@PathVariable("product") String product);
}

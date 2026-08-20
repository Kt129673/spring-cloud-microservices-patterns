package com.kiran.orderservice.client;

import com.kiran.orderservice.dto.InventoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback when inventory-service is down or circuit breaker is open.
 * Returns a safe default response instead of crashing.
 */
@Component
public class InventoryFallback implements InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryFallback.class);

    @Override
    public InventoryResponse checkStock(String product) {
        log.warn("CIRCUIT BREAKER ACTIVATED — inventory-service unavailable. Returning fallback for product: {}", product);
        return new InventoryResponse(
                product,
                0,
                false,
                "Stock check unavailable — inventory service is down. Order will be verified asynchronously."
        );
    }
}

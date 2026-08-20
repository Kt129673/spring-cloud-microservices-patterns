package com.kiran.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ORDER SERVICE — Core Business Service (Port 8081)
 *
 * ┌───────────────────────────────────────────────────────────────────────────┐
 * │ 🔄 MONOLITH vs MICROSERVICE                                             │
 * │                                                                         │
 * │ In a monolith, OrderService is just a @Service class that directly      │
 * │ injects InventoryService (same JVM, same classpath):                    │
 * │                                                                         │
 * │   @Autowired                                                            │
 * │   private InventoryService inventoryService; // Direct method call      │
 * │   inventoryService.checkStock("laptop");     // No network, instant     │
 * │                                                                         │
 * │ In microservices, inventory-service is a SEPARATE app on port 8082.     │
 * │ We need HTTP calls to communicate. But how?                             │
 * │                                                                         │
 * │ THREE KEY ANNOTATIONS ON THIS CLASS:                                    │
 * │ 1. @SpringBootApplication — Auto-config + component scan               │
 * │ 2. @EnableDiscoveryClient — Register with Eureka so Gateway can find us │
 * │ 3. @EnableFeignClients — Scan for @FeignClient interfaces to generate   │
 * │    HTTP client proxies (see InventoryClient.java)                       │
 * └───────────────────────────────────────────────────────────────────────────┘
 *
 * This service demonstrates:
 * - SYNC communication: Feign call to inventory-service (check stock)
 * - ASYNC communication: Kafka publish to "orders-v2" topic
 * - SAGA pattern: Listens for InventoryEvent to complete/fail orders
 * - Circuit Breaker: Resilience4j fallback when inventory-service is down
 * - Validation: @Valid on OrderRequest with Jakarta constraints
 * - Exception Handling: @RestControllerAdvice for consistent errors
 * - DTO Pattern: OrderRequest (in) / OrderResponse (out) / Order (entity)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

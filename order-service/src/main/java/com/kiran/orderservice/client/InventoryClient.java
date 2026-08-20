package com.kiran.orderservice.client;

import com.kiran.orderservice.dto.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * FEIGN CLIENT — Declarative HTTP Client for Sync Communication
 *
 * ┌───────────────────────────────────────────────────────────────────────────┐
 * │ 🔄 MONOLITH vs MICROSERVICE                                             │
 * │                                                                         │
 * │ MONOLITH WAY (direct method call — same JVM):                           │
 * │   @Autowired                                                            │
 * │   private InventoryService inventoryService;                            │
 * │   InventoryResponse stock = inventoryService.checkStock("laptop");      │
 * │                                                                         │
 * │ MICROSERVICE WAY (HTTP call — different JVM, different port):           │
 * │   // Without Feign — ugly, verbose:                                     │
 * │   RestTemplate rt = new RestTemplate();                                 │
 * │   rt.getForEntity("http://inventory-service:8082/inventory/laptop"...); │
 * │                                                                         │
 * │   // With Feign — just write an interface, Spring generates the HTTP    │
 * │   // client automatically! Looks like a local method call:              │
 * │   inventoryClient.checkStock("laptop"); // Looks local, but it's HTTP!  │
 * │                                                                         │
 * │ HOW FEIGN RESOLVES THE URL (no hardcoded IP):                           │
 * │   @FeignClient(name = "inventory-service")                              │
 * │       ↓                                                                 │
 * │   "inventory-service" → Eureka lookup → [192.168.1.5:8082]             │
 * │       ↓                                                                 │
 * │   LoadBalancer picks one instance (round-robin)                         │
 * │       ↓                                                                 │
 * │   HTTP GET http://192.168.1.5:8082/inventory/laptop                     │
 * │                                                                         │
 * │ The "name" MUST match the target service's spring.application.name      │
 * │ registered in Eureka.                                                   │
 * └───────────────────────────────────────────────────────────────────────────┘
 *
 * @FeignClient attributes:
 * - name: The Eureka service name to discover (NOT a URL)
 * - fallback: Class to invoke when circuit breaker is OPEN or service is DOWN
 *   (see InventoryFallback.java — prevents cascading failures)
 */
@FeignClient(
        name = "inventory-service",
        fallback = InventoryFallback.class
)
public interface InventoryClient {

    /**
     * This looks like a normal interface method, but Feign generates an HTTP client
     * that makes: GET http://{resolved-ip}:{port}/inventory/{product}
     *
     * The @GetMapping and @PathVariable work exactly like in a @RestController,
     * but here they define the OUTGOING request, not an incoming one.
     */
    @GetMapping("/inventory/{product}")
    InventoryResponse checkStock(@PathVariable("product") String product);
}

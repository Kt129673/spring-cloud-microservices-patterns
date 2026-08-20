package com.kiran.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API GATEWAY — Single Entry Point for All Client Requests
 *
 * ┌───────────────────────────────────────────────────────────────────────────┐
 * │ 🔄 MONOLITH vs MICROSERVICE                                             │
 * │                                                                         │
 * │ In a monolith, clients call ONE URL: http://myapp.com/api/orders        │
 * │ Everything is behind one server, one port.                              │
 * │                                                                         │
 * │ In microservices, clients would need to know:                           │
 * │   - http://order-host:8081/orders                                       │
 * │   - http://inventory-host:8082/inventory                                │
 * │   - http://notif-host:8083/notifications                                │
 * │ 3 different URLs! With 20 services in production? NIGHTMARE.            │
 * │                                                                         │
 * │ SOLUTION: API Gateway is the "front door" — ONE URL for everything.     │
 * │   Client → http://gateway:8080/api/orders     → routes to order-service │
 * │   Client → http://gateway:8080/api/inventory → routes to inventory-svc  │
 * │                                                                         │
 * │ Gateway also handles CROSS-CUTTING CONCERNS:                            │
 * │   - Routing (path → service mapping)                                    │
 * │   - Load balancing (lb:// prefix uses Eureka to find instances)         │
 * │   - Logging (LoggingFilter logs every request/response)                 │
 * │   - In production: authentication, rate limiting, CORS                  │
 * └───────────────────────────────────────────────────────────────────────────┘
 *
 * @EnableDiscoveryClient — Registers this gateway with Eureka so it can
 * discover backend services by name (e.g., "order-service" → actual IP:port).
 *
 * NOTE: Spring Cloud Gateway is REACTIVE (built on Project Reactor/Netty).
 * Unlike regular Spring MVC apps, it uses non-blocking I/O for high throughput.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

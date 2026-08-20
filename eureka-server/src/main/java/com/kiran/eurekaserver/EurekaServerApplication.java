package com.kiran.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * EUREKA SERVER — Service Discovery Registry
 *
 * ┌───────────────────────────────────────────────────────────────────────────┐
 * │ 🔄 MONOLITH vs MICROSERVICE                                             │
 * │                                                                         │
 * │ In a monolith, all modules live in ONE app. OrderService calls           │
 * │ InventoryService directly — they're in the same JVM, same classpath.     │
 * │ No network needed.                                                      │
 * │                                                                         │
 * │ In microservices, OrderService and InventoryService are SEPARATE apps    │
 * │ running on different ports (or different servers). How does              │
 * │ order-service "find" inventory-service? Hardcoding URLs like            │
 * │ "http://localhost:8082" is fragile — what if the port changes?          │
 * │ What if there are 3 instances on different IPs?                         │
 * │                                                                         │
 * │ SOLUTION: Eureka acts as a "phone book" for all services.               │
 * │ - Each service REGISTERS itself with Eureka on startup                  │
 * │ - Each service DISCOVERS other services by name (not URL)               │
 * │ - Eureka tracks which instances are alive via heartbeats (every 30s)    │
 * │                                                                         │
 * │ STARTUP ORDER: Eureka MUST start first — other services depend on it.   │
 * └───────────────────────────────────────────────────────────────────────────┘
 *
 * @EnableEurekaServer — This single annotation transforms a regular Spring Boot
 * app into a fully functional service registry. The Eureka dashboard is
 * available at http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

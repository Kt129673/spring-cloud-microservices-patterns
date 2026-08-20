package com.kiran.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * INVENTORY SERVICE — Domain Service for Stock Management (Port 8082)
 *
 * ┌───────────────────────────────────────────────────────────────────────────┐
 * │ 🔄 MONOLITH vs MICROSERVICE                                             │
 * │                                                                         │
 * │ In a monolith, the inventory logic is just a package inside the main    │
 * │ application. It shares the same database as orders and users.           │
 * │                                                                         │
 * │ In microservices, InventoryService is a fully INDEPENDENT application:  │
 * │ - It runs on its own port (8082)                                        │
 * │ - It has its OWN database (Database-per-Service pattern).               │
 * │ - OrderService CANNOT query the inventory tables directly. It MUST use  │
 * │   the APIs or events exposed by InventoryService.                       │
 * │                                                                         │
 * │ WHY DO THIS?                                                            │
 * │ - Independent Scaling: During a Black Friday sale, you might have       │
 * │   10 instances of OrderService but only 3 of InventoryService.          │
 * │ - Independent Deployment: You can deploy an inventory bug fix without   │
 * │   taking down the order system.                                         │
 * │ - Technology Diversity: OrderService could be Java/Spring, while        │
 * │   InventoryService could be Go or Node.js.                              │
 * └───────────────────────────────────────────────────────────────────────────┘
 */
@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}

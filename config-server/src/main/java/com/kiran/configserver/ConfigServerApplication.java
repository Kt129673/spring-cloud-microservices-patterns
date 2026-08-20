package com.kiran.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * CONFIG SERVER — Centralized Configuration Management
 *
 * ┌───────────────────────────────────────────────────────────────────────────┐
 * │ 🔄 MONOLITH vs MICROSERVICE                                             │
 * │                                                                         │
 * │ In a monolith, you have ONE application.properties file.                │
 * │ Change the DB URL? Edit one file, redeploy one app.                     │
 * │                                                                         │
 * │ In microservices, you have N services × M environments = N×M config     │
 * │ files. Kafka broker URL, Eureka endpoint, Zipkin URL — all DUPLICATED   │
 * │ across every service. Change the Kafka broker? Edit 4 files,            │
 * │ redeploy 4 services. NIGHTMARE.                                         │
 * │                                                                         │
 * │ SOLUTION: Config Server stores ALL configs in ONE Git repo.             │
 * │ Services fetch their config from Config Server at startup.              │
 * │                                                                         │
 * │ HOW IT WORKS:                                                           │
 * │ 1. config-repo/application.properties → shared by ALL services          │
 * │ 2. config-repo/order-service.properties → only for order-service        │
 * │ 3. Config Server MERGES both and serves to the requesting service       │
 * │                                                                         │
 * │ RESULT: Each service's local application.properties is now just 2 lines:│
 * │   spring.application.name=order-service                                 │
 * │   spring.config.import=optional:configserver:http://localhost:8888       │
 * │                                                                         │
 * │ ⚠️ WHY DOESN'T EUREKA USE CONFIG SERVER?                               │
 * │ Config Server registers with Eureka. If Eureka also fetched config from │
 * │ Config Server → circular dependency! Neither can start. So Eureka keeps │
 * │ its own local config.                                                   │
 * └───────────────────────────────────────────────────────────────────────────┘
 *
 * @EnableConfigServer — This annotation makes this app a Config Server that
 * reads config from the Git repo defined in application.properties and serves
 * it to client services via REST API (e.g., GET /order-service/default).
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}

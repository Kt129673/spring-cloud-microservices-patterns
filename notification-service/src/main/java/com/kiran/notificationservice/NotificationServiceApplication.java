package com.kiran.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * NOTIFICATION SERVICE — Event-Driven Microservice (Port 8083)
 *
 * ┌───────────────────────────────────────────────────────────────────────────┐
 * │ 🔄 MONOLITH vs MICROSERVICE                                             │
 * │                                                                         │
 * │ In a monolith, after deducting stock, you might call:                   │
 * │   emailService.sendOrderConfirmation(order);                            │
 * │ If the email server is slow, the user waits 5 seconds to see their      │
 * │ order confirmation screen.                                              │
 * │                                                                         │
 * │ In microservices, we use EVENT-DRIVEN ARCHITECTURE.                     │
 * │ This service doesn't have a REST API at all! It just sits in the        │
 * │ background listening to Kafka topics (inventory-events).                │
 * │                                                                         │
 * │ This represents true loose coupling: inventory-service doesn't even     │
 * │ know notification-service exists. It just shouts "Inventory confirmed!" │
 * │ into Kafka. Notification-service hears it and sends the email.          │
 * └───────────────────────────────────────────────────────────────────────────┘
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}

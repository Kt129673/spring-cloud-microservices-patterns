package com.kiran.inventoryservice.consumer;

import com.kiran.common.dto.InventoryEvent;
import com.kiran.common.dto.OrderEvent;
import com.kiran.inventoryservice.producer.InventoryProducer;
import com.kiran.inventoryservice.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer that listens for OrderEvents.
 * Performs REAL inventory logic:
 *   - Check stock availability
 *   - Deduct stock if available → publish CONFIRMED
 *   - If insufficient → publish FAILED (SAGA compensation trigger)
 *
 * ┌───────────────────────────────────────────────────────────────────────────┐
 * │ 🔄 MONOLITH vs MICROSERVICE                                             │
 * │                                                                         │
 * │ In a monolith, communication is SYNCHRONOUS. Order calls Inventory,     │
 * │ waits for a return value, and holds the thread.                         │
 * │                                                                         │
 * │ In microservices, we use ASYNCHRONOUS messaging (Kafka) for state       │
 * │ changes.                                                                │
 * │                                                                         │
 * │ WHY ASYNC?                                                              │
 * │ - Decoupling: order-service doesn't care if inventory-service is        │
 * │   temporarily down. Kafka holds the message until it comes back up.     │
 * │ - Performance: order-service doesn't wait for inventory processing.     │
 * │ - Scalability: If orders spike, we just spin up more InventoryConsumers │
 * │   in a consumer group to chew through the Kafka backlog.                │
 * └───────────────────────────────────────────────────────────────────────────┘
 */
@Service
public class OrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryProducer inventoryProducer;

    @KafkaListener(
            topics = "orders-v2",
            groupId = "inventory-group"
    )
    public void consume(OrderEvent orderEvent) {
        log.info("Received OrderEvent: orderId={}, product={}, quantity={}",
                orderEvent.getId(), orderEvent.getProduct(), orderEvent.getQuantity());

        // Real inventory logic: attempt to deduct stock
        boolean success = inventoryService.deductStock(orderEvent.getProduct(), orderEvent.getQuantity());

        // Create inventory event based on result
        InventoryEvent inventoryEvent;

        if (success) {
            inventoryEvent = new InventoryEvent(
                    orderEvent.getId(),
                    orderEvent.getProduct(),
                    "CONFIRMED",
                    "Inventory reserved successfully for " + orderEvent.getQuantity() + " units of " + orderEvent.getProduct()
            );
            log.info("Stock deducted successfully for orderId: {}", orderEvent.getId());
        } else {
            inventoryEvent = new InventoryEvent(
                    orderEvent.getId(),
                    orderEvent.getProduct(),
                    "FAILED",
                    "Insufficient stock for product: " + orderEvent.getProduct()
            );
            log.warn("Stock deduction FAILED for orderId: {}. Triggering SAGA compensation.", orderEvent.getId());
        }

        // Publish inventory event (consumed by order-service for SAGA + notification-service)
        inventoryProducer.publish(inventoryEvent);
    }
}
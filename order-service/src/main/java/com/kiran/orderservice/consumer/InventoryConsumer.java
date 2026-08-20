package com.kiran.orderservice.consumer;

import com.kiran.common.dto.InventoryEvent;
import com.kiran.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * SAGA completion: Listens for InventoryEvents and updates order status.
 * This closes the SAGA loop — inventory-service tells order-service
 * whether stock was confirmed or failed.
 */
@Service
public class InventoryConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryConsumer.class);

    @Autowired
    private OrderService orderService;

    @KafkaListener(
            topics = "inventory-events",
            groupId = "order-saga-group"
    )
    public void consumeInventoryEvent(InventoryEvent event) {
        log.info("Received InventoryEvent: orderId={}, status={}, message={}",
                event.getOrderId(), event.getStatus(), event.getMessage());

        // Update order status based on inventory result (SAGA compensation)
        orderService.updateOrderStatus(event.getOrderId(), event.getStatus());

        log.info("SAGA completed for orderId: {}. Final status: {}", event.getOrderId(), event.getStatus());
    }
}

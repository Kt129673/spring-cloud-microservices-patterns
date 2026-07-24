package com.kiran.inventoryservice.consumer;

import com.kiran.common.dto.InventoryEvent;
import com.kiran.common.dto.OrderEvent;
import com.kiran.inventoryservice.producer.InventoryProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
    @Autowired
    private InventoryProducer inventoryProducer;


    @KafkaListener(
            topics = "orders-v2",
            groupId = "inventory-group"
    )
    public void consume(OrderEvent orderEvent) {

        System.out.println("=================================");
        System.out.println("Order Received Successfully");
        System.out.println(orderEvent);
        System.out.println("=================================");


        // Create Inventory Event
        InventoryEvent inventoryEvent = new InventoryEvent();

        inventoryEvent.setOrderId(orderEvent.getId());
        inventoryEvent.setStatus("SUCCESS");
        inventoryEvent.setMessage("Inventory Reserved");

        // Publish Inventory Event
        inventoryProducer.publish(inventoryEvent);

    }
}
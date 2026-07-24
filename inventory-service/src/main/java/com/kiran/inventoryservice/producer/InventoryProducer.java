package com.kiran.inventoryservice.producer;

import com.kiran.common.dto.InventoryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryProducer {

    @Autowired
    private  KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    @Value("${app.kafka.inventory-topic}")
    private String topic;



    public void publish(InventoryEvent event) {
        kafkaTemplate.send(topic, event);

        System.out.println("=================================");
        System.out.println("Inventory Event Published");
        System.out.println(event);
        System.out.println("=================================");
    }
}
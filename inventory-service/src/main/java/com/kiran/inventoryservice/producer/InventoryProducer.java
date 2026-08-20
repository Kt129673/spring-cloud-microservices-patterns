package com.kiran.inventoryservice.producer;

import com.kiran.common.dto.InventoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryProducer {

    private static final Logger log = LoggerFactory.getLogger(InventoryProducer.class);

    @Autowired
    private KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    @Value("${app.kafka.inventory-topic}")
    private String topic;

    public void publish(InventoryEvent event) {
        log.info("Publishing InventoryEvent to topic [{}]: orderId={}, status={}, message={}",
                topic, event.getOrderId(), event.getStatus(), event.getMessage());

        kafkaTemplate.send(topic, event.getOrderId().toString(), event);

        log.info("InventoryEvent published successfully for orderId: {}", event.getOrderId());
    }
}
package com.kiran.notificationservice.consumer;

import com.kiran.common.dto.InventoryEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @KafkaListener(
            topics = "inventory-events",
                groupId = "notification-group"
    )
    public void consume(InventoryEvent event) {

        System.out.println("=================================");
        System.out.println("EMAIL SENT SUCCESSFULLY");
        System.out.println(event);
        System.out.println("=================================");
    }
}
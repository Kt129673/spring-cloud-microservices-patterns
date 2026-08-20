package com.kiran.notificationservice.consumer;

import com.kiran.common.dto.InventoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Notification consumer — listens for InventoryEvents and simulates email notifications.
 * In production, this would integrate with JavaMailSender or a notification provider (SendGrid, SNS).
 */
@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(
            topics = "inventory-events",
            groupId = "notification-group"
    )
    public void consume(InventoryEvent event) {
        log.info("Received InventoryEvent for notification: orderId={}, status={}", event.getOrderId(), event.getStatus());

        if ("CONFIRMED".equals(event.getStatus())) {
            sendConfirmationEmail(event);
        } else if ("FAILED".equals(event.getStatus())) {
            sendFailureEmail(event);
        }
    }

    private void sendConfirmationEmail(InventoryEvent event) {
        log.info("============================================");
        log.info("📧 SENDING CONFIRMATION EMAIL");
        log.info("To: Customer (orderId={})", event.getOrderId());
        log.info("Subject: Order Confirmed!");
        log.info("Body: Your order for '{}' has been confirmed.", event.getProduct());
        log.info("      {}", event.getMessage());
        log.info("============================================");
    }

    private void sendFailureEmail(InventoryEvent event) {
        log.warn("============================================");
        log.warn("📧 SENDING FAILURE EMAIL");
        log.warn("To: Customer (orderId={})", event.getOrderId());
        log.warn("Subject: Order Failed");
        log.warn("Body: Sorry, your order for '{}' could not be processed.", event.getProduct());
        log.warn("      Reason: {}", event.getMessage());
        log.warn("============================================");
    }
}
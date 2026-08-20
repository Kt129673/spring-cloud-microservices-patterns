package com.kiran.notificationservice.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Dead Letter Topic consumer.
 * Listens for messages that failed processing after all retries.
 * In production: alert ops team, store for manual review, or send to monitoring system.
 */
@Service
public class DltConsumer {

    private static final Logger log = LoggerFactory.getLogger(DltConsumer.class);

    @KafkaListener(
            topics = "inventory-events.DLT",
            groupId = "dlt-group"
    )
    public void consumeDlt(String failedMessage) {
        log.error("============================================");
        log.error("🚨 DEAD LETTER TOPIC — FAILED MESSAGE");
        log.error("Message: {}", failedMessage);
        log.error("Action Required: Investigate and reprocess manually.");
        log.error("============================================");

        // In production:
        // 1. Save to a "failed_messages" database table
        // 2. Send alert to Slack/PagerDuty
        // 3. Increment a Prometheus counter for monitoring
    }
}

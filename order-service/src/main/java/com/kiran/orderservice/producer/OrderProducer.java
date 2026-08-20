package com.kiran.orderservice.producer;

import com.kiran.common.dto.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void sendOrder(OrderEvent order) {
        log.info("Publishing OrderEvent to topic [{}]: orderId={}, product={}, status={}",
                topic, order.getId(), order.getProduct(), order.getStatus());

        kafkaTemplate.send(topic, order.getId().toString(), order);

        log.info("OrderEvent published successfully for orderId: {}", order.getId());
    }
}
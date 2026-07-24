package com.kiran.orderservice.producer;

import com.kiran.common.dto.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {


    @Autowired
    private  KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;


    public void sendOrder(OrderEvent order) {

        kafkaTemplate.send(topic, order);

        System.out.println("--------------------------------");
        System.out.println("Order Published Successfully");
        System.out.println(order);
        System.out.println("--------------------------------");
    }
}
package com.kiran.orderservice.service;

import com.kiran.common.dto.OrderEvent;
import com.kiran.orderservice.dto.OrderRequest;
import com.kiran.orderservice.dto.OrderResponse;
import com.kiran.orderservice.entity.Order;
import com.kiran.orderservice.producer.OrderProducer;
import com.kiran.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProducer orderProducer;

    public OrderResponse createOrder(OrderRequest request) {

        // Convert DTO to Entity
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setProduct(request.getProduct());
        order.setQuantity(request.getQuantity());

        // Save into H2 Database
        Order savedOrder = orderRepository.save(order);

        // Convert Entity to Event
        OrderEvent event = new OrderEvent();
        event.setId(savedOrder.getId());
        event.setCustomerName(savedOrder.getCustomerName());
        event.setProduct(savedOrder.getProduct());
        event.setQuantity(savedOrder.getQuantity());

        // Publish Event
        orderProducer.sendOrder(event);

        // Return Response
        return new OrderResponse(
                savedOrder.getId(),
                "Order Created Successfully"
        );
    }
}
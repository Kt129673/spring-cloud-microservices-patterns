package com.kiran.orderservice.service;

import com.kiran.common.dto.OrderEvent;
import com.kiran.orderservice.client.InventoryClient;
import com.kiran.orderservice.dto.InventoryResponse;
import com.kiran.orderservice.dto.OrderRequest;
import com.kiran.orderservice.dto.OrderResponse;
import com.kiran.orderservice.entity.Order;
import com.kiran.orderservice.exception.OrderNotFoundException;
import com.kiran.orderservice.producer.OrderProducer;
import com.kiran.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProducer orderProducer;

    @Autowired
    private InventoryClient inventoryClient;

    /**
     * Create order flow:
     * 1. Sync stock check via Feign (with circuit breaker fallback)
     * 2. Save order to H2 database
     * 3. Publish OrderEvent to Kafka (async)
     */
    public OrderResponse createOrder(OrderRequest request) {

        // Step 1: Sync stock check via Feign
        log.info("Checking stock for product: {} via Feign (sync)", request.getProduct());
        InventoryResponse stockCheck = inventoryClient.checkStock(request.getProduct());
        log.info("Stock check result: {}", stockCheck);

        // Determine initial status based on stock check
        String initialStatus;
        String responseMessage;

        if (stockCheck.isInStock() && stockCheck.getAvailableQuantity() >= request.getQuantity()) {
            initialStatus = "CREATED";
            responseMessage = "Order created successfully. Inventory confirmation pending.";
        } else if (!stockCheck.isInStock() && stockCheck.getMessage().contains("unavailable")) {
            // Circuit breaker fallback — inventory service is down
            initialStatus = "CREATED";
            responseMessage = "Order created. Stock check pending (inventory service unavailable).";
            log.warn("Inventory service fallback triggered. Order will be verified asynchronously.");
        } else {
            // Stock is insufficient
            initialStatus = "FAILED";
            responseMessage = "Insufficient stock. Available: " + stockCheck.getAvailableQuantity()
                    + ", Requested: " + request.getQuantity();
            log.warn("Insufficient stock for product: {}. Available: {}, Requested: {}",
                    request.getProduct(), stockCheck.getAvailableQuantity(), request.getQuantity());
        }

        // Step 2: Save order to database
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setProduct(request.getProduct());
        order.setQuantity(request.getQuantity());
        order.setStatus(initialStatus);

        Order savedOrder = orderRepository.save(order);
        log.info("Order saved with id: {}, status: {}", savedOrder.getId(), savedOrder.getStatus());

        // Step 3: Publish event to Kafka (async) — only if order is not already failed
        if ("CREATED".equals(initialStatus)) {
            OrderEvent event = new OrderEvent(
                    savedOrder.getId(),
                    savedOrder.getCustomerName(),
                    savedOrder.getProduct(),
                    savedOrder.getQuantity(),
                    savedOrder.getStatus()
            );
            orderProducer.sendOrder(event);
        }

        return new OrderResponse(savedOrder.getId(), savedOrder.getStatus(), responseMessage);
    }

    /**
     * Get all orders.
     */
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> new OrderResponse(order.getId(), order.getStatus(),
                        "Order for " + order.getProduct() + " by " + order.getCustomerName()))
                .collect(Collectors.toList());
    }

    /**
     * Get order by ID.
     */
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        return new OrderResponse(order.getId(), order.getStatus(),
                "Order for " + order.getProduct() + " by " + order.getCustomerName());
    }

    /**
     * Update order status — called by SAGA InventoryConsumer when inventory-events arrive.
     */
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        order.setStatus(status);
        orderRepository.save(order);
        log.info("Order {} status updated to: {}", orderId, status);
    }
}
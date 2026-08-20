package com.kiran.orderservice.controller;

import com.kiran.orderservice.dto.OrderRequest;
import com.kiran.orderservice.dto.OrderResponse;
import com.kiran.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ORDER CONTROLLER — API Entry Point for Orders
 *
 * ┌───────────────────────────────────────────────────────────────────────────┐
 * │ 🔄 MONOLITH vs MICROSERVICE                                             │
 * │                                                                         │
 * │ In a monolith, this controller would call OrderService, which might     │
 * │ call InventoryService directly in the same database transaction. If     │
 * │ anything fails, the whole transaction rolls back automatically (ACID).  │
 * │                                                                         │
 * │ In microservices, we CANNOT have a single database transaction across   │
 * │ order-service (H2 DB) and inventory-service (H2 DB).                    │
 * │                                                                         │
 * │ HOW WE SOLVE THIS (Eventual Consistency):                               │
 * │ 1. We accept the order and validate it (@Valid)                         │
 * │ 2. We do a QUICK sync check with inventory via Feign                    │
 * │ 3. We save the order locally (status=CREATED) and return 201 Created    │
 * │    to the user IMMEDIATELY (Fire and Forget).                           │
 * │ 4. We publish an event to Kafka. The actual inventory deduction happens │
 * │    asynchronously in the background (SAGA pattern).                     │
 * │                                                                         │
 * │ The user gets a fast response, but the final state is settled later.    │
 * └───────────────────────────────────────────────────────────────────────────┘
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * Create a new order.
     * Flow: Validate → Feign stock check → Save → Kafka publish
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        log.info("Received order request for customer: {}, product: {}", request.getCustomerName(), request.getProduct());
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all orders.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("Fetching all orders");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Get order by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.info("Fetching order with id: {}", id);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
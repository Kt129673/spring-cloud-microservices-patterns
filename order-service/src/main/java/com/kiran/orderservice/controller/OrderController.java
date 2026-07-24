package com.kiran.orderservice.controller;

import com.kiran.orderservice.dto.OrderRequest;
import com.kiran.orderservice.dto.OrderResponse;
import com.kiran.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }
}
package com.kiran.inventoryservice.controller;

import com.kiran.inventoryservice.dto.InventoryRequest;
import com.kiran.inventoryservice.dto.InventoryResponse;
import com.kiran.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;

    /**
     * Check stock for a specific product.
     * Called by order-service via Feign (sync).
     */
    @GetMapping("/{product}")
    public ResponseEntity<InventoryResponse> checkStock(@PathVariable String product) {
        log.info("Stock check request for product: {}", product);
        return ResponseEntity.ok(inventoryService.checkStock(product));
    }

    /**
     * Get all inventory items.
     */
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        log.info("Fetching all inventory");
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    /**
     * Add stock for a product (for testing/setup).
     */
    @PostMapping
    public ResponseEntity<InventoryResponse> addStock(@Valid @RequestBody InventoryRequest request) {
        log.info("Add stock request: product={}, quantity={}", request.getProductName(), request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.addStock(request));
    }
}

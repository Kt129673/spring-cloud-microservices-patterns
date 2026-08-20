package com.kiran.inventoryservice.service;

import com.kiran.inventoryservice.dto.InventoryRequest;
import com.kiran.inventoryservice.dto.InventoryResponse;
import com.kiran.inventoryservice.entity.InventoryItem;
import com.kiran.inventoryservice.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Check stock for a product (used by Feign sync call from order-service).
     */
    public InventoryResponse checkStock(String productName) {
        log.info("Checking stock for product: {}", productName);

        return inventoryRepository.findByProductName(productName)
                .map(item -> {
                    boolean inStock = item.getQuantity() > 0;
                    log.info("Product: {}, Available: {}, InStock: {}", productName, item.getQuantity(), inStock);
                    return new InventoryResponse(
                            item.getProductName(),
                            item.getQuantity(),
                            inStock,
                            inStock ? "Stock available" : "Out of stock"
                    );
                })
                .orElseGet(() -> {
                    log.warn("Product not found in inventory: {}", productName);
                    return new InventoryResponse(productName, 0, false, "Product not found in inventory");
                });
    }

    /**
     * Deduct stock for a product (called during async Kafka processing).
     * Returns true if deduction was successful, false if insufficient stock.
     */
    @Transactional
    public boolean deductStock(String productName, int quantity) {
        log.info("Attempting to deduct {} units of product: {}", quantity, productName);

        return inventoryRepository.findByProductName(productName)
                .map(item -> {
                    if (item.getQuantity() >= quantity) {
                        item.setQuantity(item.getQuantity() - quantity);
                        inventoryRepository.save(item);
                        log.info("Stock deducted. Product: {}, Deducted: {}, Remaining: {}",
                                productName, quantity, item.getQuantity());
                        return true;
                    } else {
                        log.warn("Insufficient stock for product: {}. Available: {}, Requested: {}",
                                productName, item.getQuantity(), quantity);
                        return false;
                    }
                })
                .orElseGet(() -> {
                    log.error("Cannot deduct stock — product not found: {}", productName);
                    return false;
                });
    }

    /**
     * Add stock for a product (REST endpoint for testing/setup).
     */
    @Transactional
    public InventoryResponse addStock(InventoryRequest request) {
        log.info("Adding stock: product={}, quantity={}", request.getProductName(), request.getQuantity());

        InventoryItem item = inventoryRepository.findByProductName(request.getProductName())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + request.getQuantity());
                    return existing;
                })
                .orElseGet(() -> {
                    InventoryItem newItem = new InventoryItem();
                    newItem.setProductName(request.getProductName());
                    newItem.setQuantity(request.getQuantity());
                    return newItem;
                });

        InventoryItem saved = inventoryRepository.save(item);
        log.info("Stock updated: product={}, totalQuantity={}", saved.getProductName(), saved.getQuantity());

        return new InventoryResponse(
                saved.getProductName(),
                saved.getQuantity(),
                true,
                "Stock added successfully"
        );
    }

    /**
     * Get all inventory items.
     */
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(item -> new InventoryResponse(
                        item.getProductName(),
                        item.getQuantity(),
                        item.getQuantity() > 0,
                        item.getQuantity() > 0 ? "In stock" : "Out of stock"
                ))
                .collect(Collectors.toList());
    }
}

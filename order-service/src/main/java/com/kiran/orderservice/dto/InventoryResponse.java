package com.kiran.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO from inventory-service stock check (Feign).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private String product;
    private Integer availableQuantity;
    private boolean inStock;
    private String message;
}

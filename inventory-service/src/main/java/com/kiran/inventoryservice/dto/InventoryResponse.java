package com.kiran.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private String product;
    private Integer availableQuantity;
    private boolean inStock;
    private String message;
}

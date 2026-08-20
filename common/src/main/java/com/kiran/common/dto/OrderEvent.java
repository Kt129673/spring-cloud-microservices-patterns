package com.kiran.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {

    private Long id;
    private String customerName;
    private String product;
    private Integer quantity;
    private String status; // CREATED, CONFIRMED, FAILED
}
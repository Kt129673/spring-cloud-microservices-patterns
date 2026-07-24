package com.kiran.orderservice.dto;

public class OrderRequest {

    private String customerName;
    private String product;
    private Integer quantity;

    public OrderRequest() {
    }

    public OrderRequest(String customerName, String product, Integer quantity) {
        this.customerName = customerName;
        this.product = product;
        this.quantity = quantity;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
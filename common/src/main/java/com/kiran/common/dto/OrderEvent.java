package com.kiran.common.dto;

public class OrderEvent {

    private Long id;
    private String customerName;
    private String product;
    private Integer quantity;

    public OrderEvent() {
    }

    public OrderEvent(Long id, String customerName, String product, Integer quantity) {
        this.id = id;
        this.customerName = customerName;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "OrderEvent{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
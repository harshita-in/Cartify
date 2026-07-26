package com.ecommerce.orderservice.dto;

import java.util.List;

public class OrderRequest {
    private List<OrderItemRequest> items;
    private String shippingAddress;

    public OrderRequest() {}

    public OrderRequest(List<OrderItemRequest> items, String shippingAddress) {
        this.items = items;
        this.shippingAddress = shippingAddress;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}

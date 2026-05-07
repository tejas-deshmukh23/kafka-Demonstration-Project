package com.zomato.events;

public class OrderPlacedEvent {
    private String orderId;
    private String customerId;
    private String product;
    private int quantity;
    private String region; // north, south, east, west (from your diagram!)

    public OrderPlacedEvent() {}

    public OrderPlacedEvent(String orderId, String customerId, String product, int quantity, String region) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.product = product;
        this.quantity = quantity;
        this.region = region;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    @Override
    public String toString() {
        return "OrderPlacedEvent{orderId='" + orderId + "', product='" + product +
               "', quantity=" + quantity + ", region='" + region + "'}";
    }
}
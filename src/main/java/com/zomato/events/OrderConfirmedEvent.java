package com.zomato.events;

public class OrderConfirmedEvent {
    private String orderId;
    private String customerId;
    private String status;
    private String message;
    private String region;

    public OrderConfirmedEvent() {}

    public OrderConfirmedEvent(String orderId, String customerId, String status, String message, String region) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = status;
        this.message = message;
        this.region = region;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}
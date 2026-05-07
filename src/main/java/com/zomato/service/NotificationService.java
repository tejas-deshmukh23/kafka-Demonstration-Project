package com.zomato.service;

import com.zomato.events.OrderConfirmedEvent;
import com.zomato.events.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    /**
     * Consumer 1 from your diagram - listens to order-placed
     * Sends an acknowledgment to customer immediately
     */
    @KafkaListener(
        topics = "order-placed",
        groupId = "notification-order-group"
    )
    public void onOrderPlaced(
            OrderPlacedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {

        System.out.println("\n🔔 [NOTIFICATION SERVICE] New Order Alert!");
        System.out.println("   ─────────────────────────────────────");
        System.out.println("   📧 Sending order confirmation email...");
        System.out.println("   To       : Customer " + event.getCustomerId());
        System.out.println("   Subject  : Order Received - " + event.getOrderId());
        System.out.println("   Message  : Hi! Your order for " + event.getQuantity()
                           + "x " + event.getProduct() + " has been received.");
        System.out.println("   Region   : " + event.getRegion() + " (partition " + partition + ")");
        System.out.println("   Status   : ⏳ Processing...");
        System.out.println("   ─────────────────────────────────────");
    }

    /**
     * Consumer 2 from your diagram - listens to order-confirmed
     * Sends final status notification (CONFIRMED or FAILED)
     */
    @KafkaListener(
        topics = "order-confirmed",
        groupId = "notification-confirm-group"
    )
    public void onOrderConfirmed(
            OrderConfirmedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {

        System.out.println("\n🔔 [NOTIFICATION SERVICE] Order Status Update!");
        System.out.println("   ─────────────────────────────────────");

        if ("CONFIRMED".equals(event.getStatus())) {
            System.out.println("   📧 Sending order confirmation email...");
            System.out.println("   To       : Customer " + event.getCustomerId());
            System.out.println("   Subject  : ✅ Order Confirmed - " + event.getOrderId());
            System.out.println("   Message  : " + event.getMessage());
            System.out.println("   SMS      : 📱 Your order is confirmed! Track on Zomato app.");
        } else {
            System.out.println("   📧 Sending failure notification...");
            System.out.println("   To       : Customer " + event.getCustomerId());
            System.out.println("   Subject  : ❌ Order Failed - " + event.getOrderId());
            System.out.println("   Message  : " + event.getMessage());
            System.out.println("   SMS      : 📱 Sorry! We couldn't fulfill your order. Refund in 2-3 days.");
        }

        System.out.println("   Region   : " + event.getRegion() + " (partition " + partition + ")");
        System.out.println("   ─────────────────────────────────────");
    }
}
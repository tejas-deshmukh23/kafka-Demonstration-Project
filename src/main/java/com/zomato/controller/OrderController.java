//package com.zomato.controller;
//
//public class OrderController {
//
//}

package com.zomato.controller;

import com.zomato.events.OrderPlacedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    /**
     * POST /api/orders
     * Body: { "customerId": "C001", "product": "Burger", "quantity": 2, "region": "north" }
     *
     * The region acts like a partition key from your diagram!
     * "north" orders -> partition 0
     * "south" orders -> partition 1
     */
    @PostMapping
    public String placeOrder(@RequestBody OrderPlacedEvent event) {

        // Auto-generate orderId if not provided
        if (event.getOrderId() == null || event.getOrderId().isEmpty()) {
            event.setOrderId("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        // Use region as the Kafka message KEY
        // This ensures all "north" orders go to the same partition (consistent hashing)
        CompletableFuture<SendResult<String, OrderPlacedEvent>> future =
                kafkaTemplate.send("order-placed", event.getRegion(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("✅ Order sent to Kafka!");
                System.out.println("   Topic     : " + result.getRecordMetadata().topic());
                System.out.println("   Partition : " + result.getRecordMetadata().partition()); // 0=north, 1=south
                System.out.println("   Offset    : " + result.getRecordMetadata().offset());
                System.out.println("   Order     : " + event);
            } else {
                System.err.println("❌ Failed to send order: " + ex.getMessage());
            }
        });

        return "Order placed successfully! Order ID: " + event.getOrderId() +
               " | Region: " + event.getRegion();
    }

    // Quick test endpoint
    @GetMapping("/test")
    public String test() {
        return "Order Service is running! 🚀";
    }
}
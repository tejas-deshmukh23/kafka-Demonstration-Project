package com.zomato.service;

import com.zomato.events.OrderConfirmedEvent;
import com.zomato.events.OrderPlacedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InventoryService {

    @Autowired
    private KafkaTemplate<String, OrderConfirmedEvent> kafkaTemplate;

    // Simulated stock database — like your diagram where each region has its own DB!
    private static final Map<String, Integer> northInventory = new HashMap<>();
    private static final Map<String, Integer> southInventory = new HashMap<>();

    static {
        // North region stock
        northInventory.put("Burger", 50);
        northInventory.put("Pizza", 30);
        northInventory.put("Pasta", 20);

        // South region stock
        southInventory.put("Dosa", 100);
        southInventory.put("Idli", 80);
        southInventory.put("Biryani", 60);
    }

    /**
     * This consumer listens to the "order-placed" topic
     * groupId = "inventory-group" means all inventory instances share the load
     *
     * @Header(KafkaHeaders.RECEIVED_PARTITION) tells us which partition (0=north, 1=south)
     */
    @KafkaListener(
        topics = "order-placed",
        groupId = "inventory-group"
    )
    public void handleOrderPlaced(
            OrderPlacedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        System.out.println("\n📦 [INVENTORY SERVICE] Order received!");
        System.out.println("   Order ID  : " + event.getOrderId());
        System.out.println("   Product   : " + event.getProduct());
        System.out.println("   Quantity  : " + event.getQuantity());
        System.out.println("   Region    : " + event.getRegion());
        System.out.println("   Partition : " + partition + " (0=north, 1=south from your diagram!)");
        System.out.println("   Offset    : " + offset);

        // Check inventory based on region (just like your diagram - each region has its own DB)
        boolean inStock = checkStock(event.getRegion(), event.getProduct(), event.getQuantity());

        OrderConfirmedEvent confirmedEvent;
        if (inStock) {
            deductStock(event.getRegion(), event.getProduct(), event.getQuantity());
            confirmedEvent = new OrderConfirmedEvent(
                event.getOrderId(),
                event.getCustomerId(),
                "CONFIRMED",
                "✅ Stock available. Order confirmed for " + event.getProduct(),
                event.getRegion()
            );
            System.out.println("   ✅ Stock OK! Sending CONFIRMED event...");
        } else {
            confirmedEvent = new OrderConfirmedEvent(
                event.getOrderId(),
                event.getCustomerId(),
                "FAILED",
                "❌ Out of stock: " + event.getProduct() + " in " + event.getRegion(),
                event.getRegion()
            );
            System.out.println("   ❌ Out of stock! Sending FAILED event...");
        }

        // Publish to order-confirmed topic (this service is BOTH consumer and producer!)
        kafkaTemplate.send("order-confirmed", event.getRegion(), confirmedEvent);
    }

    private boolean checkStock(String region, String product, int quantity) {
        Map<String, Integer> inventory = region.equalsIgnoreCase("north") ? northInventory : southInventory;
        return inventory.getOrDefault(product, 0) >= quantity;
    }

    private void deductStock(String region, String product, int quantity) {
        Map<String, Integer> inventory = region.equalsIgnoreCase("north") ? northInventory : southInventory;
        inventory.put(product, inventory.get(product) - quantity);
        System.out.println("   📉 Stock updated. Remaining " + product + ": " + inventory.get(product));
    }
}
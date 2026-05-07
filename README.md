## About This Project

A hands-on implementation of Apache Kafka with Spring Boot, 
modeled around a Zomato-inspired food delivery system.

## What This Project Demonstrates

- Kafka broker setup using Docker with Zookeeper
- Kafdrop UI for real-time topic and partition visualization
- Producer → publishes OrderPlacedEvent to the order-placed topic
- Topic partitioning by region (north → partition 0, south → partition 1)
- Multiple consumers in the same consumer group — Kafka automatically 
  rebalances and assigns one partition per consumer instance
- Consumer-Producer chaining — InventoryService consumes from 
  order-placed and produces to order-confirmed after stock validation
- NotificationService consumes from both topics independently

## Tech Stack
- Java 17
- Spring Boot 3.2
- Spring Kafka
- Apache Kafka + Zookeeper (Docker)
- Kafdrop (Kafka UI)
- MySQL

package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.client.ProductClient;
import com.ecommerce.orderservice.dto.*;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.model.OrderStatus;
import com.ecommerce.orderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductClient productClient;

    @PostMapping
    @Transactional
    @CircuitBreaker(name = "productServiceCB", fallbackMethod = "fallbackPlaceOrder")
    public ResponseEntity<?> placeOrder(
            @RequestBody OrderRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User identity not found in request headers");
        }

        Long customerId = Long.parseLong(userId);
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Order must contain at least one item");
        }

        BigDecimal totalOrderPrice = BigDecimal.ZERO;
        List<OrderItem> orderItemsToSave = new ArrayList<>();
        List<StockReductionItem> stockReductions = new ArrayList<>();

        // 1. Validate pricing and availability via Feign client
        for (OrderItemRequest itemReq : request.getItems()) {
            ProductDto product = productClient.getProductById(itemReq.getProductId());
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product not found with ID: " + itemReq.getProductId());
            }

            if (product.getStockQuantity() < itemReq.getQuantity()) {
                return ResponseEntity.badRequest().body("Insufficient stock for product: " + product.getName() 
                        + " (Available: " + product.getStockQuantity() + ")");
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalOrderPrice = totalOrderPrice.add(itemTotal);

            OrderItem orderItem = new OrderItem(
                    itemReq.getProductId(),
                    itemReq.getQuantity(),
                    product.getPrice()
            );
            orderItemsToSave.add(orderItem);

            stockReductions.add(new StockReductionItem(itemReq.getProductId(), itemReq.getQuantity()));
        }

        // 2. Perform transactional stock reduction via Feign call
        productClient.reduceStock(stockReductions);

        // 3. Save order and items
        Order order = new Order(customerId, totalOrderPrice, OrderStatus.COMPLETED, LocalDateTime.now(), request.getShippingAddress());
        for (OrderItem item : orderItemsToSave) {
            order.addOrderItem(item);
        }

        Order savedOrder = orderRepository.save(order);
        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
    }

    // Fallback method for Circuit Breaker
    public ResponseEntity<?> fallbackPlaceOrder(OrderRequest request, String userId, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Order cannot be processed because the product/inventory service is offline. (Circuit Breaker Active: " 
                        + t.getMessage() + ")");
    }

    @GetMapping("/customer")
    public ResponseEntity<List<Order>> getOrdersByCurrentCustomer(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long customerId = Long.parseLong(userId);
        return ResponseEntity.ok(orderRepository.findByCustomerId(customerId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderRepository.findByCustomerId(customerId));
    }
}

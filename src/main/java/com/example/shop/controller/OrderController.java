package com.example.shop.controller;

import com.example.shop.entity.Order;
import com.example.shop.entity.OrderItem;
import com.example.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        order.setCreatedAt(new Date());
        order.setStatus("PENDING");
        return orderService.save(order);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<String> confirmOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        if (!order.getStatus().equals("PENDING")) {
            return ResponseEntity.badRequest().body("Order is not in PENDING state");
        }

        double total = 0;
        for (OrderItem item : order.getItems()) {
            total = total + (item.getPrice() * item.getQuantity());
        }

        if (total <= 0) {
            return ResponseEntity.badRequest().body("Order total must be positive");
        }

        order.setStatus("CONFIRMED");
        orderService.save(order);

        return ResponseEntity.ok("Order confirmed");
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
    }
}

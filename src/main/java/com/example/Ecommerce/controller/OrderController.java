package com.example.Ecommerce.controller;

import com.example.Ecommerce.exception.CartNotFoundException;
import com.example.Ecommerce.exception.DiscountCodeAlreadyUsedException;
import com.example.Ecommerce.exception.EmptyCartException;
import com.example.Ecommerce.exception.InvalidDiscountCodeException;
import com.example.Ecommerce.models.Order;
import com.example.Ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam Long cartId) {
        Order order = orderService.createOrder(cartId);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestParam Long cartId, @RequestParam(required = false) String discountCode) {
        try {
            Order order = orderService.checkout(cartId, discountCode);
            return ResponseEntity.ok(order);
        } catch (CartNotFoundException | EmptyCartException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (InvalidDiscountCodeException | DiscountCodeAlreadyUsedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

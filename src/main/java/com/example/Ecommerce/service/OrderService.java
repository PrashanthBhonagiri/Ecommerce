package com.example.Ecommerce.service;


import com.example.Ecommerce.exception.ItemNotFoundException;
import com.example.Ecommerce.models.Cart;
import com.example.Ecommerce.models.Item;
import com.example.Ecommerce.models.Order;
import com.example.Ecommerce.repository.CartRepository;
import com.example.Ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    public Order createOrder(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ItemNotFoundException("Cart not found"));

        Order order = new Order();
        for (Item item : cart.getItems()) {
            order.addItem(item);
        }
        order.setTotalAmount(cart.getTotalAmount());
        order.setOrderDate(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Clear the cart after creating the order
        cart.getItems().clear();
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);
        return savedOrder;
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Order not found"));
    }
}

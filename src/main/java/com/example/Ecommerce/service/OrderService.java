package com.example.Ecommerce.service;


import com.example.Ecommerce.exception.*;
import com.example.Ecommerce.models.Cart;
import com.example.Ecommerce.models.Item;
import com.example.Ecommerce.models.Order;
import com.example.Ecommerce.repository.CartRepository;
import com.example.Ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final DiscountService discountService;

    @Autowired
    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, DiscountService  discountService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.discountService = discountService;
    }

    @Transactional
    public Order createOrder(Long cartId) throws CartNotFoundException {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + cartId));

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

    @Transactional
    public Order checkout(Long cartId, String discountCode) throws CartNotFoundException, InvalidDiscountCodeException, DiscountCodeAlreadyUsedException {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + cartId));

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cannot checkout with an empty cart");
        }
        Order order = new Order();
        order.setItems(new HashSet<>(cart.getItems()));
        order.setTotalAmount(cart.getTotalAmount());

        if (discountCode != null && !discountCode.isEmpty()) {
            try {
                discountService.applyDiscount(order, discountCode);
            } catch (InvalidDiscountCodeException | DiscountCodeAlreadyUsedException e) {
                System.out.println("Error applying discount code: " + e.getMessage());
            }
        }
        order = orderRepository.save(order);

        // Clear the cart
        cart.getItems().clear();
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);

        return order;

    }
}

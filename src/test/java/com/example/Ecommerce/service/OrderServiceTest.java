package com.example.Ecommerce.service;


import com.example.Ecommerce.config.EcommerceConfig;
import com.example.Ecommerce.exception.*;
import com.example.Ecommerce.models.Cart;
import com.example.Ecommerce.models.Item;
import com.example.Ecommerce.models.Order;
import com.example.Ecommerce.repository.CartRepository;
import com.example.Ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private DiscountService discountService;
    @Mock
    private EcommerceConfig ecommerceConfig;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_withValidCart_shouldCreateOrder() throws CartNotFoundException {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(10.0);

        Long cartId = 1L;
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setItems(new ArrayList<>());
        cart.getItems().add(item);
        cart.setTotalAmount(10.0);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order createdOrder = orderService.createOrder(cartId);

        assertNotNull(createdOrder);
        assertEquals(1, createdOrder.getItems().size());
        assertEquals(10.0, createdOrder.getTotalAmount());
        verify(cartRepository, times(1)).findById(cartId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartRepository, times(1)).save(cart);
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, cart.getTotalAmount());
    }

    @Test
    void createOrder_withNonExistentCart_shouldThrowException() {
        Long cartId = 1L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> orderService.createOrder(cartId));
        verify(cartRepository, times(1)).findById(cartId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrder_withExistingOrder_shouldReturnOrder() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrder(orderId);

        assertNotNull(foundOrder);
        assertEquals(orderId, foundOrder.getId());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void getOrder_withNonExistentOrder_shouldThrowException() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> orderService.getOrder(orderId));
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void checkout_withValidCartAndNoDiscount_shouldCreateOrder() throws CartNotFoundException, InvalidDiscountCodeException, DiscountCodeAlreadyUsedException {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(10.0);

        Long cartId = 1L;
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setItems(new ArrayList<>());
        cart.getItems().add(item);
        cart.setTotalAmount(10.0);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.count()).thenReturn(4L);
        when(ecommerceConfig.getNthOrderDiscount()).thenReturn(5);

        Order createdOrder = orderService.checkout(cartId, null);

        assertNotNull(createdOrder);
        assertEquals(1, createdOrder.getItems().size());
        assertEquals(10.0, createdOrder.getTotalAmount());
        verify(cartRepository, times(1)).findById(cartId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartRepository, times(1)).save(cart);
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, cart.getTotalAmount());
        verify(discountService, never()).applyDiscount(any(), any());
    }

    @Test
    void checkout_withValidCartAndDiscount_shouldApplyDiscount() throws CartNotFoundException, InvalidDiscountCodeException, DiscountCodeAlreadyUsedException {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(10.0);

        Long cartId = 1L;
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setItems(new ArrayList<>());
        cart.getItems().add(item);
        cart.setTotalAmount(10.0);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.count()).thenReturn(4L);
        when(ecommerceConfig.getNthOrderDiscount()).thenReturn(5);

        Order createdOrder = orderService.checkout(cartId, "DISCOUNT");

        assertNotNull(createdOrder);
        assertEquals(1, createdOrder.getItems().size());
        assertEquals(10.0, createdOrder.getTotalAmount());
        verify(cartRepository, times(1)).findById(cartId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartRepository, times(1)).save(cart);
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, cart.getTotalAmount());
        verify(discountService, times(1)).applyDiscount(any(), eq("DISCOUNT"));
    }

    @Test
    void checkout_withEmptyCart_shouldThrowException() {
        Long cartId = 1L;
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setItems(new ArrayList<>());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        assertThrows(EmptyCartException.class, () -> orderService.checkout(cartId, null));
        verify(cartRepository, times(1)).findById(cartId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void checkout_withNonExistentCart_shouldThrowException() {
        Long cartId = 1L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> orderService.checkout(cartId, null));
        verify(cartRepository, times(1)).findById(cartId);
        verify(orderRepository, never()).save(any(Order.class));
    }
}

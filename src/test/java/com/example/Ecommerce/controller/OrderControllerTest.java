package com.example.Ecommerce.controller;

import com.example.Ecommerce.exception.CartNotFoundException;
import com.example.Ecommerce.exception.DiscountCodeAlreadyUsedException;
import com.example.Ecommerce.exception.EmptyCartException;
import com.example.Ecommerce.exception.InvalidDiscountCodeException;
import com.example.Ecommerce.models.Order;
import com.example.Ecommerce.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void createOrder_shouldReturnCreatedOrder() throws Exception {
        Long cartId = 1L;
        Order order = new Order();
        order.setId(1L);

        when(orderService.createOrder(cartId)).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                        .param("cartId", cartId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).createOrder(cartId);
    }

    @Test
    void getOrder_shouldReturnOrder() throws Exception {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        when(orderService.getOrder(orderId)).thenReturn(order);

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId));

        verify(orderService, times(1)).getOrder(orderId);
    }

    @Test
    void checkout_shouldReturnOrder() throws Exception {
        Long cartId = 1L;
        String discountCode = "DISCOUNT10";
        Order order = new Order();
        order.setId(1L);

        when(orderService.checkout(cartId, discountCode)).thenReturn(order);

        mockMvc.perform(post("/api/orders/checkout")
                        .param("cartId", cartId.toString())
                        .param("discountCode", discountCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).checkout(cartId, discountCode);
    }

    @Test
    void checkout_withCartNotFoundException_shouldReturnBadRequest() throws Exception {
        Long cartId = 1L;
        String discountCode = "DISCOUNT10";

        when(orderService.checkout(cartId, discountCode)).thenThrow(new CartNotFoundException("Cart not found"));

        mockMvc.perform(post("/api/orders/checkout")
                        .param("cartId", cartId.toString())
                        .param("discountCode", discountCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cart not found"));

        verify(orderService, times(1)).checkout(cartId, discountCode);
    }

    @Test
    void checkout_withEmptyCartException_shouldReturnBadRequest() throws Exception {
        Long cartId = 1L;
        String discountCode = "DISCOUNT10";

        when(orderService.checkout(cartId, discountCode)).thenThrow(new EmptyCartException("Cart is empty"));

        mockMvc.perform(post("/api/orders/checkout")
                        .param("cartId", cartId.toString())
                        .param("discountCode", discountCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cart is empty"));

        verify(orderService, times(1)).checkout(cartId, discountCode);
    }

    @Test
    void checkout_withInvalidDiscountCodeException_shouldReturnBadRequest() throws Exception {
        Long cartId = 1L;
        String discountCode = "INVALID10";

        when(orderService.checkout(cartId, discountCode)).thenThrow(new InvalidDiscountCodeException("Invalid discount code"));

        mockMvc.perform(post("/api/orders/checkout")
                        .param("cartId", cartId.toString())
                        .param("discountCode", discountCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid discount code"));

        verify(orderService, times(1)).checkout(cartId, discountCode);
    }

    @Test
    void checkout_withDiscountCodeAlreadyUsedException_shouldReturnBadRequest() throws Exception {
        Long cartId = 1L;
        String discountCode = "USED10";

        when(orderService.checkout(cartId, discountCode)).thenThrow(new DiscountCodeAlreadyUsedException("Discount code already used"));

        mockMvc.perform(post("/api/orders/checkout")
                        .param("cartId", cartId.toString())
                        .param("discountCode", discountCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Discount code already used"));

        verify(orderService, times(1)).checkout(cartId, discountCode);
    }
}

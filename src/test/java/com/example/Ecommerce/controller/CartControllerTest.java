package com.example.Ecommerce.controller;

import com.example.Ecommerce.exception.CartNotFoundException;
import com.example.Ecommerce.exception.ItemNotFoundException;
import com.example.Ecommerce.models.Cart;
import com.example.Ecommerce.service.CartService;
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

public class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    void createCart_shouldReturnCreatedCart() throws Exception {
        Cart newCart = new Cart();
        newCart.setId(1L);

        when(cartService.createCart()).thenReturn(newCart);

        mockMvc.perform(post("/api/carts"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(cartService, times(1)).createCart();
    }

    @Test
    void addItemToCart_shouldReturnUpdatedCart() throws Exception {
        Long cartId = 1L;
        Long itemId = 2L;
        Cart updatedCart = new Cart();
        updatedCart.setId(cartId);

        when(cartService.addItemToCart(cartId, itemId)).thenReturn(updatedCart);

        mockMvc.perform(post("/api/carts/{cartId}/items", cartId)
                        .param("itemId", itemId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartId));

        verify(cartService, times(1)).addItemToCart(cartId, itemId);
    }

    @Test
    void addItemToCart_withCartNotFound_shouldReturnNotFound() throws Exception {
        Long cartId = 1L;
        Long itemId = 2L;

        when(cartService.addItemToCart(cartId, itemId)).thenThrow(new CartNotFoundException("Cart not found"));

        mockMvc.perform(post("/api/carts/{cartId}/items", cartId)
                        .param("itemId", itemId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(cartService, times(1)).addItemToCart(cartId, itemId);
    }

    @Test
    void addItemToCart_withItemNotFound_shouldReturnNotFound() throws Exception {
        Long cartId = 1L;
        Long itemId = 2L;

        when(cartService.addItemToCart(cartId, itemId)).thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(post("/api/carts/{cartId}/items", cartId)
                        .param("itemId", itemId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(cartService, times(1)).addItemToCart(cartId, itemId);
    }

    @Test
    void removeItemFromCart_shouldReturnUpdatedCart() throws Exception {
        Long cartId = 1L;
        Long itemId = 2L;
        Cart updatedCart = new Cart();
        updatedCart.setId(cartId);

        when(cartService.removeItemFromCart(cartId, itemId)).thenReturn(updatedCart);

        mockMvc.perform(delete("/api/carts/{cartId}/items", cartId)
                        .param("itemId", itemId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartId));

        verify(cartService, times(1)).removeItemFromCart(cartId, itemId);
    }

    @Test
    void removeItemFromCart_withCartNotFound_shouldReturnNotFound() throws Exception {
        Long cartId = 1L;
        Long itemId = 2L;

        when(cartService.removeItemFromCart(cartId, itemId)).thenThrow(new CartNotFoundException("Cart not found"));

        mockMvc.perform(delete("/api/carts/{cartId}/items", cartId)
                        .param("itemId", itemId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(cartService, times(1)).removeItemFromCart(cartId, itemId);
    }

    @Test
    void removeItemFromCart_withItemNotFound_shouldReturnNotFound() throws Exception {
        Long cartId = 1L;
        Long itemId = 2L;

        when(cartService.removeItemFromCart(cartId, itemId)).thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(delete("/api/carts/{cartId}/items", cartId)
                        .param("itemId", itemId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(cartService, times(1)).removeItemFromCart(cartId, itemId);
    }

    @Test
    void getCart_shouldReturnCart() throws Exception {
        Long cartId = 1L;
        Cart cart = new Cart();
        cart.setId(cartId);

        when(cartService.getCart(cartId)).thenReturn(cart);

        mockMvc.perform(get("/api/carts/{cartId}", cartId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartId));

        verify(cartService, times(1)).getCart(cartId);
    }

    @Test
    void getCart_withCartNotFound_shouldReturnNotFound() throws Exception {
        Long cartId = 1L;

        when(cartService.getCart(cartId)).thenThrow(new CartNotFoundException("Cart not found"));

        mockMvc.perform(get("/api/carts/{cartId}", cartId))
                .andExpect(status().isNotFound());

        verify(cartService, times(1)).getCart(cartId);
    }
}

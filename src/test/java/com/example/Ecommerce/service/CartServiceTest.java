package com.example.Ecommerce.service;

import com.example.Ecommerce.models.Cart;
import com.example.Ecommerce.models.Item;
import com.example.Ecommerce.repository.CartRepository;
import com.example.Ecommerce.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCart_shouldSaveAndReturnNewCart() {
        Cart newCart = new Cart();
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        Cart createdCart = cartService.createCart();

        assertNotNull(createdCart);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addItemToCart_shouldAddItemAndUpdateTotal() {
        Long cartId = 1L;
        Long itemId = 1L;
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setItems(new ArrayList<>());
        cart.setTotalAmount(0.0);

        Item item = new Item();
        item.setId(itemId);
        item.setPrice(10.0);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart updatedCart = cartService.addItemToCart(cartId, itemId);

        assertNotNull(updatedCart);
        assertEquals(1, updatedCart.getItems().size());
        assertEquals(10.0, updatedCart.getTotalAmount());
        verify(cartRepository, times(1)).findById(cartId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void addItemToCart_withNonExistentCart_shouldThrowException() {
        Long cartId = 1L;
        Long itemId = 1L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cartService.addItemToCart(cartId, itemId));
        verify(cartRepository, times(1)).findById(cartId);
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void addItemToCart_withNonExistentItem_shouldThrowException() {
        Long cartId = 1L;
        Long itemId = 1L;
        Cart cart = new Cart();
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cartService.addItemToCart(cartId, itemId));
        verify(cartRepository, times(1)).findById(cartId);
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void removeItemFromCart_shouldRemoveItemAndUpdateTotal() {
        Long cartId = 1L;
        Long itemId = 1L;
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setItems(new ArrayList<>());
        cart.setTotalAmount(10.0);

        Item item = new Item();
        item.setId(itemId);
        item.setPrice(10.0);
        cart.getItems().add(item);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart updatedCart = cartService.removeItemFromCart(cartId, itemId);

        assertNotNull(updatedCart);
        assertEquals(0, updatedCart.getItems().size());
        assertEquals(0.0, updatedCart.getTotalAmount());
        verify(cartRepository, times(1)).findById(cartId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void removeItemFromCart_withNonExistentCart_shouldThrowException() {
        Long cartId = 1L;
        Long itemId = 1L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cartService.removeItemFromCart(cartId, itemId));
        verify(cartRepository, times(1)).findById(cartId);
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void removeItemFromCart_withNonExistentItem_shouldThrowException() {
        Long cartId = 1L;
        Long itemId = 1L;
        Cart cart = new Cart();
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cartService.removeItemFromCart(cartId, itemId));
        verify(cartRepository, times(1)).findById(cartId);
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void getCart_withExistingCart_shouldReturnCart() {
        Long cartId = 1L;
        Cart cart = new Cart();
        cart.setId(cartId);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        Cart foundCart = cartService.getCart(cartId);

        assertNotNull(foundCart);
        assertEquals(cartId, foundCart.getId());
        verify(cartRepository, times(1)).findById(cartId);
    }

    @Test
    void getCart_withNonExistentCart_shouldThrowException() {
        Long cartId = 1L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cartService.getCart(cartId));
        verify(cartRepository, times(1)).findById(cartId);
    }
}

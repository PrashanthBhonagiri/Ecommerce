package com.example.Ecommerce.service;

import com.example.Ecommerce.models.Cart;
import com.example.Ecommerce.repository.CartRepository;
import com.example.Ecommerce.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public CartService(CartRepository cartRepository, ItemRepository itemRepository) {
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public Cart createCart() {
        Cart cart = new Cart();
        System.out.println("Cart created with id : " + cart.getId());
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart addItemToCart(Long cartId, Long itemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));

        itemRepository.findById(itemId)
                .map(item -> {
                    cart.getItems().add(item);
                    cart.setTotalAmount(cart.getTotalAmount() + item.getPrice());
                    return item;
                })
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItemFromCart(Long cartId, Long itemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));

        itemRepository.findById(itemId)
                .map(item -> {
                    cart.getItems().remove(item);
                    cart.setTotalAmount(cart.getTotalAmount() - item.getPrice());
                    return item;
                })
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));
    }

}

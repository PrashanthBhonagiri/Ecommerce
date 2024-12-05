package com.example.Ecommerce.service;

import com.example.Ecommerce.exception.ItemNotFoundException;
import com.example.Ecommerce.models.Item;
import com.example.Ecommerce.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    public Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
    }

    public List<Item> getAllItems() {
        System.out.println("From Item Service ");
        return itemRepository.findAll();
    }

}

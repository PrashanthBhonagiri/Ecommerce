package com.example.Ecommerce.service;

import com.example.Ecommerce.exception.ItemNotFoundException;
import com.example.Ecommerce.models.Item;
import com.example.Ecommerce.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createItem_shouldSaveAndReturnItem() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(10.0);

        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item savedItem = itemService.createItem(item);

        assertNotNull(savedItem);
        assertEquals("Test Item", savedItem.getName());
        assertEquals("Test Description", savedItem.getDescription());
        assertEquals(10.0, savedItem.getPrice());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void getItem_withValidId_shouldReturnItem() {
        Long id = 1L;
        Item item = new Item();
        item.setId(id);
        item.setName("Test Item");

        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        Item foundItem = itemService.getItem(id);

        assertNotNull(foundItem);
        assertEquals(id, foundItem.getId());
        assertEquals("Test Item", foundItem.getName());
        verify(itemRepository, times(1)).findById(id);
    }

    @Test
    void getItem_withInvalidId_shouldThrowItemNotFoundException() {
        Long id = 1L;
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItem(id));
        verify(itemRepository, times(1)).findById(id);
    }

    @Test
    void getAllItems_shouldReturnListOfItems() {
        Item item1 = new Item();
        item1.setName("Item 1");
        Item item2 = new Item();
        item2.setName("Item 2");

        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        List<Item> items = itemService.getAllItems();

        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals("Item 1", items.get(0).getName());
        assertEquals("Item 2", items.get(1).getName());
        verify(itemRepository, times(1)).findAll();

    }

    @Test
    void getAllItems_whenNoItems_shouldReturnEmptyList() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList());

        List<Item> foundItems = itemService.getAllItems();

        assertNotNull(foundItems);
        assertTrue(foundItems.isEmpty());
        verify(itemRepository, times(1)).findAll();
    }
}

package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    @Test
    void getItems() {
        List<Item> items = Collections.emptyList();
        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> actualItems = itemController.getItems();

        ResponseEntity<List<Item>> expectedItems = ResponseEntity.ok().body(items);

        assertEquals(expectedItems, actualItems);
    }

    @Test
    void getItemByIdUnhappyPath() {
        long id = 10L;
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Item> actualResponse = itemController.getItemById(id);

        ResponseEntity<Item> expectedResponse = ResponseEntity.notFound().build();

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getItemByIdHappyPath() {
        long id = 10L;
        Item item = mock(Item.class);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        ResponseEntity<Item> actualResponse = itemController.getItemById(id);

        ResponseEntity<Item> expectedResponse = ResponseEntity.ok(item);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getItemsByNameUnhappyPath() {
        String name = "name";
        when(itemRepository.findByName(name)).thenReturn(null);

        ResponseEntity<List<Item>> actualResponse = itemController.getItemsByName(name);

        ResponseEntity<List<Item>> expectedResponse = ResponseEntity.notFound().build();

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getItemsByNameUnhappyPathEmptyList() {
        String name = "name";
        when(itemRepository.findByName(name)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Item>> actualResponse = itemController.getItemsByName(name);

        ResponseEntity<List<Item>> expectedResponse = ResponseEntity.notFound().build();

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getItemsByNameHappyPath() {
        String name = "name";
        List<Item> items = Collections.singletonList(mock(Item.class));
        when(itemRepository.findByName(name)).thenReturn(items);

        ResponseEntity<List<Item>> actualResponse = itemController.getItemsByName(name);

        ResponseEntity<List<Item>> expectedResponse = ResponseEntity.ok(items);

        assertEquals(expectedResponse, actualResponse);
    }
}
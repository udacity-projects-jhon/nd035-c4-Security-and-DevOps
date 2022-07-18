package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private static final Random RANDOM = new Random();
    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartController cartController;

    @Test
    void addTocartUnhappyPath1() {
        ModifyCartRequest modifyCartRequest = ModifyCartRequest.builder()
                .username("sneeze")
                .itemId(RANDOM.nextLong())
                .quantity(10)
                .build();

        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(null);

        ResponseEntity<Cart> actualResponse = cartController.addTocart(modifyCartRequest);

        ResponseEntity<Cart> expectedResponse = ResponseEntity.notFound().build();

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void addToCartUnhappyPath2() {
        ModifyCartRequest modifyCartRequest = ModifyCartRequest.builder()
                .username("sneeze")
                .itemId(RANDOM.nextLong())
                .quantity(10)
                .build();
        User user = new User();
        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(user);
        when(itemRepository.findById(modifyCartRequest.getItemId())).thenReturn(Optional.empty());

        ResponseEntity<Cart> actualResponse = cartController.addTocart(modifyCartRequest);

        ResponseEntity<Cart> expectedResponse = ResponseEntity.notFound().build();

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void addToCartHappyPath() {
        ModifyCartRequest modifyCartRequest = ModifyCartRequest.builder()
                .username("sneeze")
                .itemId(RANDOM.nextLong())
                .quantity(10)
                .build();
        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);
        Item item = new Item();
        item.setPrice(BigDecimal.TEN);
        Optional<Item> optionalItem = Optional.of(item);
        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(user);
        when(itemRepository.findById(modifyCartRequest.getItemId())).thenReturn(optionalItem);

        ResponseEntity<Cart> actualResponse = cartController.addTocart(modifyCartRequest);

        Cart expectedCard = new Cart();
        expectedCard.setTotal(BigDecimal.valueOf(100));
        expectedCard.setItems(IntStream.range(0, 10).mapToObj(ignored -> item).collect(Collectors.toList()));

        ResponseEntity<Cart> expectedResponse = ResponseEntity.ok(expectedCard);

        assertEquals(expectedResponse, actualResponse);
        verify(cartRepository).save(any());
    }

    @Test
    void removeFromcartUnhappyPath1() {
        ModifyCartRequest modifyCartRequest = ModifyCartRequest.builder()
                .username("sneeze")
                .itemId(RANDOM.nextLong())
                .quantity(10)
                .build();

        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(null);

        ResponseEntity<Cart> actualResponse = cartController.removeFromcart(modifyCartRequest);

        ResponseEntity<Cart> expectedResponse = ResponseEntity.notFound().build();

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void removeFromcartUnhappyPath2() {
        ModifyCartRequest modifyCartRequest = ModifyCartRequest.builder()
                .username("sneeze")
                .itemId(RANDOM.nextLong())
                .quantity(10)
                .build();
        User user = new User();
        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(user);
        when(itemRepository.findById(modifyCartRequest.getItemId())).thenReturn(Optional.empty());

        ResponseEntity<Cart> actualResponse = cartController.removeFromcart(modifyCartRequest);

        ResponseEntity<Cart> expectedResponse = ResponseEntity.notFound().build();

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void removeFromcartHappyPath() {
        ModifyCartRequest modifyCartRequest = ModifyCartRequest.builder()
                .username("sneeze")
                .itemId(RANDOM.nextLong())
                .quantity(5)
                .build();

        User user = new User();
        Cart cart = new Cart();
        Item item = new Item();
        item.setPrice(BigDecimal.TEN);
        cart.setTotal(BigDecimal.valueOf(100));
        cart.setItems(IntStream.range(0, 10).mapToObj(ignored -> item).collect(Collectors.toList()));
        user.setCart(cart);

        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(user);
        when(itemRepository.findById(modifyCartRequest.getItemId())).thenReturn(Optional.of(item));


        ResponseEntity<Cart> actualResponse = cartController.removeFromcart(modifyCartRequest);

        Cart expectedCard = new Cart();
        expectedCard.setTotal(BigDecimal.valueOf(50));
        expectedCard.setItems(IntStream.range(0, 5).mapToObj(ignored -> item).collect(Collectors.toList()));

        ResponseEntity<Cart> expectedResponse = ResponseEntity.ok(expectedCard);

        assertEquals(expectedResponse, actualResponse);
        verify(cartRepository).save(any());
    }
}
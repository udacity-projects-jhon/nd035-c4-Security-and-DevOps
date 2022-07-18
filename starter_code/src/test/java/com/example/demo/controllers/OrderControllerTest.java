package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;


    @Test
    void submitUnhappyPath() {
        String userName = "Rogue";
        when(userRepository.findByUsername(userName)).thenReturn(null);

        ResponseEntity<UserOrder> actualResponse = orderController.submit(userName);

        ResponseEntity<UserOrder> expectedResponse = ResponseEntity.notFound().build();

        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    void submitHappyPath() {
        String userName = "Rogue";
        User user = new User();
        Cart cart = new Cart();
        cart.setTotal(BigDecimal.TEN);
        cart.setItems(Collections.emptyList());
        cart.setUser(user);

        user.setCart(cart);

        when(userRepository.findByUsername(userName)).thenReturn(user);

        ResponseEntity<UserOrder> actualResponse = orderController.submit(userName);

        UserOrder userOrder = new UserOrder();
        userOrder.setTotal(BigDecimal.TEN);
        userOrder.setItems(Collections.emptyList());
        userOrder.setUser(user);

        ResponseEntity<UserOrder> expectedResponse = ResponseEntity.ok().body(userOrder);

        assertEquals(userOrder, expectedResponse.getBody());
        verify(orderRepository).save(userOrder);
    }

    @Test
    void getOrdersForUserUnhappyPath() {
        String userName = "Rogue";
        when(userRepository.findByUsername(userName)).thenReturn(null);

        ResponseEntity<List<UserOrder>> actualResponse = orderController.getOrdersForUser(userName);

        ResponseEntity<List<UserOrder>> expectedResponse = ResponseEntity.notFound().build();

        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    void getOrdersForUserHappyPath() {
        String userName = "Rogue";
        User user = new User();

        UserOrder userOrder = new UserOrder();
        userOrder.setTotal(BigDecimal.TEN);
        userOrder.setItems(Collections.emptyList());
        userOrder.setUser(user);

        List<UserOrder> userOrders = Collections.singletonList(userOrder);

        when(userRepository.findByUsername(userName)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);


        ResponseEntity<List<UserOrder>> actualResponse = orderController.getOrdersForUser(userName);

        ResponseEntity<List<UserOrder>> expectedResponse = ResponseEntity.ok().body(userOrders);

        assertEquals(actualResponse, expectedResponse);
    }
}
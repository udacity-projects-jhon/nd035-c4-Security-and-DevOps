package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserController userController;

    @Test
    void findByIdUnHappyPath() {
        long id = 10L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<User> actualResponse = userController.findById(id);

        ResponseEntity<User> expectedResponse = ResponseEntity.notFound().build();

        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    void findByIdHappyPath() {
        long id = 10L;
        Optional<User> user = Optional.of(mock(User.class));
        when(userRepository.findById(id)).thenReturn(user);

        ResponseEntity<User> actualResponse = userController.findById(id);

        ResponseEntity<User> expectedResponse = ResponseEntity.ok().body(user.get());

        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    void findByUserNameUnhappyPath() {
        String userName = "Rogue";
        when(userRepository.findByUsername(userName)).thenReturn(null);

        ResponseEntity<User> actualResponse = userController.findByUserName(userName);

        ResponseEntity<User> expectedResponse = ResponseEntity.notFound().build();

        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    void findByUserNameHappyPath() {
        String userName = "Rogue";
        User user = mock(User.class);
        when(userRepository.findByUsername(userName)).thenReturn(user);

        ResponseEntity<User> actualResponse = userController.findByUserName(userName);

        ResponseEntity<User> expectedResponse = ResponseEntity.ok().body(user);

        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    void createUserUnHappyPathShortPassword() {
        CreateUserRequest shortPassWord = CreateUserRequest.builder()
                .username("Rogue")
                .password("123")
                .confirmPassword("123")
                .build();

        ResponseEntity<User> actualResponse = userController.createUser(shortPassWord);

        ResponseEntity<User> expectedResponse = ResponseEntity.badRequest().build();

        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    void createUserUnHappyDiffPassword() {
        CreateUserRequest shortPassWord = CreateUserRequest.builder()
                .username("Rogue")
                .password("Pr99^dP1k!2n")
                .confirmPassword("g01W3@*XIpPn")
                .build();

        ResponseEntity<User> actualResponse = userController.createUser(shortPassWord);

        ResponseEntity<User> expectedResponse = ResponseEntity.badRequest().build();

        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    void createUserUnHappyHappyPath() {
        String password = "g01W3@*XIpPn";
        String encryptedPassword = "encryptedPassword";
        CreateUserRequest inputUser = CreateUserRequest.builder()
                .username("Rogue")
                .password(password)
                .confirmPassword(password)
                .build();

        when(bCryptPasswordEncoder.encode(password)).thenReturn(encryptedPassword);

        ResponseEntity<User> actualUser = userController.createUser(inputUser);

        User expectedUser = new User();
        expectedUser.setUsername(inputUser.getUsername());
        expectedUser.setPassword(encryptedPassword);
        expectedUser.setCart(new Cart());

        assertEquals(HttpStatus.OK, actualUser.getStatusCode());
        assertEquals(actualUser.getBody(), expectedUser);

        verify(cartRepository).save(any(Cart.class));
        verify(userRepository).save(any(User.class));
    }
}
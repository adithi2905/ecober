package com.ecober.domain.service;

import com.ecober.domain.model.User;
import com.ecober.infrastructure.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserLoginServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private HttpSession session;
    private UserLoginService userLoginService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        session = mock(HttpSession.class);
        userLoginService = new UserLoginService();

        inject(userLoginService, "userRepository", userRepository);
        inject(userLoginService, "passwordEncoder", passwordEncoder);
    }

    private void inject(Object target, String field, Object value) {
        try {
            var f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSuccessfulLogin() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashed");
        user.setUserId(UUID.randomUUID());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain", "hashed")).thenReturn(true);

        String result = userLoginService.login("testuser", "plain", session);

        assertEquals("SUCCESS", result);
        verify(session).setAttribute(eq("riderId"), eq(user.getUserId()));
    }

    @Test
    void testInvalidPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashed");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        String result = userLoginService.login("testuser", "wrong", session);
        assertEquals("Invalid password", result);
    }

    @Test
    void testInvalidUsername() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        String result = userLoginService.login("unknown", "any", session);
        assertEquals("Invalid username", result);
    }
}

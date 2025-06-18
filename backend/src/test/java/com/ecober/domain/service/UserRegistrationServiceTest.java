package com.ecober.domain.service;

import com.ecober.domain.model.User;
import com.ecober.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRegistrationServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserRegistrationService userRegistrationService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userRegistrationService = new UserRegistrationService();

        inject(userRegistrationService, "userRepository", userRepository);
        inject(userRegistrationService, "passwordEncoder", passwordEncoder);
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
    void testCreateUser_Success() {
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("plain-password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain-password")).thenReturn("hashed-password");

        userRegistrationService.createUser(newUser);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User saved = userCaptor.getValue();
        assertEquals("hashed-password", saved.getPassword());
        assertEquals("RIDER", saved.getRole());
    }

    @Test
    void testCreateUser_AlreadyExists() {
        User existing = new User();
        existing.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existing));

        User duplicate = new User();
        duplicate.setUsername("testuser");
        duplicate.setPassword("some-password");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userRegistrationService.createUser(duplicate));

        assertTrue(ex.getMessage().contains("User already exists"));
        verify(userRepository, never()).save(any());
    }
}

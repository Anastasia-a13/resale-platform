package ru.skypro.homework.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.auth.Register;
import ru.skypro.homework.model.user.Role;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.AuthServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private ru.skypro.homework.model.user.User createUser(String email, String encodedPassword) {
        ru.skypro.homework.model.user.User user = new ru.skypro.homework.model.user.User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(Role.USER);
        return user;
    }

    @Test
    void loginSuccess() {
        ru.skypro.homework.model.user.User user = createUser("test@mail.com", "encoded");
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);

        boolean result = authService.login("test@mail.com", "password");

        assertTrue(result);
        verify(userRepository).findByEmail("test@mail.com");
    }

    @Test
    void loginWrongPassword() {
        ru.skypro.homework.model.user.User user = createUser("test@mail.com", "encoded");
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        boolean result = authService.login("test@mail.com", "wrong");

        assertFalse(result);
    }

    @Test
    void loginNotFound() {
        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());

        boolean result = authService.login("unknown@mail.com", "password");

        assertFalse(result);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void registerSuccess() {
        Register register = new Register("new@mail.com", "Password1", "Иван", "Иванов", "+7 (999) 123-45-67", "USER");
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password1")).thenReturn("encoded");
        when(userRepository.save(any(ru.skypro.homework.model.user.User.class))).thenAnswer(inv -> inv.getArgument(0));

        boolean result = authService.register(register);

        assertTrue(result);
        verify(userRepository).save(any(ru.skypro.homework.model.user.User.class));
    }

    @Test
    void registerDuplicate() {
        Register register = new Register("exist@mail.com", "Password1", "Иван", "Иванов", "+7 (999) 123-45-67", "USER");
        ru.skypro.homework.model.user.User existing = createUser("exist@mail.com", "encoded");
        when(userRepository.findByEmail("exist@mail.com")).thenReturn(Optional.of(existing));

        boolean result = authService.register(register);

        assertFalse(result);
        verify(userRepository, never()).save(any(ru.skypro.homework.model.user.User.class));
    }
}

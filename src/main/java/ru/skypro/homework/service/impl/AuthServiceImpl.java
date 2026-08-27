package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.auth.Register;
import ru.skypro.homework.model.user.Role;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

/**
 * Реализация сервиса аутентификации и регистрации пользователей.
 * Предоставляет методы для входа в систему и создания нового аккаунта.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public boolean login(String userName, String password) {
        boolean result = userRepository.findByEmail(userName)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
        if (result) {
            log.info("Успешный вход: {}", userName);
        } else {
            log.warn("Неудачная попытка входа: {}", userName);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean register(Register register) {
        if (userRepository.findByEmail(register.username()).isPresent()) {
            return false;
        }
        User user = User.builder()
                .email(register.username())
                .password(passwordEncoder.encode(register.password()))
                .firstName(register.firstName())
                .lastName(register.lastName())
                .phone(register.phone())
                .role(Role.valueOf(register.role()))
                .build();
        userRepository.save(user);
        log.info("Зарегистрирован новый пользователь: {}", register.username());
        return true;
    }
}

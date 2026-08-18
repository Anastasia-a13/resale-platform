package ru.skypro.homework.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.auth.Register;
import ru.skypro.homework.model.user.Role;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.encoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean login(String userName, String password) {
        return userRepository.findByEmail(userName)
                .map(user -> encoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    @Override
    @Transactional
    public boolean register(Register register) {
        if (userRepository.findByEmail(register.username()).isPresent()) {
            return false;
        }
        User user = User.builder()
                .email(register.username())
                .password(encoder.encode(register.password()))
                .firstName(register.firstName())
                .lastName(register.lastName())
                .phone(register.phone())
                .role(Role.valueOf(register.role()))
                .build();
        userRepository.save(user);
        return true;
    }
}

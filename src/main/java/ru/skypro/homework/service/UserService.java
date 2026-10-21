package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.user.NewPasswordDto;
import ru.skypro.homework.dto.user.UpdateUserDto;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.exception.BadRequestException;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.UserRepository;

/**
 * Сервис для управления профилем пользователя.
 * Предоставляет операции получения, обновления данных,
 * загрузки аватара и смены пароля.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.USER_NOT_FOUND));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(UserDetails userDetails, UpdateUserDto updateUserDto) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.USER_NOT_FOUND));
        userMapper.updateUser(updateUserDto, user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void uploadAvatar(UserDetails userDetails, MultipartFile image) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.USER_NOT_FOUND));
        String fileName = user.getImage() != null
                ? imageService.updateAvatar(user.getImage(), image).fileName()
                : imageService.saveAvatar(image);
        user.setImage(fileName);
        userRepository.save(user);
        log.info("Загружен аватар для пользователя={}, размер={} байт", user.getEmail(), image.getSize());
    }

    @Transactional
    public void changePassword(UserDetails userDetails, NewPasswordDto newPasswordDto) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.USER_NOT_FOUND));
        if (!passwordEncoder.matches(newPasswordDto.currentPassword(), user.getPassword())) {
            log.warn("Неверный текущий пароль: {}", user.getEmail());
            throw new BadRequestException(BadRequestException.WRONG_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(newPasswordDto.newPassword()));
        userRepository.save(user);
        log.info("Пароль успешно изменён: {}", user.getEmail());
    }
}

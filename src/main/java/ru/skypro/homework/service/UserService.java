package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
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
        user.setImage(imageService.saveImage(image));
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(UserDetails userDetails, NewPasswordDto newPasswordDto) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.USER_NOT_FOUND));
        if (!passwordEncoder.matches(newPasswordDto.currentPassword(), user.getPassword())) {
            throw new BadRequestException(BadRequestException.WRONG_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(newPasswordDto.newPassword()));
        userRepository.save(user);
    }
}

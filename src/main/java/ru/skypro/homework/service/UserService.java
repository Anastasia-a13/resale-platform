package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.user.NewPasswordDto;
import ru.skypro.homework.dto.user.UpdateUserDto;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getCurrentUser(UserDetails userDetails) {
    }

    public UserDto updateUser(UserDetails userDetails, UpdateUserDto updateUserDto) {
    }

    public void uploadAvatar(UserDetails userDetails, MultipartFile image) {
    }

    public void changePassword(UserDetails userDetails, NewPasswordDto newPasswordDto) {
    }
}

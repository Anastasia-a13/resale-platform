package ru.skypro.homework.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.user.NewPasswordDto;
import ru.skypro.homework.dto.user.UpdateUserDto;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.exception.BadRequestException;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.core.userdetails.User.withUsername;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private UserService userService;

    private UserDetails createUserDetails(String email) {
        return withUsername(email)
                .password("pass")
                .authorities("ROLE_USER")
                .build();
    }

    private ru.skypro.homework.model.user.User createUser(String email) {
        ru.skypro.homework.model.user.User user = new ru.skypro.homework.model.user.User();
        user.setId(1);
        user.setEmail(email);
        user.setFirstName("Иван");
        user.setLastName("Иванов");
        user.setPhone("+7 (999) 123-45-67");
        return user;
    }

    @Test
    void getCurrentUser() {
        ru.skypro.homework.model.user.User user = createUser("test@mail.com");
        UserDto dto = new UserDto(1, "test@mail.com", "Иван", "Иванов", "+79991234567", "USER", null);

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.getCurrentUser(createUserDetails("test@mail.com"));

        assertEquals("test@mail.com", result.email());
        assertEquals("Иван", result.firstName());
    }

    @Test
    void getCurrentUserNotFound() {
        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getCurrentUser(createUserDetails("unknown@mail.com")));
    }

    @Test
    void updateUser() {
        ru.skypro.homework.model.user.User user = createUser("test@mail.com");
        UpdateUserDto dto = new UpdateUserDto("Пётр", "Петров", "+7 (999) 000-00-00");
        UserDto updatedDto = new UserDto(1, "test@mail.com", "Пётр", "Петров", "+79990000000", "USER", null);

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(ru.skypro.homework.model.user.User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(updatedDto);

        UserDto result = userService.updateUser(createUserDetails("test@mail.com"), dto);

        assertEquals("Пётр", result.firstName());
        assertEquals("Петров", result.lastName());
    }

    @Test
    void changePasswordSuccess() {
        ru.skypro.homework.model.user.User user = createUser("test@mail.com");
        user.setPassword("encoded_old");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPass1", "encoded_old")).thenReturn(true);
        when(passwordEncoder.encode("NewPass1")).thenReturn("encoded_new");

        NewPasswordDto dto = new NewPasswordDto("OldPass1", "NewPass1");

        assertDoesNotThrow(() -> userService.changePassword(createUserDetails("test@mail.com"), dto));
        verify(userRepository).save(any(ru.skypro.homework.model.user.User.class));
    }

    @Test
    void changePasswordWrongCurrent() {
        ru.skypro.homework.model.user.User user = createUser("test@mail.com");
        user.setPassword("encoded_old");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass", "encoded_old")).thenReturn(false);

        NewPasswordDto dto = new NewPasswordDto("WrongPass", "NewPass1");

        assertThrows(BadRequestException.class,
                () -> userService.changePassword(createUserDetails("test@mail.com"), dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void uploadAvatarNew() {
        ru.skypro.homework.model.user.User user = createUser("test@mail.com");
        user.setImage(null);
        MockMultipartFile image = new MockMultipartFile("image", "avatar.jpg", "image/jpeg", new byte[]{1, 2, 3});

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(imageService.saveAvatar(image)).thenReturn("avatar.jpg");
        when(userRepository.save(any(ru.skypro.homework.model.user.User.class))).thenReturn(user);

        userService.uploadAvatar(createUserDetails("test@mail.com"), image);

        assertEquals("avatar.jpg", user.getImage());
        verify(imageService).saveAvatar(image);
        verify(imageService, never()).updateAvatar(any(), any());
    }

    @Test
    void uploadAvatarReplace() {
        ru.skypro.homework.model.user.User user = createUser("test@mail.com");
        user.setImage("old_avatar.jpg");
        MockMultipartFile image = new MockMultipartFile("image", "new.jpg", "image/jpeg", new byte[]{1, 2, 3});
        ImageService.ImageResult imgResult = new ImageService.ImageResult("new_avatar.jpg", new byte[]{1});

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(imageService.updateAvatar("old_avatar.jpg", image)).thenReturn(imgResult);
        when(userRepository.save(any(ru.skypro.homework.model.user.User.class))).thenReturn(user);

        userService.uploadAvatar(createUserDetails("test@mail.com"), image);

        assertEquals("new_avatar.jpg", user.getImage());
        verify(imageService).updateAvatar("old_avatar.jpg", image);
        verify(imageService, never()).saveAvatar(any());
    }
}

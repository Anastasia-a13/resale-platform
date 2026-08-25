package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.config.UserDetailsServiceImpl;
import ru.skypro.homework.config.WebSecurityConfig;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.exception.BadRequestException;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(WebSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void getUserSuccess() throws Exception {
        UserDto dto = new UserDto(1, "user@mail.com", "Иван", "Иванов", "+79991234567", "USER", "avatar.jpg");
        when(userService.getCurrentUser(any())).thenReturn(dto);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@mail.com"))
                .andExpect(jsonPath("$.firstName").value("Иван"));
    }

    @Test
    void getUserUnauthorized() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void updateUserSuccess() throws Exception {
        UserDto dto = new UserDto(1, "user@mail.com", "Пётр", "Петров", "+79990000000", "USER", null);
        when(userService.updateUser(any(), any())).thenReturn(dto);

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Пётр\",\"lastName\":\"Петров\",\"phone\":\"+7 (999) 000-00-00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Пётр"))
                .andExpect(jsonPath("$.lastName").value("Петров"));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void updateUserInvalidPhone() throws Exception {
        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Иван\",\"lastName\":\"Иванов\",\"phone\":\"not-a-phone\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void setPasswordSuccess() throws Exception {
        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"OldPass12\",\"newPassword\":\"NewPass12\"}"))
                .andExpect(status().isOk());
        verify(userService).changePassword(any(), any());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void setPasswordWrongCurrent() throws Exception {
        doThrow(new BadRequestException("Wrong password")).when(userService).changePassword(any(), any());

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"WrongPass\",\"newPassword\":\"NewPass12\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void setPasswordInvalidData() throws Exception {
        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"short\",\"newPassword\":\"short\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void uploadAvatarSuccess() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3});

        mockMvc.perform(multipart(HttpMethod.PATCH, "/users/me/image")
                        .file(image))
                .andExpect(status().isOk());
        verify(userService).uploadAvatar(any(), any());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void getUserImageSuccess() throws Exception {
        when(imageService.getAvatar("avatar.jpg")).thenReturn(new byte[]{10, 20});

        mockMvc.perform(get("/users/image/avatar.jpg"))
                .andExpect(status().isOk());
    }
}

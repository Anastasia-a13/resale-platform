package ru.skypro.homework.controller;

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
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
@Import(WebSecurityConfig.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private AdService adService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void getAdImageSuccess() throws Exception {
        when(imageService.getImage("test.jpg")).thenReturn(new byte[]{10, 20});

        mockMvc.perform(get("/ads/image/test.jpg"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void updateAdImageSuccess() throws Exception {
        when(adService.updateImage(any(), anyInt(), any())).thenReturn(new byte[]{1, 2, 3});

        MockMultipartFile image = new MockMultipartFile("image", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3});

        mockMvc.perform(multipart(HttpMethod.PATCH, "/ads/1/image")
                        .file(image))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void getUserImageSuccess() throws Exception {
        when(imageService.getAvatar("avatar.jpg")).thenReturn(new byte[]{10, 20});

        mockMvc.perform(get("/users/image/avatar.jpg"))
                .andExpect(status().isOk());
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
}

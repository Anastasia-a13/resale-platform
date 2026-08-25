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
import ru.skypro.homework.dto.ad.AdDto;
import ru.skypro.homework.dto.ad.AdsDto;
import ru.skypro.homework.dto.ad.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ad.ExtendedAdDto;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdController.class)
@Import(WebSecurityConfig.class)
class AdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdService adService;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAdsSuccess() throws Exception {
        AdsDto adsDto = new AdsDto(2, List.of(
                new AdDto(1, "img1.jpg", 1, 1000, "Ad 1"),
                new AdDto(2, "img2.jpg", 2, 2000, "Ad 2")
        ));
        when(adService.getAllAds()).thenReturn(adsDto);

        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.results.length()").value(2));
    }

    @Test
    void getAdSuccess() throws Exception {
        ExtendedAdDto dto = new ExtendedAdDto(1, "Иван", "Иванов", "Title", "Desc", 1000, "img.jpg", "mail.com", "+79991234567");
        when(adService.getAdById(1)).thenReturn(dto);

        mockMvc.perform(get("/ads/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.authorFirstName").value("Иван"));
    }

    @Test
    void getAdNotFound() throws Exception {
        when(adService.getAdById(99)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/ads/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void createAdSuccess() throws Exception {
        AdDto adDto = new AdDto(1, "img.jpg", 1, 500, "Title");
        when(adService.createAd(any(), any(), any())).thenReturn(adDto);

        MockMultipartFile properties = new MockMultipartFile(
                "properties", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(new CreateOrUpdateAdDto("Title for testing", 1000, "Description for testing")));
        MockMultipartFile image = new MockMultipartFile(
                "image", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1});

        mockMvc.perform(multipart("/ads")
                        .file(properties)
                        .file(image))
                .andExpect(status().isCreated());
    }

    @Test
    void createAdUnauthorized() throws Exception {
        mockMvc.perform(multipart("/ads")
                        .file(new MockMultipartFile("properties", "", MediaType.APPLICATION_JSON_VALUE, "{}".getBytes()))
                        .file(new MockMultipartFile("image", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1})))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void createAdInvalidData() throws Exception {
        MockMultipartFile properties = new MockMultipartFile(
                "properties", "", MediaType.APPLICATION_JSON_VALUE,
                "{\"title\":\"\",\"price\":-1,\"description\":\"\"}".getBytes());
        MockMultipartFile image = new MockMultipartFile(
                "image", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1});

        mockMvc.perform(multipart("/ads")
                        .file(properties)
                        .file(image))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "owner@mail.com", roles = "USER")
    void deleteAdSuccess() throws Exception {
        mockMvc.perform(delete("/ads/1"))
                .andExpect(status().isNoContent());
        verify(adService).deleteAd(any(), eq(1));
    }

    @Test
    @WithMockUser(username = "other@mail.com", roles = "USER")
    void deleteAdForbidden() throws Exception {
        doThrow(new ForbiddenException("No access")).when(adService).deleteAd(any(), eq(1));

        mockMvc.perform(delete("/ads/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", roles = "ADMIN")
    void deleteAdAdminSuccess() throws Exception {
        mockMvc.perform(delete("/ads/1"))
                .andExpect(status().isNoContent());
        verify(adService).deleteAd(any(), eq(1));
    }

    @Test
    @WithMockUser(username = "owner@mail.com", roles = "USER")
    void updateAdSuccess() throws Exception {
        AdDto adDto = new AdDto(1, "img.jpg", 1, 2000, "Updated");
        when(adService.updateAd(any(), anyInt(), any())).thenReturn(adDto);

        mockMvc.perform(patch("/ads/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\",\"price\":2000,\"description\":\"Updated description text\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    @WithMockUser(username = "other@mail.com", roles = "USER")
    void updateAdForbidden() throws Exception {
        when(adService.updateAd(any(), anyInt(), any())).thenThrow(new ForbiddenException("No access"));

        mockMvc.perform(patch("/ads/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Hacked\",\"price\":2000,\"description\":\"Hacked description text\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void getAdsMeSuccess() throws Exception {
        AdsDto adsDto = new AdsDto(1, List.of(new AdDto(1, "img.jpg", 1, 1000, "My Ad")));
        when(adService.getUserAds(any())).thenReturn(adsDto);

        mockMvc.perform(get("/ads/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void updateImageSuccess() throws Exception {
        when(adService.updateImage(any(), anyInt(), any())).thenReturn(new byte[]{1, 2, 3});

        MockMultipartFile image = new MockMultipartFile("image", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3});

        mockMvc.perform(multipart(HttpMethod.PATCH, "/ads/1/image")
                        .file(image))
                .andExpect(status().isOk());
    }

    @Test
    void getImageSuccess() throws Exception {
        when(imageService.getImage("test.jpg")).thenReturn(new byte[]{10, 20});

        mockMvc.perform(get("/ads/image/test.jpg"))
                .andExpect(status().isOk());
    }
}

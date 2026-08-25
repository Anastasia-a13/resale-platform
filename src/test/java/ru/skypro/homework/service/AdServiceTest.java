package ru.skypro.homework.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.dto.ad.AdDto;
import ru.skypro.homework.dto.ad.AdsDto;
import ru.skypro.homework.dto.ad.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ad.ExtendedAdDto;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.user.Role;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.core.userdetails.User.withUsername;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private AdMapper adMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValidatorService validator;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private AdService adService;

    private UserDetails createUserDetails(String email, String role) {
        return withUsername(email)
                .password("pass")
                .authorities(role)
                .build();
    }

    private ru.skypro.homework.model.user.User createUser(String email) {
        ru.skypro.homework.model.user.User user = new ru.skypro.homework.model.user.User();
        user.setId(1);
        user.setEmail(email);
        user.setRole(Role.USER);
        return user;
    }

    private Ad createAd(Integer id, String authorEmail) {
        Ad ad = new Ad();
        ad.setId(id);
        ad.setTitle("Test Ad");
        ad.setPrice(1000);
        ad.setDescription("Description");
        ad.setImage("image.jpg");
        ru.skypro.homework.model.user.User author = createUser(authorEmail);
        ad.setAuthor(author);
        return ad;
    }

    @Test
    void getAllAds() {
        Ad ad1 = createAd(1, "user1@mail.com");
        Ad ad2 = createAd(2, "user2@mail.com");
        AdDto dto1 = new AdDto(1, "img1.jpg", 1, 1000, "Ad 1");
        AdDto dto2 = new AdDto(2, "img2.jpg", 2, 2000, "Ad 2");

        when(adRepository.findAll()).thenReturn(List.of(ad1, ad2));
        when(adMapper.toDto(ad1)).thenReturn(dto1);
        when(adMapper.toDto(ad2)).thenReturn(dto2);

        AdsDto result = adService.getAllAds();

        assertEquals(2, result.count());
        assertEquals(2, result.results().size());
    }

    @Test
    void createAd() {
        UserDetails user = createUserDetails("author@mail.com", "ROLE_USER");
        ru.skypro.homework.model.user.User author = createUser("author@mail.com");
        CreateOrUpdateAdDto dto = new CreateOrUpdateAdDto("Title", 500, "Description text");
        Ad ad = createAd(null, "author@mail.com");
        Ad saved = createAd(1, "author@mail.com");
        AdDto adDto = new AdDto(1, "img.jpg", 1, 500, "Title");

        when(userRepository.findByEmail("author@mail.com")).thenReturn(Optional.of(author));
        when(adMapper.toEntity(dto)).thenReturn(ad);
        when(imageService.saveImage(any())).thenReturn("img.jpg");
        when(adRepository.save(any(Ad.class))).thenReturn(saved);
        when(adMapper.toDto(saved)).thenReturn(adDto);

        AdDto result = adService.createAd(user, dto, new MockMultipartFile("image", new byte[1]));

        assertNotNull(result);
        assertEquals("Title", result.title());
        verify(adRepository).save(any(Ad.class));
    }

    @Test
    void getAdByIdSuccess() {
        Ad ad = createAd(1, "author@mail.com");
        ExtendedAdDto extDto = new ExtendedAdDto(1, "Иван", "Иванов", "Title", "Desc", 1000, "img.jpg", "author@mail.com", "+79991234567");

        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        when(adMapper.toExtendedDto(ad)).thenReturn(extDto);

        ExtendedAdDto result = adService.getAdById(1);

        assertEquals(1, result.pk());
        assertEquals("Иван", result.authorFirstName());
    }

    @Test
    void getAdByIdNotFound() {
        when(adRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adService.getAdById(99));
    }

    @Test
    void deleteAdSuccess() {
        UserDetails owner = createUserDetails("owner@mail.com", "ROLE_USER");
        Ad ad = createAd(1, "owner@mail.com");

        when(adRepository.findById(1)).thenReturn(Optional.of(ad));

        adService.deleteAd(owner, 1);

        verify(validator).checkAdOwnership(ad, owner);
        verify(adRepository).deleteById(1);
    }

    @Test
    void deleteAdForbidden() {
        UserDetails other = createUserDetails("other@mail.com", "ROLE_USER");
        Ad ad = createAd(1, "owner@mail.com");

        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        doThrow(new ForbiddenException("No access")).when(validator).checkAdOwnership(ad, other);

        assertThrows(ForbiddenException.class, () -> adService.deleteAd(other, 1));
        verify(adRepository, never()).deleteById(any());
    }

    @Test
    void updateAdSuccess() {
        UserDetails owner = createUserDetails("owner@mail.com", "ROLE_USER");
        Ad ad = createAd(1, "owner@mail.com");
        CreateOrUpdateAdDto dto = new CreateOrUpdateAdDto("New Title", 2000, "New description");
        AdDto adDto = new AdDto(1, "img.jpg", 1, 2000, "New Title");

        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        when(adRepository.save(any(Ad.class))).thenReturn(ad);
        when(adMapper.toDto(ad)).thenReturn(adDto);

        AdDto result = adService.updateAd(owner, 1, dto);

        assertEquals("New Title", result.title());
        verify(adMapper).updateAd(dto, ad);
    }

    @Test
    void updateAdForbidden() {
        UserDetails other = createUserDetails("other@mail.com", "ROLE_USER");
        Ad ad = createAd(1, "owner@mail.com");
        CreateOrUpdateAdDto dto = new CreateOrUpdateAdDto("New", 2000, "New desc");

        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        doThrow(new ForbiddenException("No access")).when(validator).checkAdOwnership(ad, other);

        assertThrows(ForbiddenException.class, () -> adService.updateAd(other, 1, dto));
        verify(adRepository, never()).save(any());
    }

    @Test
    void getUserAds() {
        UserDetails owner = createUserDetails("owner@mail.com", "ROLE_USER");
        Ad ad = createAd(1, "owner@mail.com");
        AdDto dto = new AdDto(1, "img.jpg", 1, 1000, "Ad");

        when(adRepository.findAllByAuthorEmail("owner@mail.com")).thenReturn(List.of(ad));
        when(adMapper.toDto(ad)).thenReturn(dto);

        AdsDto result = adService.getUserAds(owner);

        assertEquals(1, result.count());
        assertEquals("Ad", result.results().get(0).title());
    }

    @Test
    void updateImageSuccess() {
        UserDetails owner = createUserDetails("owner@mail.com", "ROLE_USER");
        Ad ad = createAd(1, "owner@mail.com");
        MockMultipartFile image = new MockMultipartFile("image", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});
        ImageService.ImageResult imgResult = new ImageService.ImageResult("new.jpg", new byte[]{1, 2, 3});

        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        when(imageService.updateImage("image.jpg", image)).thenReturn(imgResult);
        when(adRepository.save(any(Ad.class))).thenReturn(ad);

        byte[] result = adService.updateImage(owner, 1, image);

        assertEquals(3, result.length);
        assertEquals("new.jpg", ad.getImage());
    }
}

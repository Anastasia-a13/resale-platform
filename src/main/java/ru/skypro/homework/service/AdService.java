package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ad.AdDto;
import ru.skypro.homework.dto.ad.AdsDto;
import ru.skypro.homework.dto.ad.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ad.ExtendedAdDto;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final UserRepository userRepository;
    private final ValidatorService validator;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public AdsDto getAllAds() {
        List<AdDto> ads = adRepository.findAll().stream()
                .map(adMapper::toDto)
                .toList();
        return new AdsDto(ads.size(), ads);
    }

    @Transactional
    public AdDto createAd(UserDetails userDetails, CreateOrUpdateAdDto properties, MultipartFile image) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.USER_NOT_FOUND));
        Ad ad = adMapper.toEntity(properties);
        ad.setAuthor(user);
        ad.setImage(imageService.saveImage(image));
        return adMapper.toDto(adRepository.save(ad));
    }

    @Transactional(readOnly = true)
    public ExtendedAdDto getAdById(Integer id) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.AD_NOT_FOUND));
        return adMapper.toExtendedDto(ad);
    }

    @Transactional
    public void deleteAd(UserDetails userDetails, Integer id) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.AD_NOT_FOUND));
        validator.checkAdOwnership(ad, userDetails);
        adRepository.deleteById(id);
    }

    @Transactional
    public AdDto updateAd(UserDetails userDetails, Integer id, CreateOrUpdateAdDto dto) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.AD_NOT_FOUND));
        validator.checkAdOwnership(ad, userDetails);
        adMapper.updateAd(dto, ad);
        return adMapper.toDto(adRepository.save(ad));
    }

    @Transactional(readOnly = true)
    public AdsDto getUserAds(UserDetails userDetails) {
        List<AdDto> ads = adRepository.findAllByAuthorEmail(userDetails.getUsername()).stream()
                .map(adMapper::toDto)
                .toList();
        return new AdsDto(ads.size(), ads);
    }

    @Transactional
    public byte[] updateImage(UserDetails userDetails, Integer id, MultipartFile image) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.AD_NOT_FOUND));
        validator.checkAdOwnership(ad, userDetails);
        byte[] imageBytes = imageService.getImageBytes(image);
        ad.setImage(imageService.saveImage(image));
        adRepository.save(ad);
        return imageBytes;
    }
}
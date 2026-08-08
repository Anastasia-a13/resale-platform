package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ad.AdDto;
import ru.skypro.homework.dto.ad.AdsDto;
import ru.skypro.homework.dto.ad.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ad.ExtendedAdDto;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;

@Service
@RequiredArgsConstructor
public class AdService {
    private final AdRepository adRepository;
    private final AdMapper adMapper;

    public AdsDto getAllAds() {
    }

    public AdDto createAd(UserDetails userDetails, CreateOrUpdateAdDto properties, MultipartFile image) {
    }

    public ExtendedAdDto getAdById(UserDetails userDetails, Integer id) {
    }

    public void deleteAd(UserDetails userDetails, Integer id) {
    }

    public AdDto updateAd(UserDetails userDetails, Integer id, CreateOrUpdateAdDto dto) {
    }

    public AdsDto getUserAds(UserDetails userDetails) {
    }

    public byte[] updateImage(UserDetails userDetails, Integer id, MultipartFile image) {
    }
}
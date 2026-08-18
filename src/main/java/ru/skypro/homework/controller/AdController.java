package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ad.AdDto;
import ru.skypro.homework.dto.ad.AdsDto;
import ru.skypro.homework.dto.ad.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ad.ExtendedAdDto;
import ru.skypro.homework.service.AdService;

/**
 * Контроллер для управления объявлениями.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ads")
@Tag(name = "Объявления", description = "API для работы с объявлениями")
public class AdController {

    private final AdService adService;

    /**
     * Получает список объявлений.
     */
    @GetMapping
    @Operation(summary = "Получить список объявлений")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список объявлений успешно получен")
    })
    public ResponseEntity<AdsDto> getAds() {
        return ResponseEntity.ok(adService.getAllAds());
    }

    /**
     * Создает новое объявление.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Добавление объявления")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Объявление успешно создано"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
    })
    public ResponseEntity<AdDto> addAd(
            @RequestPart("properties") @Valid CreateOrUpdateAdDto properties,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AdDto createdAd = adService.createAd(userDetails, properties, image);
        return ResponseEntity.status(201).body(createdAd);
    }

    /**
     * Получает детальные данные конкретного объявления.
     *
     * @param id          идентификатор объявления
     * @return ResponseEntity с объектом ExtendedAdDto
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получение информации об объявлении")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные объявления успешно получены"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
    })
    public ResponseEntity<ExtendedAdDto> getAd(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(adService.getAdById(id));
    }

    /**
     * Удаляет объявление.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление объявления")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Объявление успешно удалено"),
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
    })
    public ResponseEntity<Void> removeAd(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        adService.deleteAd(userDetails, id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Обновление информации об объявлении.
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Обновление информации об объявлении")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Объявление успешно обновлено"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
    })
    public ResponseEntity<AdDto> updateAds(
            @PathVariable Integer id,
            @RequestBody @Valid CreateOrUpdateAdDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(adService.updateAd(userDetails, id, dto));
    }

    /**
     * Получение объявлений авторизованного пользователя.
     */
    @GetMapping("/me")
    @Operation(summary = "Получение объявлений авторизованного пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список объявлений пользователя успешно получен"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
    })
    public ResponseEntity<AdsDto> getAdsMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(adService.getUserAds(userDetails));
    }

    /**
     * Обновление картинки объявления.
     * Возвращает байты изображения.
     */
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Обновление картинки объявления")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Картинка успешно обновлена и возвращена"),
            @ApiResponse(responseCode = "403", description = "Нет прав на изменение картинки"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
    })
    public ResponseEntity<byte[]> updateImage(
            @PathVariable Integer id,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        byte[] imageBytes = adService.updateImage(userDetails, id, image);
        MediaType mediaType = image.getContentType() != null
                ? MediaType.parseMediaType(image.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imageBytes);
    }
}
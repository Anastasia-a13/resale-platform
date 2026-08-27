package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import java.util.Locale;

/**
 * Контроллер для работы с изображениями.
 * Реализует получение и обновление картинок объявлений и аватаров пользователей.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Изображения", description = "API для работы с изображениями объявлений и аватаров")
public class ImageController {

    private final ImageService imageService;
    private final AdService adService;
    private final UserService userService;

    /**
     * Получает картинку объявления по имени файла.
     *
     * @param fileName имя файла изображения
     * @return ResponseEntity с байтами изображения
     */
    @GetMapping(value = "/ads/image/{fileName}",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    @Operation(summary = "Получить картинку объявления")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Картинка успешно получена"),
            @ApiResponse(responseCode = "404", description = "Картинка не найдена")
    })
    public ResponseEntity<byte[]> getAdImage(@PathVariable String fileName) {
        byte[] image = imageService.getImage(fileName);
        return ResponseEntity.ok()
                .contentType(resolveMediaType(fileName))
                .body(image);
    }

    /**
     * Обновляет картинку объявления.
     * Возвращает байты обновленного изображения.
     *
     * @param id          идентификатор объявления
     * @param image       файл изображения (multipart/form-data)
     * @param userDetails данные авторизованного пользователя
     * @return ResponseEntity с байтами обновленного изображения
     */
    @PatchMapping(value = "/ads/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Обновление картинки объявления")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Картинка успешно обновлена и возвращена"),
            @ApiResponse(responseCode = "403", description = "Нет прав на изменение картинки"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
    })
    public ResponseEntity<byte[]> updateAdImage(
            @PathVariable Integer id,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        byte[] imageBytes = adService.updateAdImage(userDetails, id, image);
        MediaType mediaType = image.getContentType() != null
                ? MediaType.parseMediaType(image.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imageBytes);
    }

    /**
     * Получает аватар пользователя по имени файла.
     *
     * @param fileName имя файла аватара
     * @return ResponseEntity с байтами изображения
     */
    @GetMapping(value = "/users/image/{fileName}",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    @Operation(summary = "Получить аватар пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аватар успешно получен"),
            @ApiResponse(responseCode = "404", description = "Аватар не найден")
    })
    public ResponseEntity<byte[]> getUserImage(@PathVariable String fileName) {
        byte[] image = imageService.getAvatar(fileName);
        return ResponseEntity.ok()
                .contentType(resolveMediaType(fileName))
                .body(image);
    }

    /**
     * Загружает новый аватар для текущего пользователя.
     *
     * @param image       файл изображения (multipart/form-data)
     * @param userDetails данные авторизованного пользователя
     * @return ResponseEntity со статусом 200 при успехе
     */
    @PatchMapping(value = "/users/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить аватар пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аватар успешно загружен"),
            @ApiResponse(responseCode = "400", description = "Файл не предоставлен или имеет неверный формат"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public ResponseEntity<Void> updateUserImage(
            @RequestPart("image") MultipartFile image,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.uploadAvatar(userDetails, image);
        return ResponseEntity.ok().build();
    }

    private MediaType resolveMediaType(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return switch (ext) {
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}

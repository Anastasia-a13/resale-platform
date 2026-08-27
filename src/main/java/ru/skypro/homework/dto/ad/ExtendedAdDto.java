package ru.skypro.homework.dto.ad;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO с расширенной информацией об объявлении.
 * <p>
 * Содержит все данные объявления, включая полную информацию об авторе.
 * </p>
 */
@Schema(description = "Расширенные данные объявления")
public record ExtendedAdDto(
        @Schema(description = "ID объявления", example = "101")
        Integer pk,

        @Schema(description = "Имя автора", example = "Федор")
        String authorFirstName,

        @Schema(description = "Фамилия автора", example = "Достоевский")
        String authorLastName,

        @Schema(description = "Заголовок", example = "Велосипед")
        String title,

        @Schema(description = "Описание", example = "Продается велосипед...")
        String description,

        @Schema(description = "Цена", example = "5000")
        Integer price,

        @Schema(description = "Картинка", example = "https://example.com/img.jpg")
        String image,

        @Schema(description = "Email автора", example = "user@example.com")
        String email,

        @Schema(description = "Телефон автора", example = "+7 (999) 123-45-67")
        String phone
) {
}
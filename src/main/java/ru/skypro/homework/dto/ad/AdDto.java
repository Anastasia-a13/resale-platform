package ru.skypro.homework.dto.ad;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO с информацией об объявлении для отображения в списке.
 * <p>
 * Содержит ID автора, ссылку на изображение, ID объявления, цену и заголовок.
 * </p>
 */
@Schema(description = "Краткое описание объявления")
public record AdDto(
        @Schema(description = "ID автора", example = "1")
        Integer author,

        @Schema(description = "Ссылка на картинку", example = "https://example.com/img.jpg")
        String image,

        @Schema(description = "ID объявления", example = "101")
        Integer pk,

        @Schema(description = "Цена", example = "5000")
        Integer price,

        @Schema(description = "Заголовок", example = "Велосипед")
        String title
) {
}
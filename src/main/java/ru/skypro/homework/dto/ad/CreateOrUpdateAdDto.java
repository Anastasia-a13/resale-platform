package ru.skypro.homework.dto.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для создания или обновления объявления.
 */
@Schema(description = "Данные объявления для создания/обновления")
public record CreateOrUpdateAdDto(
        @Schema(description = "Заголовок объявления", example = "Велосипед б/у")
        @NotBlank(message = "Заголовок обязателен")
        @Size(min = 4, max = 32, message = "Длина заголовка от 4 до 32 символов")
        String title,

        @Schema(description = "Цена объявления", example = "5000")
        @Min(value = 0, message = "Цена не может быть отрицательной")
        Integer price,

        @Schema(description = "Описание объявления", example = "Продаю велосипед...")
        @NotBlank(message = "Описание обязательно")
        @Size(min = 8, max = 64, message = "Длина описания от 8 до 64 символов")
        String description
) {
}
package ru.skypro.homework.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для создания или обновления комментария.
 */

@Schema(description = "Текст комментария")
public record CreateOrUpdateCommentDto(
        @Schema(description = "Текст сообщения", example = "Отличный товар!")
        @NotBlank(message = "Текст комментария обязателен")
        @Size(min = 8, max = 64, message = "Длина текста от 8 до 64 символов")
        String text
) {
}
package ru.skypro.homework.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO для обновления информации о пользователе.
 * <p>
 * Содержит имя, фамилию и телефон с валидацией форматов.
 * </p>
 */
@Schema(description = "Обновление данных пользователя")
public record UpdateUserDto(
        @Schema(description = "Новое имя", example = "Александр")
        @Size(min = 3, max = 10, message = "Длина имени от 3 до 10 символов")
        String firstName,

        @Schema(description = "Новая фамилия", example = "Пушкин")
        @Size(min = 3, max = 10, message = "Длина фамилии от 3 до 10 символов")
        String lastName,

        @Schema(description = "Новый телефон", example = "+7 (999) 000-00-00")
        @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}", message = "Неверный формат телефона")
        String phone
) {
}
package ru.skypro.homework.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для смены пароля.
 */
@Schema(description = "Смена пароля")
public record NewPasswordDto(
        @Schema(description = "Текущий пароль", example = "OldPass123")
        @NotBlank(message = "Текущий пароль обязателен")
        @Size(min = 8, max = 16, message = "Длина от 8 до 16 символов")
        String currentPassword,

        @Schema(description = "Новый пароль", example = "NewPass123")
        @NotBlank(message = "Новый пароль обязателен")
        @Size(min = 8, max = 16, message = "Длина от 8 до 16 символов")
        String newPassword
) {
}
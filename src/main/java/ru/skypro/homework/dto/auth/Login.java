package ru.skypro.homework.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для входа в систему.
 * Использует record для неизменяемости и валидацию полей.
 */
@Schema(description = "Данные для авторизации пользователя")
public record Login(

        @Schema(description = "Логин пользователя (email)", example = "user@example.com")
        @NotBlank(message = "Логин не может быть пустым")
        @Email(message = "Некорректно введен email")
        @Size(min = 4, max = 32, message = "Логин должен быть от 4 до 32 символов")
        String username,

        @Schema(description = "Пароль пользователя", example = "Password123")
        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 8, max = 16, message = "Пароль должен быть от 8 до 16 символов")
        String password
) {
}

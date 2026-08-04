package ru.skypro.homework.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.skypro.homework.dto.Role;

/**
 * DTO для регистрации нового пользователя.
 * Соответствует схеме 'Register' из спецификации OpenAPI.
 * <p>
 * Все поля обязательны для заполнения и проходят валидацию перед обработкой контроллером.
 * </p>
 */
@Schema(description = "DTO для регистрации пользователя")
public record Register(
        @Schema(description = "Логин пользователя (email)", example = "user@example.com")
        @NotBlank(message = "Логин обязателен")
        @Email(message = "Введите корректный email")
        @Size(min = 4, max = 32, message = "Логин от 4 до 32 символов")
        String username,

        @Schema(description = "Пароль", example = "ABcde123")
        @NotBlank(message = "Пароль обязателен")
        @Size(min = 8, max = 16, message = "Пароль от 8 до 16 символов")
        String password,

        @Schema(description = "Имя", example = "Федор")
        @NotBlank(message = "Имя не может быть пустым")
        @Size(min = 2, max = 16, message = "Имя от 2 до 16 символов")
        String firstName,

        @Schema(description = "Фамилия", example = "Достоевский")
        @NotBlank(message = "Фамилия не может быть пустой")
        @Size(min = 2, max = 16, message = "Фамилия от 2 до 16 символов")
        String lastName,

        @Schema(description = "Телефон", example = "+7 (999) 123-45-67")
        @NotBlank(message = "Телефон обязателен")
        @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}", message = "Неверный формат телефона")
        String phone,

        @Schema(description = "Роль", example = "USER")
        @NotBlank(message = "Роль обязательна")
        String role
) {
}
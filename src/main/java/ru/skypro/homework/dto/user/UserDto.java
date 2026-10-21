package ru.skypro.homework.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO с информацией о пользователе для отображения.
 * <p>
 * Содержит основные данные пользователя: ID, email, имя, фамилию,
 * телефон, роль и ссылку на аватар.
 * </p>
 */
@Schema(description = "Профиль пользователя")
public record UserDto(
        @Schema(description = "ID пользователя", example = "1")
        Integer id,

        @Schema(description = "Логин (email)", example = "user@example.com")
        String email,

        @Schema(description = "Имя", example = "Федор")
        String firstName,

        @Schema(description = "Фамилия", example = "Достоевский")
        String lastName,

        @Schema(description = "Телефон", example = "+7 (999) 123-45-67")
        String phone,

        @Schema(description = "Роль", example = "USER")
        String role,

        @Schema(description = "Аватар", example = "https://example.com/avatar.jpg")
        String image
) {
}
package ru.skypro.homework.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO с информацией о комментарии.
 * <p>
 * Содержит ID автора, его аватар и имя, дату создания, ID комментария и текст.
 * </p>
 */
@Schema(description = "Комментарий к объявлению")
public record CommentDto(
        @Schema(description = "ID комментария", example = "101")
        Integer pk,

        @Schema(description = "ID автора комментария", example = "1")
        Integer author,

        @Schema(description = "Имя автора комментария", example = "Федор")
        String authorFirstName,

        @Schema(description = "Аватар автора", example = "https://example.com/avatar.jpg")
        String authorImage,

        @Schema(description = "Текст комментария", example = "Отличный товар!")
        String text,

        @Schema(description = "Дата создания (timestamp ms)", example = "1715623400000")
        Long createdAt
) {
}

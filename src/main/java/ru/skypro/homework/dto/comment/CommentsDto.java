package ru.skypro.homework.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO со списком комментариев и общим количеством.
 */
@Schema(description = "Список комментариев")
public record CommentsDto(
        @Schema(description = "Количество комментариев", example = "5")
        Integer count,

        @Schema(description = "Список комментариев")
        List<CommentDto> results
) {
}
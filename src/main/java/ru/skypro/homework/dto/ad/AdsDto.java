package ru.skypro.homework.dto.ad;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO со списком объявлений и общим количеством.
 */
@Schema(description = "Список объявлений")
public record AdsDto(
        @Schema(description = "Общее количество объявлений", example = "10")
        Integer count,

        @Schema(description = "Список объявлений")
        List<AdDto> results
) {
}
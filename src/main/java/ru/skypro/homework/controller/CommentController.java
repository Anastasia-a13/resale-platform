package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.comment.CommentDto;
import ru.skypro.homework.dto.comment.CommentsDto;
import ru.skypro.homework.dto.comment.CreateOrUpdateCommentDto;
import ru.skypro.homework.service.CommentService;

/**
 * Контроллер комментариев.
 */
@RestController
@Tag(name = "Комментарии", description = "API для работы с комментариями к объявлениям")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Получает список всех комментариев для указанного объявления.
     */
    @GetMapping("/ads/{adId}/comments")
    @Operation(summary = "Получение комментариев объявления")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список комментариев успешно получен"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
    })
    public ResponseEntity<CommentsDto> getComments(
            @PathVariable Integer adId
    ) {
        return ResponseEntity.ok(commentService.getCommentsByAdId(adId));
    }

    /**
     * Добавляет новый комментарий к указанному объявлению.
     */
    @PostMapping("/ads/{adId}/comments")
    @Operation(summary = "Добавление комментария к объявлению")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий успешно создан"), // В YAML для addComment стоит 200, хотя обычно 201. Следуем YAML.
            @ApiResponse(responseCode = "404", description = "Объявление не найдено"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
    })
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Integer adId,
            @RequestBody @Valid CreateOrUpdateCommentDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CommentDto createdComment = commentService.createComment(adId, dto, userDetails);
        return ResponseEntity.ok(createdComment);
    }

    /**
     * Удаляет комментарий по ID.
     */
    @DeleteMapping("/ads/{adId}/comments/{commentId}")
    @Operation(summary = "Удаление комментария")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий успешно удален"), // КРИТИЧНО: В YAML стоит 200, а не 204!
            @ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @ApiResponse(responseCode = "404", description = "Комментарий или объявление не найдены"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
    })
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer adId,
            @PathVariable Integer commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        commentService.deleteComment(adId, commentId, userDetails);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет существующий комментарий.
     */
    @PatchMapping("/ads/{adId}/comments/{commentId}")
    @Operation(summary = "Обновление комментария")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий успешно обновлен"),
            @ApiResponse(responseCode = "403", description = "Нет прав на редактирование"),
            @ApiResponse(responseCode = "404", description = "Комментарий или объявление не найдены"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
    })
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Integer adId,
            @PathVariable Integer commentId,
            @RequestBody @Valid CreateOrUpdateCommentDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(commentService.updateComment(adId, commentId, dto, userDetails));
    }
}

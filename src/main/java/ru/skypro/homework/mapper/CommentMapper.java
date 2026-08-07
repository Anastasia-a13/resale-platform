package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.comment.CommentDto;
import ru.skypro.homework.dto.comment.CreateOrUpdateCommentDto;
import ru.skypro.homework.model.Comment;

/**
 * MapStruct-маппер для конвертации сущности {@link Comment} в DTO и обратно.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {
    /**
     * Преобразует сущность {@link Comment} в DTO {@link CommentDto}.
     *
     * @param entity сущность комментария
     * @return DTO комментария
     */
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.image", target = "authorImage")
    CommentDto toDto(Comment entity);

    /**
     * Преобразует DTO создания/обновления комментария в сущность {@link Comment}.
     * Поля id, createdAt, ad и author заполняются сервисом.
     *
     * @param dto DTO комментария
     * @return сущность комментария
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "text")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toEntity(CreateOrUpdateCommentDto dto);

    /**
     * Обновляет существующий комментарий данными из DTO, пропуская null-значения.
     * Поля id, createdAt, ad и author не изменяются.
     *
     * @param dto     DTO с новыми данными
     * @param comment целевая сущность комментария
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "author", ignore = true)
    void updateComment(CreateOrUpdateCommentDto dto, @MappingTarget Comment comment);
}

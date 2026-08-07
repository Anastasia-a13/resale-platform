package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.ad.AdDto;
import ru.skypro.homework.dto.ad.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ad.ExtendedAdDto;
import ru.skypro.homework.model.Ad;

/**
 * MapStruct-маппер для конвертации сущности {@link Ad} в DTO и обратно.
 */
@Mapper(componentModel = "spring")
public interface AdMapper {
    /**
     * Преобразует сущность {@link Ad} в DTO {@link AdDto}.
     *
     * @param entity сущность объявления
     * @return DTO объявления
     */
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    AdDto toDto(Ad entity);

    /**
     * Преобразует DTO создания/обновления объявления в сущность {@link Ad}.
     * Поля id, image и author заполняются сервисом.
     *
     * @param dto DTO объявления
     * @return сущность объявления
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "author", ignore = true)
    Ad toEntity(CreateOrUpdateAdDto dto);

    /**
     * Преобразует сущность {@link Ad} в расширенное DTO {@link ExtendedAdDto}.
     *
     * @param entity сущность объявления
     * @return расширенное DTO объявления
     */
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "author.lastName", target = "authorLastName")
    @Mapping(source = "author.email", target = "email")
    @Mapping(source = "author.phone", target = "phone")
    ExtendedAdDto toExtendedDto(Ad entity);

    /**
     * Обновляет существующее объявление данными из DTO, пропуская null-значения.
     * Поля id, image и author не изменяются.
     *
     * @param dto  DTO с новыми данными
     * @param ad   целевая сущность объявления
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "author", ignore = true)
    void updateAd(CreateOrUpdateAdDto dto, @MappingTarget Ad ad);
}

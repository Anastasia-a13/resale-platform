package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.auth.Register;
import ru.skypro.homework.dto.user.UpdateUserDto;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.model.user.User;

/**
 * MapStruct-маппер для конвертации сущности {@link User} в DTO и обратно.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Преобразует сущность {@link User} в DTO {@link UserDto}.
     *
     * @param entity сущность пользователя
     * @return DTO пользователя
     */
    UserDto toDto(User entity);

    /**
     * Преобразует DTO регистрации в сущность пользователя.
     *
     * @param register DTO с данными регистрации
     * @return новая сущность {@link User}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(source = "username", target = "email")
    User toEntity(Register register);

    /**
     * Обновляет существующего пользователя данными из DTO, пропуская null-значения.
     * Поля id, email, password, role и image не изменяются.
     *
     * @param dto  DTO с новыми данными
     * @param user целевая сущность пользователя
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "image", ignore = true)
    void updateUser(UpdateUserDto dto, @MappingTarget User user);
}
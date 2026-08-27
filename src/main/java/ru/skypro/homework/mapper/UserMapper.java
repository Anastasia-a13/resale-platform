package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.user.UpdateUserDto;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.model.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "image", expression = "java(entity.getImage() == null ? null : \"/users/image/\" + entity.getImage())")
    UserDto toDto(User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "image", ignore = true)
    void updateUser(UpdateUserDto dto, @MappingTarget User user);
}
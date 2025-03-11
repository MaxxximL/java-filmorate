package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.User;

public class UserMapper {
    public static CreateUserDto toDto(User model) {
        return CreateUserDto.builder()
                .id(model.getId())
                .email(model.getEmail())
                .login(model.getLogin())
                .name(model.getName())
                .birthday(model.getBirthday())
                .build();
    }

    public static User toModel(CreateUserDto createUserDto) {
        return User.builder()
                .email(createUserDto.getEmail())
                .login(createUserDto.getLogin())
                .name(createUserDto.getName())
                .birthday(createUserDto.getBirthday())
                .build();
    }
}
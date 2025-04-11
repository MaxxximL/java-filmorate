package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class CreateUserDto {

    private long id;

    @Email
    private String email;

    @NotBlank
    @Size(min = 1, message = "Логин не может быть пустым.")
    private String login;

    private String name;

    @Past(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;

    // Добавляем поле для хранения id друзей
    private final Set<Long> friends = new HashSet<>();
}
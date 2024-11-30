package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private int id;

    @Email(message = "Электронная почта должна содержать символ '@'.")
    private String email;

    @NotBlank(message = "Логин не может быть пустым и содержать пробелы.")
    @Size(min = 1, message = "Логин не может быть пустым.")
    private String login;

    @NotBlank(message = "Имя не может быть пустым.")
    private String name;

    @Past(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
}
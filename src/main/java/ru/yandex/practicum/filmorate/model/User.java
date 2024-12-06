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

    @Email
    private String email;

    @NotBlank
    @Size
    private String login;

    @NotBlank
    private String name;

    @Past
    private LocalDate birthday;
}
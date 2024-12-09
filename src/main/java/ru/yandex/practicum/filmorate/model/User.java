package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
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


    private String name;

    @Past
    private LocalDate birthday;
}
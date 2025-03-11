package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
public class Film {

    private long id;

    @NotBlank(message = "Имя фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    private String description;

    private LocalDate releaseDate;

    @Positive
    private long duration;

    // Добавляем поле для хранения id пользователей, которые поставили лайки
    private Set<Long> likes = new HashSet<>();

    private Long mpaId;

    private Mpa mpa;

    private List<Long> genreIds = new ArrayList<>(); // Инициализируем как пустой список

    private List<Genre> genres = new ArrayList<>(); // Инициализация списка жанров


}
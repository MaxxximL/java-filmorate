package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CreateFilmDto {
    private long id;

    @NotBlank(message = "Имя фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Positive
    private long duration;

    private Long mpaId;

    private MpaDto mpa;

    private List<Long> genreIds = new ArrayList<>();

    private List<GenreDto> genres = new ArrayList<>(); // Инициализация списка


}
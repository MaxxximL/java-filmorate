package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private Mpa mpa;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    @Builder.Default
    private List<Long> genreIds = new ArrayList<>();

    @Builder.Default
    private List<Genre> genres = new ArrayList<>();

    @Builder.Default
    private List<Director> directors = new ArrayList<>();

}
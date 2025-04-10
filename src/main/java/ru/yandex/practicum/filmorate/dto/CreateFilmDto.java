package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    private MpaDto mpa;

    private List<Long> genreIds = new ArrayList<>();

    private List<GenreDto> genres = new ArrayList<>();

    private List<DirectorDto> directors = new ArrayList<>();

}
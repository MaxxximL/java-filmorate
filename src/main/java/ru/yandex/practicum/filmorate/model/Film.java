package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {

    private int id;

    @NotBlank
    private String name;

    @Size
    private String description;

    @PastOrPresent
    private LocalDate releaseDate;

    @Positive
    private long duration;


}

package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void addFilm() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Описание тестового фильма");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmController.addFilm(film);

        assertThat(createdFilm.getId()).isEqualTo(1); // Первый фильм с ID 1
        assertThat(createdFilm.getName()).isEqualTo(film.getName());
    }

    @Test
    void updateFilm() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Описание тестового фильма");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmController.addFilm(film);
        createdFilm.setName("Обновленный фильм");

        Film updatedFilm = filmController.updateFilm(createdFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Обновленный фильм");
    }

    @Test
    void updateNonExistentFilm() {
        Film film = new Film();
        film.setId(999); // Не существующий ID
        film.setName("Фильм №");

        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
    }

    @Test
    void getAllFilms() {
        Film film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание фильма 1");
        film1.setReleaseDate(LocalDate.of(2021, 1, 1));
        film1.setDuration(100);
        filmController.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание фильма 2");
        film2.setReleaseDate(LocalDate.of(2021, 2, 1));
        film2.setDuration(150);
        filmController.addFilm(film2);

        List<Film> allFilms = filmController.getAllFilms();

        assertThat(allFilms).hasSize(2);
        assertThat(allFilms).contains(film1, film2);
    }
}
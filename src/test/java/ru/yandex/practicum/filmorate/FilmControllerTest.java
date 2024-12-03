package ru.yandex.practicum.filmorate;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
    }

    @Test
    public void testAddFilm_success() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description of a test film.");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmController.addFilm(film);

        assertNotNull(createdFilm);
        assertEquals("Test Film", createdFilm.getName());
        assertEquals(1, createdFilm.getId()); // Проверка ID
    }

    @Test
    public void testUpdateFilm_success() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description of a test film.");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        filmController.addFilm(film);
        film.setName("Updated Film");

        Film updatedFilm = filmController.updateFilm(film);

        assertNotNull(updatedFilm);
        assertEquals("Updated Film", updatedFilm.getName());
    }

    @Test
    public void testGetAllFilms() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description of a test film.");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        filmController.addFilm(film);

        List<Film> films = filmController.getAllFilms();

        assertNotNull(films);
        assertEquals(1, films.size());
    }

    @Test
    public void testUpdateFilm_notFound() {
        Film film = new Film();
        film.setId(99); // ID не существует
        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(film);
        });

        assertEquals("Фильм с ID 99 не найден.", exception.getMessage());
    }

    @Test
    public void testFilmCreateFailReleaseDate() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(3000, 1, 1)); // Дата в будущем
        film.setDuration(100);

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });

        assertEquals("Дата выхода фильма не может быть в будущем.", exception.getMessage());
    }

    @Test
    public void testFilmCreateFailDuration() {
        Film film = new Film();
        film.setName("Film Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(-10); // Отрицательная продолжительность

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });

        assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
    }
}
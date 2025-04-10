package ru.yandex.practicum.filmorate.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerIntegrationTest {

    @Autowired
    private FilmController filmController;

    @Test
    void getAllFilms_shouldReturnAllFilms() {
        List<CreateFilmDto> films = filmController.getAllFilms();
        assertNotNull(films);
        assertEquals(5, films.size());
    }

    @Test
    void getFilm_shouldReturnFilm_whenFilmExists() {
        CreateFilmDto film = filmController.getFilm(1L).getBody();
        assertNotNull(film);
        assertEquals(1L, film.getId());
        assertEquals("Крадущийся тигр, затаившийся дракон", film.getName());
    }

    @Test
    void getFilm_shouldThrowException_whenFilmNotExists() {
        assertThrows(EntityNotFoundException.class, () -> filmController.getFilm(999L));
    }

    @Test
    void createFilm_shouldAddNewFilm() {
        CreateFilmDto newFilm = CreateFilmDto.builder()
                .name("New Film")
                .description("New Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(MpaDto.builder().id(1L).build())
                .build();

        CreateFilmDto createdFilm = filmController.createFilm(newFilm).getBody();
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());
        assertEquals("New Film", createdFilm.getName());

        List<CreateFilmDto> films = filmController.getAllFilms();
        assertEquals(6, films.size());
    }

    @Test
    void updateFilm_shouldUpdateExistingFilm() {
        CreateFilmDto film = filmController.getFilm(1L).getBody();
        assertNotNull(film);
        film.setName("Updated Name");

        Film updatedFilm = filmController.updateFilm(film);
        assertNotNull(updatedFilm);
        assertEquals(1L, updatedFilm.getId());
        assertEquals("Updated Name", updatedFilm.getName());

        CreateFilmDto fetchedFilm = filmController.getFilm(1L).getBody();
        assertEquals("Updated Name", fetchedFilm.getName());
    }

    @Test
    void addLike_shouldAddLikeToFilm() {
        filmController.addLike(1L, 3L);
        CreateFilmDto film = filmController.getFilm(1L).getBody();
        assertNotNull(film);
        assertTrue(filmController.getLikes(1L).getBody().contains(3L));
    }

    @Test
    void getFilmsByDirector_shouldReturnFilmsSortedByYear() {
        List<CreateFilmDto> films = filmController.getFilmsByDirector(1L, "year").getBody();
        assertNotNull(films);
        assertEquals(2, films.size());
        assertTrue(films.get(0).getReleaseDate().isBefore(films.get(1).getReleaseDate()));
    }

    @Test
    void searchFilms_shouldReturnMatchingFilms() {
        List<Film> films = filmController.searchFilms("Крадущийся", "title");
        assertNotNull(films);
        assertEquals(2, films.size());
    }
}
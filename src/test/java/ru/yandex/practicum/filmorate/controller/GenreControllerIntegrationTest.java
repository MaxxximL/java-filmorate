package ru.yandex.practicum.filmorate.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GenreControllerIntegrationTest {

    @Autowired
    private GenreController genreController;

    @Test
    void getAllGenres_shouldReturnAllGenres() {
        List<Genre> genres = genreController.getAllGenres().getBody();
        assertNotNull(genres);
        assertEquals(6, genres.size());
    }

    @Test
    void getGenre_shouldReturnGenre_whenGenreExists() {
        Genre genre = genreController.getGenre(1L).getBody();
        assertNotNull(genre);
        assertEquals(1L, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    void getGenre_shouldThrowException_whenGenreNotExists() {
        assertThrows(EntityNotFoundException.class, () -> genreController.getGenre(999L));
    }
}
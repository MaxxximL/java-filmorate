package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
class GenreDbStorageTest {

    @Autowired
    private GenreStorage genreStorage;

    @Test
    void getAllGenres_shouldReturnAllGenres() {
        List<Genre> genres = genreStorage.getAllGenres();
        assertNotNull(genres);
        assertEquals(6, genres.size());
    }

    @Test
    void getGenre_shouldReturnGenre_whenGenreExists() {
        Genre genre = genreStorage.getGenre(1L);
        assertNotNull(genre);
        assertEquals(1L, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    void getGenre_shouldReturnNull_whenGenreNotExists() {
        Genre genre = genreStorage.getGenre(999L);
        assertNull(genre);
    }

    @Test
    void updateFilmGenres_shouldUpdateGenresForFilm() {
        List<Genre> newGenres = List.of(
                Genre.builder().id(1L).build(),
                Genre.builder().id(2L).build()
        );

        genreStorage.updateFilmGenres(1L, newGenres);
        // Здесь можно добавить проверку через прямой запрос к БД или через другие методы
    }
}
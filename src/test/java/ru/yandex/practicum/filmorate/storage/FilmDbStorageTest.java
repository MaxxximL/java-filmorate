package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        FilmDbStorage.class,
        FilmRowMapper.class,
        GenreRowMapper.class,
        MpaRowMapper.class,
        GenreDbStorage.class,
        MpaDbStorage.class,
        DirectorDbStorage.class,  // Add this
        DirectorRowMapper.class   // Add this
})
class FilmDbStorageTest {

    @Autowired
    private FilmStorage filmStorage;

    @Test
    void getAllFilms_shouldReturnAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        assertNotNull(films);
        assertEquals(5, films.size());
    }

    @Test
    void getFilm_shouldReturnFilm_whenFilmExists() {
        Film film = filmStorage.getFilm(1L);
        assertNotNull(film);
        assertEquals(1L, film.getId());
        assertEquals("Крадущийся тигр, затаившийся дракон", film.getName());
    }

    @Test
    void getFilm_shouldReturnNull_whenFilmNotExists() {
        Film film = filmStorage.getFilm(999L);
        assertNull(film);
    }

    @Test
    void addFilm_shouldAddNewFilm() {
        Film newFilm = Film.builder()
                .name("New Film")
                .description("New Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(Mpa.builder().id(1L).build())
                .build();

        Film createdFilm = filmStorage.addFilm(newFilm);
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());
        assertEquals("New Film", createdFilm.getName());

        List<Film> films = filmStorage.getAllFilms();
        assertEquals(6, films.size());
    }

    @Test
    void updateFilm_shouldUpdateExistingFilm() {
        Film film = filmStorage.getFilm(1L);
        assertNotNull(film);
        film.setName("Updated Name");

        Film updatedFilm = filmStorage.updateFilm(film);
        assertNotNull(updatedFilm);
        assertEquals(1L, updatedFilm.getId());
        assertEquals("Updated Name", updatedFilm.getName());

        Film fetchedFilm = filmStorage.getFilm(1L);
        assertEquals("Updated Name", fetchedFilm.getName());
    }

    @Test
    void addLike_shouldAddLikeToFilm() {
        filmStorage.addLike(1L, 3L);
        Set<Long> likes = filmStorage.getLikes(1L);
        assertTrue(likes.contains(3L));
    }

    @Test
    void getMostLikedFilms_shouldReturnFilmsSortedByLikes() {
        List<Film> films = filmStorage.getMostLikedFilms(2);
        assertNotNull(films);
        assertEquals(2, films.size());
        assertEquals(1L, films.get(0).getId()); // Film with most likes
    }

    @Test
    void getFilmsByDirectorSortedByYear_shouldReturnFilmsSortedByYear() {
        List<Film> films = filmStorage.getFilmsByDirectorSortedByYear(1L);
        assertNotNull(films);
        assertEquals(2, films.size());
        assertTrue(films.get(0).getReleaseDate().isBefore(films.get(1).getReleaseDate()));
    }
}

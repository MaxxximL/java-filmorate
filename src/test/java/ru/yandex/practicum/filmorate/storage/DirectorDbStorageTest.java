package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({DirectorDbStorage.class, DirectorRowMapper.class}) // Add DirectorRowMapper here
class DirectorDbStorageTest {

    @Autowired
    private DirectorStorage directorStorage;

    @Test
    void getAllDirectors_shouldReturnAllDirectors() {
        List<Director> directors = directorStorage.getAllDirectors();
        assertNotNull(directors);
        assertEquals(3, directors.size());
    }

    @Test
    void getDirectorById_shouldReturnDirector_whenDirectorExists() {
        Optional<Director> director = directorStorage.getDirectorById(1L);
        assertTrue(director.isPresent());
        assertEquals(1L, director.get().getId());
        assertEquals("Christopher Nolan", director.get().getName());
    }

    @Test
    void getDirectorById_shouldReturnEmpty_whenDirectorNotExists() {
        Optional<Director> director = directorStorage.getDirectorById(999L);
        assertTrue(director.isEmpty());
    }

    @Test
    void createDirector_shouldAddNewDirector() {
        Director newDirector = Director.builder().name("New Director").build();
        Director createdDirector = directorStorage.createDirector(newDirector);

        assertNotNull(createdDirector);
        assertNotNull(createdDirector.getId());
        assertEquals("New Director", createdDirector.getName());

        List<Director> directors = directorStorage.getAllDirectors();
        assertEquals(4, directors.size());
    }

    @Test
    void updateDirector_shouldUpdateExistingDirector() {
        Director director = Director.builder().id(1L).name("Updated Name").build();
        Director updatedDirector = directorStorage.updateDirector(director);

        assertNotNull(updatedDirector);
        assertEquals(1L, updatedDirector.getId());
        assertEquals("Updated Name", updatedDirector.getName());

        Optional<Director> fetchedDirector = directorStorage.getDirectorById(1L);
        assertTrue(fetchedDirector.isPresent());
        assertEquals("Updated Name", fetchedDirector.get().getName());
    }

    @Test
    void deleteDirector_shouldRemoveDirector() {
        directorStorage.deleteDirector(1L);
        Optional<Director> director = directorStorage.getDirectorById(1L);
        assertTrue(director.isEmpty());
    }

    @Test
    void getDirectorsByFilmId_shouldReturnDirectorsForFilm() {
        List<Director> directors = directorStorage.getDirectorsByFilmId(1L);
        assertNotNull(directors);
        assertEquals(1, directors.size());
        assertEquals(1L, directors.get(0).getId());
    }
}
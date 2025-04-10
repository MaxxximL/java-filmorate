package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.DirectorDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DirectorControllerIntegrationTest {

    @Autowired
    private DirectorController directorController;

    @Test
    void getAllDirectors_shouldReturnAllDirectors() {
        List<DirectorDto> directors = directorController.getAllDirectors().getBody();
        assertNotNull(directors);
        assertEquals(3, directors.size());
    }

    @Test
    void getDirectorById_shouldReturnDirector_whenDirectorExists() {
        DirectorDto director = (DirectorDto) directorController.getDirectorById(1L).getBody();
        assertNotNull(director);
        assertEquals(1L, director.getId());
        assertEquals("Christopher Nolan", director.getName());
    }

    @Test
    void getDirectorById_shouldThrowException_whenDirectorNotExists() {
        ResponseEntity<?> response = directorController.getDirectorById(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createDirector_shouldAddNewDirector() {
        DirectorDto newDirector = DirectorDto.builder().name("New Director").build();
        DirectorDto createdDirector = directorController.createDirector(newDirector).getBody();

        assertNotNull(createdDirector);
        assertNotNull(createdDirector.getId());
        assertEquals("New Director", createdDirector.getName());

        List<DirectorDto> directors = directorController.getAllDirectors().getBody();
        assertEquals(4, directors.size());
    }

    @Test
    void updateDirector_shouldUpdateExistingDirector() {
        DirectorDto director = DirectorDto.builder().id(1L).name("Updated Name").build();
        DirectorDto updatedDirector = (DirectorDto) directorController.updateDirector(director).getBody();

        assertNotNull(updatedDirector);
        assertEquals(1L, updatedDirector.getId());
        assertEquals("Updated Name", updatedDirector.getName());

        DirectorDto fetchedDirector = (DirectorDto) directorController.getDirectorById(1L).getBody();
        assertEquals("Updated Name", fetchedDirector.getName());
    }

    @Test
    void deleteDirector_shouldRemoveDirector() {
        // Delete the director
        ResponseEntity<Void> deleteResponse = (ResponseEntity<Void>) directorController.deleteDirector(1L);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Try to get the deleted director
        ResponseEntity<?> getResponse = directorController.getDirectorById(1L);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
}

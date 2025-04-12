package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    private Film film;
    private User user;
    private TestRestTemplate restTemplate;


    @BeforeEach
    public void setUp() {
        film = new Film();
        film.setName("Film1");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);
        film = filmService.addFilm(film);


    user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    user = userService.addUser(user);
}

    @Test
    public void addFilmTest() {
        Film film = new Film();
        film.setName("Film1");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        ResponseEntity<Object> response = filmController.addFilm(film);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateFilmTest() {
        // Обновление фильма
        film.setDescription("Updated Description");
        ResponseEntity<Object> response = filmController.updateFilm(film);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Film updatedFilm = (Film) response.getBody();
        assertEquals("Updated Description", updatedFilm.getDescription());
    }

    @Test
    public void updateFilmNotFoundTest() {
        Film nonExistentFilm = new Film();
        nonExistentFilm.setId(999); // Используем несуществующий ID
        nonExistentFilm.setName("Non-existent Film");
        nonExistentFilm.setDescription("Doesn't matter");
        nonExistentFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        nonExistentFilm.setDuration(148);

        ResponseEntity<Object> response = filmController.updateFilm(nonExistentFilm);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getFilmTest() {
        // Ищем существующий фильм
        Film retrievedFilm = (Film) filmController.getFilm(film.getId()).getBody();
        assertEquals(film.getId(), retrievedFilm.getId());
        assertEquals(film.getName(), retrievedFilm.getName());
        assertEquals(film.getDescription(), retrievedFilm.getDescription());
        assertEquals(film.getReleaseDate(), retrievedFilm.getReleaseDate());
        assertEquals(film.getDuration(), retrievedFilm.getDuration());
    }

    @Test
    public void getFilmNotFoundTest() {
        // Запрос к несуществующему ID
        ResponseEntity<Object> response = filmController.getFilm(999);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    public void getAllFilmsTest() {

        Film film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(2010, 7, 16));
        film1.setDuration(148);
        filmService.addFilm(film1);



        List<Film> films = filmController.getAllFilms();

        assertEquals(film1.getName(), films.get(0).getName());

    }

    @Test
    public void addLikeTest() {

        ResponseEntity<Object> response = filmController.addLike(film.getId(), user.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Set<Long> likes = filmService.getLikes(film.getId());
        assertEquals(1, likes.size());
        assertEquals(user.getId(), likes.iterator().next());
    }


    @Test
    public void removeLikeTest() {

        filmController.addLike(film.getId(), user.getId());

        Set<Long> initialLikes = filmService.getLikes(film.getId());
        assertEquals(1, initialLikes.size());
        assertEquals(user.getId(), initialLikes.iterator().next());

        ResponseEntity<Object> response = filmController.removeLike(film.getId(), user.getId());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    }

    @Test
    public void getLikesTest() {
        ResponseEntity<Set<Long>> response = filmController.getLikes(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
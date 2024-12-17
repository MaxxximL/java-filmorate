package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        Film validFilm = new Film();
        validFilm.setName("Valid Film");
        validFilm.setDescription("Description of the film");
        validFilm.setReleaseDate(LocalDate.now());
        validFilm.setDuration(120);
        filmController = new FilmController();
    }

    @Test
    void shouldCreateFilmWhenValid() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Valid Film\", \"description\":\"Description\", \"releaseDate\": \"2023-03-25\", \"duration\": 120}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Valid Film")));
    }

    @Test
    public void whenFilmNameIsBlank_thenReturns400() throws Exception {
        Film film = new Film();
        film.setName(" ");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void whenFilmDurationIsNegative_thenReturns400() throws Exception {
        Film film = new Film();
        film.setDuration(-10);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
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


    @Test
    public void testFilmCreateFailDuration() throws Exception {
        Film film = new Film();
        film.setName("Another Valid Film");
        film.setDescription("Description of another valid film");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-50); // Неверная продолжительность

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenFilmReleaseDateIsInFuture_thenReturns400() throws Exception {
        Film film = new Film();
        film.setName("Future Film");
        film.setDescription("This film is set to release in the future.");
        film.setReleaseDate(LocalDate.of(3000, 1, 1));
        film.setDuration(120);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason", is("Дата релиза не может быть в будущем.")));
    }

}

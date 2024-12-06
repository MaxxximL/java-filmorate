package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")

public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final List<Film> films = new ArrayList<>();
    private int filmIdCounter = 1;

    @PostMapping
    public ResponseEntity<Object> addFilm(@Valid @RequestBody Film film) {

        film.setId(filmIdCounter++);
        films.add(film);
        log.info("Добавлен фильм: {}", film);
        if (films.contains(film)) {
            ResponseEntity.ok().body(film);
            return ResponseEntity.ok().body(film);
        }

        ru.yandex.practicum.filmorate.model.ErrorResponse errorResponseBody = ErrorResponse.builder().code(400).reasone("Имя не может быть пустым.").build();
        return new ResponseEntity<>(errorResponseBody, HttpStatusCode.valueOf(400));

    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.set(i, film);
                log.info("Обновлён фильм: {}", film);
                return film;
            }
        }
        throw new ValidationException("Фильм с ID " + film.getId() + " не найден.");
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрошены все фильмы");
        return films;

    }
}
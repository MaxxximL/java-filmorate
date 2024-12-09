package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final List<Film> films = new ArrayList<>();
    private int filmIdCounter = 1;

    @PostMapping
    public ResponseEntity<Object> addFilm(@RequestBody Film film) {
        List<String> validationErrors = validateFilm(film);
        if (!validationErrors.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode(400);
            errorResponse.setReason(String.join(", ", validationErrors));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        film.setId(filmIdCounter++);
        films.add(film);
        log.info("Добавлен фильм: {}", film);

        return ResponseEntity.ok().body(film);
    }

    @PutMapping
    public ResponseEntity<Object> updateFilm(@RequestBody Film film) {
        List<String> validationErrors = validateFilm(film);
        if (!validationErrors.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode(400);
            errorResponse.setReason(String.join(", ", validationErrors));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.set(i, film);
                log.info("Обновлён фильм: {}", film);
                return ResponseEntity.ok().body(film);
            }
        }
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(404);
        errorResponse.setReason("Фильм с ID " + film.getId() + " не найден.");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрошены все фильмы");
        return films;
    }

    private List<String> validateFilm(Film film) {
        List<String> errors = new ArrayList<>();
        if (film.getName() == null || film.getName().isBlank()) {
            errors.add("Имя фильма не может быть пустым.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            errors.add("Максимальная длина описания — 200 символов.");
        }
        if (film.getDuration() <= 0) {
            errors.add("Продолжительность фильма должна быть положительной.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 27))) {
            errors.add("Дата выпуска фильма не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isAfter(LocalDate.now())) {
            errors.add("Дата релиза не может быть в будущем.");
        }
        return errors;
    }
}
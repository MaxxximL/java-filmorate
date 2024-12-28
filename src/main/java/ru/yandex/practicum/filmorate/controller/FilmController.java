package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ErrorResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<Object> addFilm(@RequestBody Film film) {
        List<String> validationErrors = filmService.validateFilm(film);
        if (!validationErrors.isEmpty()) {
            String errorMessage = "Film validation failed: " + String.join(", ", validationErrors);
            return ResponseEntity.badRequest().body(new ErrorResponse(errorMessage));
        }
        return ResponseEntity.ok(filmService.addFilm(film));
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        try {
            return ResponseEntity.ok(filmService.updateFilm(film));
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Film not found: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable long id) {
        try {
            Film film = filmService.getFilm(id);
            return ResponseEntity.ok(film);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<ErrorResponse> addLike(@PathVariable long id, @PathVariable long userId) {
        try {
            filmService.addLike(id, userId);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage())); // 404 NOT FOUND
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<ErrorResponse> removeLike(@PathVariable long id, @PathVariable long userId) {
        try {
            filmService.removeLike(id, userId);
            return ResponseEntity.noContent().build(); // 204 NO CONTENT
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage())); // 404 NOT FOUND
        }
    }

    @GetMapping("/popular")
    public List<Film> getMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getMostLikedFilms(count);
    }
}

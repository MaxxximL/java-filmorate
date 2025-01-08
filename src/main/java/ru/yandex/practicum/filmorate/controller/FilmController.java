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
import java.util.Set;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<Object> addFilm(@RequestBody Film film) {
        try {
            return ResponseEntity.ok(filmService.addFilm(film));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Film validation failed: " + e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<Object> updateFilm(@RequestBody Film film) {
        try {
            return ResponseEntity.ok(filmService.updateFilm(film));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Film not found: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFilm(@PathVariable long id) {
        try {
            return ResponseEntity.ok(filmService.getFilm(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Film not found with id: " + id));
        }
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Object> addLike(@PathVariable long id, @PathVariable long userId) {
        try {
            filmService.addLike(id, userId);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Film or user not found: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Object> removeLike(@PathVariable long id, @PathVariable long userId) {
        try {
            filmService.removeLike(id, userId);
            return ResponseEntity.noContent().build(); // 204 NO CONTENT
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Film or user not found: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<Set<Long>> getLikes(@PathVariable long id) {
        Set<Long> likes = filmService.getLikes(id);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/popular")
    public List<Film> getMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getMostLikedFilms(count);
    }
}
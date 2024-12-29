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
    private UserController userService;

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
    public ResponseEntity<Object> updateFilm(@RequestBody Film film) {
        try {
            return ResponseEntity.ok(filmService.updateFilm(film));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Film not found: " + e.getMessage())); // Вернуть 404 Not Found
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
    public ResponseEntity<Void> addLike(@PathVariable long id, @PathVariable long userId) {
        try {
            filmService.addLike(id, userId);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 NOT FOUND без сообщения
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable long id, @PathVariable long userId) {
        try {
            filmService.removeLike(id, userId);
            return ResponseEntity.noContent().build(); // 204 NO CONTENT
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 NOT FOUND без сообщения
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

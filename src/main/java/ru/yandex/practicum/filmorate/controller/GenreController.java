package ru.yandex.practicum.filmorate.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreStorage genreStorage;

    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = genreStorage.getAllGenres();
        if (genres.isEmpty()) { // если список жанров пустой
            return ResponseEntity.notFound().build(); // возврат 404
        }
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenre(@PathVariable long id) {
        Genre genre = genreStorage.getGenre(id);
        if (genre == null) {
            throw new EntityNotFoundException("Genre with id " + id + " not found."); // бросаем исключение, если жанр не найден
        }
        return ResponseEntity.ok(genre);
    }
}
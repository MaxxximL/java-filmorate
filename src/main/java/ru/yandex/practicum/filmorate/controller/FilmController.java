package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final GenreStorage genreStorage; // Добавляем хранилище для жанров

    @PostMapping
    public ResponseEntity<CreateFilmDto> createFilm(@RequestBody CreateFilmDto filmDto) {
        if (filmDto == null) {
            return ResponseEntity.badRequest().build(); // 400 если `filmDto` == null
        }
        return ResponseEntity.ok(filmService.addFilm(filmDto));
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody CreateFilmDto filmDto) {
        if (filmDto == null || filmDto.getId() <= 0) {
            return ResponseEntity.badRequest().build(); // 400 если `filmDto` == null или невалидный id
        }
        return ResponseEntity.ok(filmService.updateFilm(filmDto.getId(), filmDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreateFilmDto> getFilm(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().build(); // 400 если `id` == null
        }
        Film film = filmService.getFilm(id);
        return film != null ? ResponseEntity.ok(FilmMapper.toDto(film)) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<CreateFilmDto> getAllFilms() {
        return filmService.getAllFilms().stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<CreateFilmDto>> getMostPopularFilms(@RequestParam(defaultValue = "1000") int count) {
        return ResponseEntity.ok(filmService.getMostLikedFilms(count).stream()
                .map(FilmMapper::toDto)
                .toList());
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<Set<Long>> getLikes(@PathVariable long id) {
        Set<Long> likes = filmService.getLikes(id);
        return ResponseEntity.ok(likes);
    }


}
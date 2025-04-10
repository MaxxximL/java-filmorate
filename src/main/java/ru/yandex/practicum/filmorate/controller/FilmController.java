package ru.yandex.practicum.filmorate.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final GenreStorage genreStorage; // Добавляем хранилище для жанров
    private final DirectorService directorService;

    @PostMapping
    public ResponseEntity<CreateFilmDto> createFilm(@RequestBody CreateFilmDto filmDto) {
        // Validate genres exist
        if (filmDto.getGenres() != null) {
            for (GenreDto genre : filmDto.getGenres()) {
                if (genreStorage.getGenre(genre.getId()) == null) {
                    throw new EntityNotFoundException("Genre with id " + genre.getId() + " not found");
                }
            }
        }
        return ResponseEntity.ok(filmService.addFilm(filmDto));
    }

    @PutMapping
    public Film updateFilm(@RequestBody CreateFilmDto filmDto) {
        return filmService.updateFilm(filmDto.getId(), filmDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreateFilmDto> getFilm(@PathVariable long id) {
        Film film = filmService.getFilm(id);
        return ResponseEntity.ok(FilmMapper.toDto(film));
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

    @GetMapping("/director/{directorId}")
    public ResponseEntity<List<CreateFilmDto>> getFilmsByDirector(
            @PathVariable long directorId,
            @RequestParam(required = false) String sortBy) {

        directorService.getDirectorById(directorId); // Validate director exists

        List<Film> films;
        if ("year".equals(sortBy)) {
            films = filmService.getFilmsByDirectorSortedByYear(directorId);
        } else {
            films = filmService.getFilmsByDirectorSortedByLikes(directorId);
        }

        List<CreateFilmDto> filmDtos = films.stream()
                .map(FilmMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filmDtos);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(
            @RequestParam String query,
            @RequestParam(defaultValue = "title") String by) {

        List<String> searchBy = Arrays.stream(by.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        return filmService.searchFilms(query, searchBy);
    }
}


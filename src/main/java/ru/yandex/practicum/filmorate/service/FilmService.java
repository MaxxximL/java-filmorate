package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int filmIdCounter = 1;
    private List<String> validationErrors = new ArrayList<>();

    public Film addFilm(Film film) {
        validateFilm(film);
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Неправильная валидация фильма");
        }

        film.setId(filmIdCounter++);
        log.info("Добавлен фильм: {}", film);

        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (filmStorage.getFilm(film.getId()) == null) {
            throw new EntityNotFoundException("Film not found: " + film.getId());
        }
        validateFilm(film);
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Неправильная валидация фильма");
        }
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(long id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new EntityNotFoundException("Film not found with id: " + id);
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(long filmId, long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new EntityNotFoundException("Film not found: " + filmId);
        }
        if (userStorage.getUser(userId) == null) {
            throw new EntityNotFoundException("User not found: " + userId);
        }
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new EntityNotFoundException("Film not found: " + filmId);
        }
        if (userStorage.getUser(userId) == null) {
            throw new EntityNotFoundException("User not found: " + userId);
        }
        filmStorage.removeLike(filmId, userId);
    }

    public Set<Long> getLikes(long filmId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new EntityNotFoundException("Film not found: " + filmId);
        }
        return filmStorage.getLikes(filmId);
    }

    public List<Film> getMostLikedFilms(int count) {
        return filmStorage.getMostLikedFilms(count);
    }

    private boolean validateFilm(Film film) {
        validationErrors.clear();
        if (film.getName() == null || film.getName().isBlank()) {
            validationErrors.add("Имя фильма не может быть пустым.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            validationErrors.add("Максимальная длина описания — 200 символов.");
        }
        if (film.getDuration() <= 0) {
            validationErrors.add("Продолжительность фильма должна быть положительной.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 27))) {
            validationErrors.add("Дата выпуска фильма не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isAfter(LocalDate.now())) {
            validationErrors.add("Дата релиза не может быть в будущем.");
        }
        return validationErrors.isEmpty();
    }

    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }
}
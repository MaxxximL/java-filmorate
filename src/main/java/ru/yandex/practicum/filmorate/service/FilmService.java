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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int filmIdCounter = 1;

    public Film addFilm(Film film) {
        List<String> validationErrors = validateFilm(film);
        if (!validationErrors.isEmpty()) {
            log.error("Неправильная валидация фильма: {}", String.join(", ", validationErrors));
            throw new ValidationException("Неправильная валидация фильма");
        }

        film.setId(filmIdCounter++);
        log.info("Добавлен фильм: {}", film);

        try {
            return filmStorage.addFilm(film);
        } catch (Exception e) {
            log.error("Ошибка при добавлении фильма: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при добавлении фильма");
        }
    }

    public Film updateFilm(Film film) {
        if (filmStorage.getFilm(film.getId()) == null) {
            log.error("Фильм не найден: {}", film.getId());
            throw new EntityNotFoundException("Фильм не найден");
        }
        validateFilm(film);
        try {
            return filmStorage.updateFilm(film);
        } catch (Exception e) {
            log.error("Ошибка при обновлении фильма: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при обновлении фильма");
        }
    }

    public Film getFilm(long id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            log.error("Фильм не найден: {}", id);
            throw new EntityNotFoundException("Фильм не найден");
        }
        return film;
    }

    
    public List<Film> getAllFilms() {
        try {
            return filmStorage.getAllFilms();
        } catch (Exception e) {
            log.error("Ошибка при получении всех фильмов: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при получении всех фильмов");
        }
    }

    public void addLike(long filmId, long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            log.error("Фильм не найден: {}", filmId);
            throw new EntityNotFoundException("Фильм не найден");
        }
        try {
            filmStorage.addLike(filmId, userId);
        } catch (Exception e) {
            log.error("Ошибка при добавлении лайка: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при добавлении лайка");
        }
    }

    public void removeLike(long filmId, long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            log.error("Фильм не найден: {}", filmId);
            throw new EntityNotFoundException("Фильм не найден");
        }
        try {
            filmStorage.removeLike(filmId, userId);
        } catch (Exception e) {
            log.error("Ошибка при удалении лайка: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при удалении лайка");
        }
    }

    public List<Film> getMostLikedFilms(int count) {
        try {
            return filmStorage.getMostLikedFilms(count);
        } catch (Exception e) {
            log.error("Ошибка при получении самых популярных фильмов: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при получении самых популярных фильмов");
        }
    }

    public List<String> validateFilm(Film film) {
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

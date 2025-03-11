package ru.yandex.practicum.filmorate.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    private final JdbcTemplate jdbcTemplate;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage,
                       FilmMapper filmMapper,
                       JdbcTemplate jdbcTemplate) { // Добавлено
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.jdbcTemplate = jdbcTemplate;// Добавлено
    }

    @Transactional
    public CreateFilmDto addFilm(CreateFilmDto filmDto) {

        if (filmDto.getMpa() == null) {
            throw new ValidationException("MPA id должно быть указано.");
        }

        // Проверяем, переданы ли жанры
        if (filmDto.getGenres() == null || filmDto.getGenres().isEmpty()) {
            throw new ValidationException("Фильм должен содержать хотя бы один жанр.");
        }

        Film newFilm = FilmMapper.toModel(filmDto);
        validateFilm(newFilm);

        // Получение и установка MPA
        Mpa mpa = mpaStorage.getMpa(filmDto.getMpa().getId());
        newFilm.setMpaId(mpa.getId()); // Убедитесь, что mpaId устанавливается

        // Сохранение фильма
        Film createdFilm = filmStorage.save(newFilm);

        // Обновляем жанры
        updateGenres(createdFilm.getId(), filmDto.getGenres().stream().map(GenreDto::getId).collect(Collectors.toList()));

        return FilmMapper.toDto(createdFilm);
    }

    public Film updateFilm(long id, CreateFilmDto filmDto) {
        Film existingFilm = filmStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Film with id " + id + " not found."));

        // Преобразование DTO в модель
        Film updatedFilm = FilmMapper.toModel(filmDto);
        updatedFilm.setId(id);

        // Проверка MPA
        if (filmDto.getMpa() != null) {
            Mpa mpa = mpaStorage.getMpa(filmDto.getMpa().getId());
            if (mpa == null) {
                throw new EntityNotFoundException("MPA с id " + filmDto.getMpa().getId() + " не найден.");
            }
            updatedFilm.setMpa(mpa);
        } else {
            updatedFilm.setMpa(existingFilm.getMpa()); // Если mpa не передан, используем существующее значение
        }

        // Обновляем жанры
        updateGenres(id, filmDto.getGenreIds());

        return filmStorage.save(updatedFilm);
    }

    public Film getFilm(long id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new EntityNotFoundException("Film with id " + id + " not found.");
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(long filmId, long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new EntityNotFoundException("Film with id " + filmId + " not found.");
        }
        if (userStorage.getUser(userId) == null) {
            throw new EntityNotFoundException("User with id " + userId + " not found.");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new EntityNotFoundException("Film with id " + filmId + " not found.");
        }
        if (userStorage.getUser(userId) == null) {
            throw new EntityNotFoundException("User with id " + userId + " not found.");
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

    private void validateFilm(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }

        // Убедитесь, что жанры не пустые
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            throw new ValidationException("Фильм должен содержать хотя бы один жанр.");
        }

    }

    private void updateGenres(long filmId, List<Long> genreIds) {
        deleteFilmGenres(filmId);

        if (genreIds != null && !genreIds.isEmpty()) {
            for (Long genreId : genreIds) {
                addFilmGenre(filmId, genreId);
            }
        }
    }

    private void deleteFilmGenres(long filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);  // Выполняем удаление
    }

    private void addFilmGenre(long filmId, long genreId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new EntityNotFoundException("Film with id " + filmId + " not found.");
        }

        if (genreStorage.getGenre(genreId) == null) {
            throw new EntityNotFoundException("Genre with id " + genreId + " not found.");
        }

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);  // Выполняем добавление
    }
}
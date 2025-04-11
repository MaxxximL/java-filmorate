package ru.yandex.practicum.filmorate.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final DirectorService directorService;

    private final FilmMapper filmMapper;
    private final JdbcTemplate jdbcTemplate;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage,
                       DirectorStorage directorStorage,
                       FilmMapper filmMapper,
                       DirectorService directorService,
                       JdbcTemplate jdbcTemplate) { // Добавлено
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.filmMapper = filmMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.directorStorage = directorStorage;
        this.directorService = directorService;
    }

    @Transactional
    public CreateFilmDto addFilm(CreateFilmDto filmDto) {
        Film newFilm = FilmMapper.toModel(filmDto);
        validateFilm(newFilm);

        // Validate and set MPA
        if (filmDto.getMpa() == null || filmDto.getMpa().getId() == null) {
            throw new ValidationException("MPA cannot be empty.");
        }
        Mpa mpa = mpaStorage.getMpa(filmDto.getMpa().getId());
        if (mpa == null) {
            throw new EntityNotFoundException("MPA with id " + filmDto.getMpa().getId() + " not found.");
        }
        newFilm.setMpa(mpa);

        // Save film first to get ID
        Film createdFilm = filmStorage.save(newFilm);

        // Save genres
        if (filmDto.getGenres() != null && !filmDto.getGenres().isEmpty()) {
            updateGenres(createdFilm.getId(),
                    filmDto.getGenres().stream()
                            .map(GenreDto::getId)
                            .collect(Collectors.toList()));
        }

        // Save directors
        if (filmDto.getDirectors() != null && !filmDto.getDirectors().isEmpty()) {
            updateDirectors(createdFilm.getId(),
                    filmDto.getDirectors().stream()
                            .map(DirectorDto::getId)
                            .collect(Collectors.toList()));
        }

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
                throw new EntityNotFoundException("MPA с id " + filmDto.getMpa().getId() + " не найдена.");
            }
            updatedFilm.setMpa(mpa);
        } else {
            if (existingFilm.getMpa() != null) {
                updatedFilm.setMpa(existingFilm.getMpa());
            } else {
                throw new ValidationException("MPA не может быть пустым.");
            }
        }

        // Update directors
        if (filmDto.getDirectors() != null) {
            updateDirectors(id,
                    filmDto.getDirectors().stream()
                            .map(DirectorDto::getId)
                            .collect(Collectors.toList()));
        }

        return filmStorage.save(updatedFilm);
    }

    private void updateDirectors(long filmId, List<Long> directorIds) {
        directorStorage.removeDirectorsFromFilm(filmId);

        if (directorIds != null && !directorIds.isEmpty()) {
            Set<Long> uniqueDirectorIds = new HashSet<>(directorIds);

            for (Long directorId : uniqueDirectorIds) {
                directorStorage.addDirectorToFilm(filmId, directorId);
            }
        }
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

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }

        // Валидация поля description
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new ValidationException("Описание фильма не может быть пустым или null.");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }

        if (film.getMpa() == null) {
            throw new FilmNotFoundException("Фильм должен содержать MPA.");
        }

        // Validate genres
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genreStorage.getGenre(genre.getId()) == null) {
                    throw new EntityNotFoundException("Genre with id " + genre.getId() + " not found.");
                }
            }
        }
    }

    private void updateGenres(long filmId, List<Long> genreIds) {
        deleteFilmGenres(filmId);

        if (genreIds != null && !genreIds.isEmpty()) {
            // Remove duplicates by converting to Set and back to List
            Set<Long> uniqueGenreIds = new HashSet<>(genreIds);

            for (Long genreId : uniqueGenreIds) {
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

    public Mpa getMpa(long id) {
        Mpa mpa = mpaStorage.getMpa(id);
        if (mpa == null) {
            throw new EntityNotFoundException("MPA with id " + id + " not found.");
        }
        return mpa;
    }

    public List<Film> getFilmsByDirectorSortedByYear(long directorId) {
        directorService.getDirectorById(directorId); // Validate director exists
        return filmStorage.getFilmsByDirectorSortedByYear(directorId);
    }

    public List<Film> getFilmsByDirectorSortedByLikes(long directorId) {
        directorService.getDirectorById(directorId); // Validate director exists
        return filmStorage.getFilmsByDirectorSortedByLikes(directorId);
    }

    public List<Film> searchFilms(String query, List<String> by) {
        return filmStorage.searchFilms(query, by);
    }

}



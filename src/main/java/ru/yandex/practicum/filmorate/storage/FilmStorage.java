package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(long id);

    List<Film> getAllFilms();

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getMostLikedFilms(int count);

    Set<Long> getLikes(long filmId); // метод для получения уникальных лайков

    Optional<Film> findById(long id);

    Film save(Film film);

    List<Film> getFilmsByDirectorSortedByYear(long directorId);

    List<Film> getFilmsByDirectorSortedByLikes(long directorId);

    List<Film> searchFilms(String query, List<String> by);

}
package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    List<Genre> getAllGenres();

    Genre getGenre(long id);

    void updateFilmGenres(long filmId, List<Genre> genres);

    void addFilmGenre(long filmId, long genreId);
}
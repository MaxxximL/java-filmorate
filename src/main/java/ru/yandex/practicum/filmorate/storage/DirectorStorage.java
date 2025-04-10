package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(long id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(long id);

    List<Director> getDirectorsByFilmId(long filmId);

    void addDirectorToFilm(long filmId, long directorId);

    void removeDirectorsFromFilm(long filmId);
}
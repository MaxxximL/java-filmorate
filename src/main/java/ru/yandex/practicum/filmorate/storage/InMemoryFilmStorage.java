package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();
    private long idCounter = 1;

    @Override
    public Film addFilm(Film film) {
        film.setId(idCounter++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilm(long id) {
        return films.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(long filmId, long userId) {
        filmLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }


    @Override
    public void removeLike(long filmId, long userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.getLikes().remove(userId); // убираем уникальный лайк
        }
    }

    @Override
    public Set<Long> getLikes(long filmId) {
        return filmLikes.getOrDefault(filmId, Collections.emptySet());
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        return filmLikes.entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .limit(count)
                .map(e -> films.get(e.getKey()))
                .toList();
    }
}
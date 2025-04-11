package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Primary
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    @Override
    public Film getFilm(long id) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, filmRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, filmRowMapper);
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sqlQuery = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        String sqlQuery = "SELECT f.* " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, filmRowMapper, count);
    }

    @Override
    public Set<Long> getLikes(long filmId) {
        String sqlQuery = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Long.class, filmId));
    }

    @Override
    public Optional<Film> findById(long id) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, filmRowMapper, id);
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film save(Film film) {
        if (findById(film.getId()).isPresent()) {
            updateFilm(film);
            return film;
        } else {
            addFilm(film);
            return film;
        }
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByYear(long directorId) {
        String sql = "SELECT f.* FROM films f " +
                "JOIN film_directors fd ON f.id = fd.film_id " +
                "WHERE fd.director_id = ? " +
                "ORDER BY f.release_date";
        return jdbcTemplate.query(sql, filmRowMapper, directorId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByLikes(long directorId) {
        String sql = "SELECT f.* FROM films f " +
                "JOIN film_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.user_id) DESC";
        return jdbcTemplate.query(sql, filmRowMapper, directorId);
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by) {
        String sql;
        if (by.contains("title") && by.contains("director")) {
            sql = "SELECT f.* FROM films f " +
                    "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors d ON fd.director_id = d.id " +
                    "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                    "WHERE LOWER(f.name) LIKE LOWER(?) OR LOWER(d.name) LIKE LOWER(?) " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(fl.user_id) DESC";
            return jdbcTemplate.query(sql, filmRowMapper, "%" + query + "%", "%" + query + "%");
        } else if (by.contains("director")) {
            sql = "SELECT f.* FROM films f " +
                    "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors d ON fd.director_id = d.id " +
                    "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                    "WHERE LOWER(d.name) LIKE LOWER(?) " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(fl.user_id) DESC";
            return jdbcTemplate.query(sql, filmRowMapper, "%" + query + "%");
        } else {
            // Default to title search
            sql = "SELECT f.* FROM films f " +
                    "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                    "WHERE LOWER(f.name) LIKE LOWER(?) " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(fl.user_id) DESC";
            return jdbcTemplate.query(sql, filmRowMapper, "%" + query + "%");
        }
    }
}


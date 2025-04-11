package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
            return jdbcTemplate.query(sql, genreRowMapper);
    }

        @Override
        public Genre getGenre(long id) {
            String sql = "SELECT * FROM genres WHERE id=?";
            try {
                return jdbcTemplate.queryForObject(sql, new GenreRowMapper(), id);
            } catch (EmptyResultDataAccessException e) {
                return null; // возвращаем null, если жанр не найден
            }
        }

    public void updateFilmGenres(long filmId, List<Genre> genres) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);

        for (Genre genre : genres) {
            addFilmGenre(filmId, genre.getId());
        }
    }

    public void addFilmGenre(long filmId, long genreId) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

}
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
    }
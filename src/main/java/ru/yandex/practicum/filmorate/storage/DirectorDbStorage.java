package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMapper directorRowMapper;

    @Override
    public List<Director> getAllDirectors() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, directorRowMapper);
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        String sql = "SELECT * FROM directors WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, directorRowMapper, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Director createDirector(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(long id) {
        // First remove all film-director associations
        String deleteFilmDirectorsSql = "DELETE FROM film_directors WHERE director_id = ?";
        jdbcTemplate.update(deleteFilmDirectorsSql, id);

        // Then delete the director
        String deleteDirectorSql = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(deleteDirectorSql, id);
    }

    @Override
    public List<Director> getDirectorsByFilmId(long filmId) {
        String sql = "SELECT d.* FROM directors d JOIN film_directors fd ON d.id = fd.director_id WHERE fd.film_id = ?";
        return jdbcTemplate.query(sql, directorRowMapper, filmId);
    }

    @Override
    public void addDirectorToFilm(long filmId, long directorId) {
        String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, directorId);
    }

    @Override
    public void removeDirectorsFromFilm(long filmId) {
        String sql = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}
package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        long filmId = rs.getLong("id");
        Film film = Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .build();

        // Set MPA
        Long mpaId = rs.getLong("mpa_id");
        Mpa mpa = mpaStorage.getMpa(mpaId);
        film.setMpa(mpa);

        // Set genres
        List<Genre> genres = getGenresForFilm(filmId);
        film.setGenres(genres);
        film.setGenreIds(genres.stream().map(Genre::getId).collect(Collectors.toList()));

        // Set directors
        List<Director> directors = directorStorage.getDirectorsByFilmId(filmId);
        film.setDirectors(directors);

        return film;
    }

    private List<Genre> getGenresForFilm(long filmId) {
        String sql = "SELECT g.* FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, genreRowMapper, filmId);
    }
}
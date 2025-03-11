package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final JdbcTemplate jdbcTemplate;
    private RowMapper<Genre> genreRowMapper;


    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        long filmId = rs.getLong("id");
        Film film = Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpaId(rs.getLong("mpa_id"))

                .build();

        //Set genres
        List<Long> genreIds = getGenreIdsForFilm(filmId);
        film.setGenreIds(genreIds);

        return film;
    }

    private List<Long> getGenreIdsForFilm(long filmId) {
        String sql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, filmId);
    }

}
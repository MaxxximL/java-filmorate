package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRowMapper implements RowMapper<Mpa> {

    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {

        return Mpa.builder()
                .id(rs.getLong(1))
                .name(rs.getString(2))
                .build();

    }
}

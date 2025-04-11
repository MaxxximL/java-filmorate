package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class, MpaRowMapper.class})
class MpaDbStorageTest {

    @Autowired
    private MpaStorage mpaStorage;

    @Test
    void getAllMpa_shouldReturnAllMpa() {
        List<Mpa> mpaList = mpaStorage.getAllMpa();
        assertNotNull(mpaList);
        assertEquals(5, mpaList.size());
    }

    @Test
    void getMpa_shouldReturnMpa_whenMpaExists() {
        Mpa mpa = mpaStorage.getMpa(1L);
        assertNotNull(mpa);
        assertEquals(1L, mpa.getId());
        assertEquals("G", mpa.getName());
    }

    @Test
    void getMpa_shouldReturnNull_whenMpaNotExists() {
        Mpa mpa = mpaStorage.getMpa(999L);
        assertNull(mpa);
    }

    @Test
    void getIdOfMpa_shouldReturnId() {
        Mpa mpa = mpaStorage.getMpa(1L);
        assertNotNull(mpa);
        assertEquals(1L, mpaStorage.getIdOfMpa(mpa));
    }
}
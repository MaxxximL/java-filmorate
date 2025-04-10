package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MpaControllerIntegrationTest {

    @Autowired
    private MpaController mpaController;

    @Test
    void getAllMpa_shouldReturnAllMpa() {
        List<Mpa> mpaList = mpaController.getAllMpa();
        assertNotNull(mpaList);
        assertEquals(5, mpaList.size());
    }

    @Test
    void getMpa_shouldReturnMpa_whenMpaExists() {
        MpaDto mpa = mpaController.getMpa(1L).getBody();
        assertNotNull(mpa);
        assertEquals(1L, mpa.getId());
        assertEquals("G", mpa.getName());
    }

    @Test
    void getMpa_shouldReturnNotFound_whenMpaNotExists() {
        assertNull(mpaController.getMpa(999L).getBody());
    }
}
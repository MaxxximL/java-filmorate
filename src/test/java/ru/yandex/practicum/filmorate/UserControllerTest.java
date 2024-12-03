package ru.yandex.practicum.filmorate;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    @Test
    public void testCreateUser_success() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test.user@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser);
        assertEquals("Test User", createdUser.getName());
        assertEquals(1, createdUser.getId()); // Проверка ID
    }

    @Test
    public void testUpdateUser_success() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test.user@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        userController.createUser(user);

        user.setName("Updated Name");
        User updatedUser = userController.updateUser(user);

        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    public void testGetAllUsers() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test.user@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        userController.createUser(user);

        List<User> users = userController.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    public void testUpdateUser_notFound() {
        User user = new User();
        user.setId(99); // ID не существует
        Exception exception = assertThrows(ValidationException.class, () -> userController.updateUser(user));

        assertEquals("Пользователь с ID 99 не найден.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithEmptyName() {
        User user = new User();
        user.setEmail("test.user@gmail.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setName(""); // Имя пустое

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));

        assertEquals("Имя не может быть пустым.", exception.getMessage());
    }
}
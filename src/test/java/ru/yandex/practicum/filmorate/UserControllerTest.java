package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void createUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Тестовый пользователь");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.createUser(user);

        assertThat(createdUser.getId()).isEqualTo(1); // Первый пользователь с ID 1
        assertThat(createdUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Тестовый пользователь");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.createUser(user);
        createdUser.setName("Обновленный пользователь");

        User updatedUser = userController.updateUser(createdUser);

        assertThat(updatedUser.getName()).isEqualTo("Обновленный пользователь");
    }

    @Test
    void updateNonExistentUser() {
        User user = new User();
        user.setId(999); // Не существующий ID
        user.setEmail("notfound@example.com");

        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }

    @Test
    void getAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("Пользователь 1");
        user1.setBirthday(LocalDate.of(1995, 1, 1));
        userController.createUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("Пользователь 2");
        user2.setBirthday(LocalDate.of(1996, 1, 1));
        userController.createUser(user2);

        List<User> allUsers = userController.getAllUsers();

        assertThat(allUsers).hasSize(2);
        assertThat(allUsers).contains(user1, user2);
    }
}
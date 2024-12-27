package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private int userIdCounter = 1;

    public User addUser(User user) {
        List<String> validationErrors = validateUser(user);
        if (!validationErrors.isEmpty()) {

            throw new ValidationException("Неправильная валидация юзера");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(userIdCounter++);
        log.info("Добавлен пользователь: {}", user);

        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.getUser(user.getId()) == null) {
            throw new EntityNotFoundException("User not found: " + user.getId());
        }
        validateUser(user);
        return userStorage.updateUser(user);
    }

    public User getUser(long id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        return user;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(long userId, long friendId) {
        if (userStorage.getUser(userId) == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        if (userStorage.getUser(friendId) == null) {
            throw new EntityNotFoundException("Friend not found with ID: " + friendId);
        }

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        if (userStorage.getUser(userId) == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        if (userStorage.getUser(friendId) == null) {
            throw new EntityNotFoundException("Friend not found with ID: " + friendId);
        }
        userStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getFriends(long userId) {
        if (userStorage.getUser(userId) == null) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        return userStorage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    private List<String> validateUser(User user) {
        List<String> errors = new ArrayList<>();
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.add("Некорректный email.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            errors.add("Логин не может быть пустым.");
        }
        if (user.getName() != null && user.getName().isBlank()) {
            errors.add("Имя не может быть пустым.");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            errors.add("Дата рождения не может быть в будущем.");
        }
        return errors;
    }
}
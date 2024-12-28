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

            log.error("Неправильная валидация юзера: {}", String.join(", ", validationErrors));
            throw new ValidationException("Неправильная валидация юзера");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(userIdCounter++);
        log.info("Добавлен пользователь: {}", user);

        try {
            return userStorage.addUser(user);
        } catch (Exception e) {
            log.error("Ошибка при добавлении пользователя: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при добавлении пользователя");
        }
    }

    public User updateUser(User user) {
        if (userStorage.getUser(user.getId()) == null) {
            log.error("Пользователь не найден: {}", user.getId());
            throw new EntityNotFoundException("Пользователь не найден");
        }
        validateUser(user);
        try {
            return userStorage.updateUser(user);
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при обновлении пользователя");
        }
    }

    public User getUser(long id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            log.error("Пользователь не найден: {}", id);
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return user;
    }

    public Collection<User> getAllUsers() {
        try {
            return userStorage.getAllUsers();
        } catch (Exception e) {
            log.error("Ошибка при получении всех пользователей: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при получении всех пользователей");
        }
    }

    public void addFriend(long userId, long friendId) {
        if (userStorage.getUser(userId) == null) {
            log.error("Пользователь не найден: {}", userId);
            throw new EntityNotFoundException("Пользователь не найден");
        }

        if (userStorage.getUser(friendId) == null) {
            log.error("Друг не найден: {}", friendId);
            throw new EntityNotFoundException("Друг не найден");
        }

        try {
            userStorage.addFriend(userId, friendId);
        } catch (Exception e) {
            log.error("Ошибка при добавлении друга: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при добавлении друга");
        }
    }

    public void removeFriend(long userId, long friendId) {
        if (userStorage.getUser(userId) == null) {
            log.error("Пользователь не найден: {}", userId);
            throw new EntityNotFoundException("Пользователь не найден");
        }

        if (userStorage.getUser(friendId) == null) {
            log.error("Друг не найден: {}", friendId);
            throw new EntityNotFoundException("Друг не найден");
        }

        try {
            userStorage.removeFriend(userId, friendId);
        } catch (Exception e) {
            log.error("Ошибка при удалении друга: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при удалении друга");
        }
    }

    public Collection<User> getFriends(long id) {
        try {
            return userStorage.getFriends(id);
        } catch (Exception e) {
            log.error("Ошибка при получении друзей: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при получении друзей");
        }
    }

    public Collection<User> getCommonFriends(long id, long otherId) {
        try {
            return userStorage.getCommonFriends(id, otherId);
        } catch (Exception e) {
            log.error("Ошибка при получении общих друзей: {}", e.getMessage());
            throw new EntityNotFoundException("Ошибка при получении общих друзей");
        }
    }

    public List<String> validateUser(User user) {
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

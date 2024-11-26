package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final List<User> users = new ArrayList<>();
    private int userIdCounter = 1;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        user.setId(userIdCounter++);
        users.add(user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                log.info("Обновлён пользователь: {}", user);
                return user;
            }
        }
        throw new ValidationException("Пользователь с ID " + user.getId() + " не найден.");
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрошены все пользователи");
        return users;
    }
}
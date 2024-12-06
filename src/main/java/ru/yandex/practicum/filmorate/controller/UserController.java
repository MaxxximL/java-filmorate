package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")

public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final List<User> users = new ArrayList<>();
    private int userIdCounter = 1;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid User user) {
        user.setId(userIdCounter++);
        users.add(user);
        log.info("Добавлен пользователь: {}", user);
        if (users.contains(user)) {
            return ResponseEntity.ok().body(user);
        }

        ErrorResponse errorResponseBody = ErrorResponse.builder().code(400).reasone("Имя не может быть пустым.").build();
        return new ResponseEntity<>(errorResponseBody, HttpStatusCode.valueOf(400));

    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {

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

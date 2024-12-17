package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final List<User> users = new ArrayList<>();
    private int userIdCounter = 1;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        List<String> validationErrors = validateUser(user);
        if (!validationErrors.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode(400);
            errorResponse.setReason(String.join(", ", validationErrors));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(userIdCounter++);
        users.add(user);
        log.info("Добавлен пользователь: {}", user);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping
    public ResponseEntity<Object> updateUser(@RequestBody User user) {
        List<String> validationErrors = validateUser(user);
        if (!validationErrors.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode(400);
            errorResponse.setReason(String.join(", ", validationErrors));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                log.info("Обновлён пользователь: {}", user);
                return ResponseEntity.ok().body(user);
            }
        }
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(404);
        errorResponse.setReason("Пользователь с ID " + user.getId() + " не найден.");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрошены все пользователи");
        return users;
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

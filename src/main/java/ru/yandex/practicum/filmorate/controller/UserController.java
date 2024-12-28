package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        List<String> validationErrors = userService.validateUser(user);
        if (!validationErrors.isEmpty()) {
            String errorMessage = "User validation failed: " + String.join(", ", validationErrors);
            return ResponseEntity.badRequest().body(new ErrorResponse(errorMessage));
        }
        return ResponseEntity.ok(userService.addUser(user));
    }


    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(user));
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("User not found: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        User user = userService.getUser(id);
        if (user == null) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable long id, @PathVariable long friendId) {
        User user = userService.getUser(id);
        User friend = userService.getUser(friendId);

        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }

        if (friend == null) {
            throw new EntityNotFoundException("Friend not found with ID: " + friendId);
        }

        userService.addFriend(id, friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable long id, @PathVariable long friendId) {
        User user = userService.getUser(id);
        User friend = userService.getUser(friendId);

        // Проверяем наличие пользователей перед удалением
        if (user == null || friend == null) {
            throw new EntityNotFoundException("One or both users not found: " + id + " or " + friendId);
        }

        userService.removeFriend(id, friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);

    }
}


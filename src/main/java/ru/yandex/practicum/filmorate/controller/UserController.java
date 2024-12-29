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
    public ResponseEntity<Object> updateUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(user));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found: " + e.getMessage())); // Вернуть 404 Not Found
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
    public ResponseEntity<ErrorResponse> addFriend(@PathVariable long id, @PathVariable long friendId) {
        try {
            userService.addFriend(id, friendId);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage())); // 404 NOT FOUND
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<ErrorResponse> removeFriend(@PathVariable long id, @PathVariable long friendId) {
        try {
            userService.removeFriend(id, friendId);
            return ResponseEntity.noContent().build(); // 204 NO CONTENT
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage())); // 404 NOT FOUND
        }
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
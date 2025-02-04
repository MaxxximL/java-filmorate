package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ErrorResponse;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.addUser(user));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<Object> updateUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(user));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        try {
            return ResponseEntity.ok(userService.getUser(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found with id: " + id));
        }
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Object> addFriend(@PathVariable long id, @PathVariable long friendId) {
        try {
            userService.addFriend(id, friendId);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User or friend not found: " + e.getMessage()));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage())); // 400 BAD REQUEST
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Object> removeFriend(@PathVariable long id, @PathVariable long friendId) {
        try {
            userService.removeFriend(id, friendId);
            return ResponseEntity.noContent().build(); // 204 NO CONTENT
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage())); // 404 NOT FOUND
        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage())); // 400 BAD REQUEST
        }
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Object> getFriends(@PathVariable long id) {
        try {
            Collection<User> friends = userService.getFriends(id);
            return ResponseEntity.ok(friends);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found with id: " + id));
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
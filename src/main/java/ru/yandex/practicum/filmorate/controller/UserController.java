package ru.yandex.practicum.filmorate.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<CreateUserDto> createUser(@RequestBody CreateUserDto userDto) {
        return ResponseEntity.ok(userService.addUser(userDto));
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody CreateUserDto userDto) {
        return userService.updateUser(userDto.getId(), userDto);

    }

    @GetMapping("/{id}")
    public ResponseEntity<CreateUserDto> getUser(@PathVariable long id) {
        User user = userService.getUser(id);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @GetMapping
    public List<CreateUserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable long id, @PathVariable long friendId) {
        try {
            userService.addFriend(id, friendId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // Return 404 for non-existent users
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build(); // Return 400 for validation errors
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFriend(id, friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<CreateUserDto>> getFriends(@PathVariable long id) {
        return ResponseEntity.ok(userService.getFriends(id).stream()
                .map(UserMapper::toDto)
                .toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<CreateUserDto>> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return ResponseEntity.ok(userService.getCommonFriends(id, otherId).stream()
                .map(UserMapper::toDto)
                .toList());
    }
}
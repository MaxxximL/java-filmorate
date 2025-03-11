package ru.yandex.practicum.filmorate.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private int userIdCounter = 1;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {

        this.userStorage = userStorage;
    }

    public CreateUserDto addUser(CreateUserDto userDto) {
        User newUser = UserMapper.toModel(userDto);
        validateUser(newUser);
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        // Проверка на уникальность email
        if (userStorage.getAllUsers().stream().anyMatch(u -> u.getEmail().equals(newUser.getEmail()))) {
            throw new ValidationException("Пользователь с таким email уже существует.");
        }
        User createdUser = userStorage.save(newUser); // используем save, а не addUser
        return UserMapper.toDto(createdUser);
    }

    public User updateUser(long id, CreateUserDto userDto) {
        User existingUser = userStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found."));

        User updatedUser = UserMapper.toModel(userDto);
        updatedUser.setId(id); //ID берем из path

        validateUser(updatedUser);
        // Проверка на уникальность email при обновлении
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) && userStorage.getAllUsers().stream().anyMatch(u -> u.getEmail().equals(updatedUser.getEmail()))) {
            throw new ValidationException("Пользователь с таким email уже существует.");
        }
        return userStorage.save(updatedUser);
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


    public List<String> addFriend(long userId, long friendId) {
        if (userStorage.getUser(userId) == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        if (userStorage.getUser(friendId) == null) {
            throw new EntityNotFoundException("Friend not found with ID: " + friendId);
        }

        // Проверяем, не является ли друг уже другом
        Set<Long> currentFriends = userStorage.getFriendsIds(userId);
        if (currentFriends.contains(friendId)) {
            throw new ValidationException("Пользователь уже в списке друзей: " + friendId);
        }

        userStorage.addFriend(userId, friendId);
        return null;
    }

    public void removeFriend(long userId, long friendId) {
        // Проверяем существование обоих пользователей
        if (userStorage.getUser(userId) == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        if (userStorage.getUser(friendId) == null) {
            throw new EntityNotFoundException("Friend not found with ID: " + friendId);
        }

        // Удаление друга
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

    private void validateUser(User user) {  // Изменено: теперь метод возвращает void
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Некорректный email.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым.");
        }
        if (user.getName() != null && user.getName().isBlank()) {  // Валидация имени
            user.setName(user.getLogin()); // Если имя пустое, используем логин
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) { // Изменено: проверка на null
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

}
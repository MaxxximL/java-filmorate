package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("testuser@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user = userService.addUser(user);
    }

    @Test
    public void createUserTest() {
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setLogin("newuser");
        newUser.setName("New User");
        newUser.setBirthday(LocalDate.of(1995, 12, 25));

        ResponseEntity<Object> response = userController.createUser(newUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateUserTest() {
        // Обновление пользователя
        user.setName("Updated Name");
        ResponseEntity<Object> response = userController.updateUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        User updatedUser = (User) response.getBody();
        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    public void updateUserNotFoundTest() {
        User nonExistentUser = new User();
        nonExistentUser.setId(999);
        nonExistentUser.setEmail("nonexistent@example.com");
        nonExistentUser.setLogin("nonexistent");
        nonExistentUser.setName("Non-existent User");
        nonExistentUser.setBirthday(LocalDate.of(2000, 1, 1));

        ResponseEntity<Object> response = userController.updateUser(nonExistentUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getUserTest() {
        // Ищем существующего пользователя
        User retrievedUser = (User) userController.getUser(user.getId()).getBody();
        assertEquals(user.getId(), retrievedUser.getId());
        assertEquals(user.getEmail(), retrievedUser.getEmail());
        assertEquals(user.getLogin(), retrievedUser.getLogin());
        assertEquals(user.getName(), retrievedUser.getName());
        assertEquals(user.getBirthday(), retrievedUser.getBirthday());
    }

    @Test
    public void getUserNotFoundTest() {
        // Запрос к несуществующему ID
        ResponseEntity<Object> response = userController.getUser(999);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addFriendTest() {

        User friend = new User();
        friend.setEmail("friend@example.com");
        friend.setLogin("friend");
        friend.setName("Friend User");
        friend.setBirthday(LocalDate.of(1985, 5, 5));
        friend = userService.addUser(friend);

        // Добавляем в друзья
        ResponseEntity<Object> response = userController.addFriend(user.getId(), friend.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void removeFriendTest() {

        User friend = new User();
        friend.setEmail("friend@example.com");
        friend.setLogin("friend");
        friend.setName("Friend User");
        friend.setBirthday(LocalDate.of(1985, 5, 5));
        friend = userService.addUser(friend);

        userController.addFriend(user.getId(), friend.getId());

        ResponseEntity<Object> response = userController.removeFriend(user.getId(), friend.getId());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void getFriendsTest() {

        User friend = new User();
        friend.setEmail("friend@example.com");
        friend.setLogin("friend");
        friend.setName("Friend User");
        friend.setBirthday(LocalDate.of(1985, 5, 5));
        friend = userService.addUser(friend);

        userController.addFriend(user.getId(), friend.getId());

        Collection<User> friends = (Collection<User>) userController.getFriends(user.getId()).getBody();

        assertEquals(1, friends.size());
        assertEquals(friend.getEmail(), friends.iterator().next().getEmail());
    }

    @Test
    public void getCommonFriendsTest() {
        // Создаем пользователей
        User friend1 = new User();
        friend1.setEmail("friend1@example.com");
        friend1.setLogin("friend1");
        friend1.setName("Friend User 1");
        friend1.setBirthday(LocalDate.of(1985, 5, 5));
        friend1 = userService.addUser(friend1);

        User friend2 = new User();
        friend2.setEmail("friend2@example.com");
        friend2.setLogin("friend2");
        friend2.setName("Friend User 2");
        friend2.setBirthday(LocalDate.of(1985, 5, 5));
        friend2 = userService.addUser(friend2);

        userController.addFriend(user.getId(), friend1.getId());
        userController.addFriend(user.getId(), friend2.getId());
        userController.addFriend(friend1.getId(), friend2.getId());

        Collection<User> commonFriends = (Collection<User>) userController.getCommonFriends(user.getId(), friend1.getId());

        assertEquals(1, commonFriends.size());
        assertEquals(friend2.getEmail(), commonFriends.iterator().next().getEmail());
    }

}
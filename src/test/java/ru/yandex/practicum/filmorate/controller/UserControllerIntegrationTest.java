package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.CreateUserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerIntegrationTest {

    @Autowired
    private UserController userController;

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        List<CreateUserDto> users = userController.getAllUsers();
        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @Test
    void getUser_shouldReturnUser_whenUserExists() {
        CreateUserDto user = userController.getUser(1L).getBody();
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("user1@example.com", user.getEmail());
    }

    @Test
    void getUser_shouldThrowException_whenUserNotExists() {
        assertThrows(EmptyResultDataAccessException.class, () -> userController.getUser(999L));
    }

    @Test
    void createUser_shouldAddNewUser() {
        CreateUserDto newUser = CreateUserDto.builder()
                .email("new@example.com")
                .login("newlogin")
                .name("New User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        CreateUserDto createdUser = userController.createUser(newUser).getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("new@example.com", createdUser.getEmail());

        List<CreateUserDto> users = userController.getAllUsers();
        assertEquals(4, users.size());
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        CreateUserDto user = userController.getUser(1L).getBody();
        assertNotNull(user);
        user.setName("Updated Name");

        User updatedUser = userController.updateUser(user);
        assertNotNull(updatedUser);
        assertEquals(1L, updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());

        CreateUserDto fetchedUser = userController.getUser(1L).getBody();
        assertEquals("Updated Name", fetchedUser.getName());
    }

    @Test
    void addFriend_shouldAddFriend() {
        userController.addFriend(1L, 3L);
        List<CreateUserDto> friends = userController.getFriends(1L).getBody();
        assertNotNull(friends);
        assertEquals(2, friends.size());
        assertEquals(2L, friends.get(0).getId());
    }

    @Test
    void getCommonFriends_shouldReturnCommonFriends() {
        List<CreateUserDto> commonFriends = userController.getCommonFriends(1L, 2L).getBody();
        assertNotNull(commonFriends);
        assertEquals(1, commonFriends.size());
        assertEquals(3L, commonFriends.get(0).getId());
    }
}
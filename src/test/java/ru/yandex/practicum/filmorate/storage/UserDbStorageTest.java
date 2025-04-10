package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class}) // Add UserRowMapper here
class UserDbStorageTest {

    @Autowired
    private UserStorage userStorage;

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        Collection<User> users = userStorage.getAllUsers();
        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @Test
    void getUser_shouldReturnUser_whenUserExists() {
        User user = userStorage.getUser(1L);
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("user1@example.com", user.getEmail());
    }


    @Test
    void addUser_shouldAddNewUser() {
        User newUser = User.builder()
                .email("new@example.com")
                .login("newlogin")
                .name("New User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userStorage.addUser(newUser);
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("new@example.com", createdUser.getEmail());

        Collection<User> users = userStorage.getAllUsers();
        assertEquals(4, users.size());
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        User user = userStorage.getUser(1L);
        assertNotNull(user);
        user.setName("Updated Name");

        User updatedUser = userStorage.updateUser(user);
        assertNotNull(updatedUser);
        assertEquals(1L, updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());

        User fetchedUser = userStorage.getUser(1L);
        assertEquals("Updated Name", fetchedUser.getName());
    }

    @Test
    void addFriend_shouldNotAddDuplicateFriend() {
        // User 1 already has friend 2 (from test data)
        userStorage.addFriend(1L, 2L); // This should not throw exception

        // Verify the friendship still exists
        Set<Long> friends = userStorage.getFriendsIds(1L);
        assertTrue(friends.contains(2L));
    }

    @Test
    void getCommonFriends_shouldReturnCommonFriends() {
        Collection<User> commonFriends = userStorage.getCommonFriends(1L, 2L);
        assertNotNull(commonFriends);
        assertEquals(1, commonFriends.size());
        assertEquals(3L, commonFriends.iterator().next().getId());
    }
}
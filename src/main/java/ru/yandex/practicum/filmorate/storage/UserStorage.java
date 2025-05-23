package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    User getUser(long id);

    Collection<User> getAllUsers();

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    Collection<User> getFriends(long userId);

    Collection<User> getCommonFriends(long userId, long otherId);

    Set<Long> getFriendsIds(long userId);

    Optional<User> findById(long id);

    User save(User user);
}
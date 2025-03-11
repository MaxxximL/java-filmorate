package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> userFriends = new HashMap<>();
    private long idCounter = 1;

    @Override
    public User addUser(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public void addFriend(long userId, long friendId) {
        userFriends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        userFriends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        Set<Long> friendsOfUser = userFriends.get(userId);
        Set<Long> friendsOfFriend = userFriends.get(friendId);
        if (friendsOfUser != null) {
            friendsOfUser.remove(friendId);
        }
        if (friendsOfFriend != null) {
            friendsOfFriend.remove(userId);
        }
    }

    @Override
    public Collection<User> getFriends(long userId) {
        Set<Long> friends = userFriends.get(userId);
        if (friends == null) {
            return Collections.emptyList();
        }
        return friends.stream().map(users::get).toList();
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        Set<Long> friendsOfUser = userFriends.get(userId);
        Set<Long> friendsOfOther = userFriends.get(otherId);
        if (friendsOfUser == null || friendsOfOther == null) {
            return Collections.emptyList();
        }
        friendsOfUser.retainAll(friendsOfOther);
        return friendsOfUser.stream().map(users::get).toList();
    }

    @Override
    public Set<Long> getFriendsIds(long userId) {
        return userFriends.getOrDefault(userId, Collections.emptySet());

    }

        @Override
        public Optional<User> findById(long id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public User save(User user) {
            users.put(user.getId(), user);
            return user;
        }
    }
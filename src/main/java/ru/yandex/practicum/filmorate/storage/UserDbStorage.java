package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Primary
@RequiredArgsConstructor
@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Long userId = keyHolder.getKey().longValue();
        user.setId(userId);

        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User getUser(long id) {
        String sql = "SELECT * FROM users WHERE id=?";
        return jdbcTemplate.queryForObject(sql, userRowMapper, id);

    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);

    }

    @Override
    public void addFriend(long userId, long friendId) {
        // Check if friendship already exists
        String checkSql = "SELECT COUNT(*) FROM user_friends WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, friendId);

        if (count == null || count == 0) {
            String sql = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, userId, friendId);
        }
        // If friendship already exists, do nothing
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sql = "DELETE FROM user_friends WHERE user_id=? AND friend_id=?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        String sql = "SELECT u.* FROM users u JOIN user_friends uf ON u.id = uf.friend_id WHERE uf.user_id=?";
        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        String sql = "SELECT u.* FROM users u JOIN user_friends uf1 ON u.id = uf1.friend_id " +
                "JOIN user_friends uf2 ON u.id = uf2.friend_id WHERE uf1.user_id=? AND uf2.user_id=?";
        return jdbcTemplate.query(sql, userRowMapper, userId, otherId);
    }

    @Override
    public Set<Long> getFriendsIds(long userId) {
        String sql = "SELECT friend_id FROM user_friends WHERE user_id=?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, userId));
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User save(User user) {
        if (findById(user.getId()).isPresent()) {
            updateUser(user);
            return user;
        } else {
            addUser(user);
            return user;
        }
    }
}

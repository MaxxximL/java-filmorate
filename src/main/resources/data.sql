-- Вставка начальных данных в таблицу mpa
INSERT INTO mpa (name) VALUES
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17');

-- Вставка начальных данных в таблицу genres
INSERT INTO genres (name) VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик');


-- Вставка пользователей
INSERT INTO users (email, login, name, birthday) VALUES
('user1@example.com', 'user1', 'User One', '1990-01-01'),
('user2@example.com', 'user2', 'User Two', '1992-05-12'),
('user3@example.com', 'user3', 'User Three', '1995-07-25');

-- Вставка фильмов
INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES
('Film One', 'Description for Film One', '2020-01-01', 120, 1),
('Film Two', 'Description for Film Two', '2022-02-02', 90, 2),
('Film Three', 'Description for Film Three', '2021-03-15', 140, 3),
('Film Four', 'Description for Film Four', '2019-06-30', 150, 4),
('Film Five', 'Description for Film Five', '2014-05-30', 160, 5);

-- Вставка лайков
INSERT INTO film_likes (film_id, user_id) VALUES
(1, 1),
(1, 2),
(2, 1),
(2, 3),
(3, 3);

-- Вставка друзей
INSERT INTO user_friends (user_id, friend_id) VALUES
(1, 2),
(1, 3),
(2, 3);
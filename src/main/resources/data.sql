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

-- Вставка фильмов с указанием режиссёров
INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES
('Крадущийся тигр, затаившийся дракон', 'История о любви и битве.', '2000-02-05', 120, 1),
('Крадущийся в ночи', 'Триллер с элементами драмы.', '2021-03-15', 140, 2),
('Три метра над уровнем неба', 'История о любви и мечте.', '2010-12-22', 118, 3),
('Форрест Гамп', 'История жизни простого человека.', '1994-07-06', 142, 4),
('Мстители: Война бесконечности', 'Супергерои объединяются.', '2018-04-27', 149, 5);

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

-- Вставка режиссеров
INSERT INTO directors (name) VALUES
('Christopher Nolan'),
('Quentin Tarantino'),
('Steven Spielberg');

-- Assign directors to films
INSERT INTO film_directors (film_id, director_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 1),
(5, 2);


INSERT INTO film_genres (film_id, genre_id) VALUES
(1, 1),  -- Для первого фильма добавляем жанр с ID 1
(1, 2),  -- Добавляем также жанр с ID 2
(2, 1),  -- Для второго фильма добавляем жанр с ID 1
(3, 3);  -- И так далее…
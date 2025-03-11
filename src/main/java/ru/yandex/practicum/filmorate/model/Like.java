package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Like {
    private int likeId; // Изменено на camelCase
    private int userId;
    private int filmId;
}
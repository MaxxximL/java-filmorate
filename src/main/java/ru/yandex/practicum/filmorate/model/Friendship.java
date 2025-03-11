package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private int friendshipId; // изменено с friendship_id на friendshipId
    private int userId;
}
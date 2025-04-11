package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Genre implements Serializable {
    private long id;
    private String name;

}
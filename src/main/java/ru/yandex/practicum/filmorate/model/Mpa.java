package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Mpa implements Serializable {

    private Long id;
    private String name;

}
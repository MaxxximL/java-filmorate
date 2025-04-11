package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

public class MpaMapper {

    public static MpaDto toDto(Mpa mpa) {
        return MpaDto.builder()
                .id(mpa.getId())
                .name(mpa.getName())
                .build();
    }

    public static Mpa toModel(MpaDto mpaDto) {
        return Mpa.builder()
                .id(mpaDto.getId())
                .name(mpaDto.getName())
                .build();
    }
}
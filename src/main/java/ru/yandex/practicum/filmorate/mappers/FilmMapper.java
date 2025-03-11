package ru.yandex.practicum.filmorate.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmMapper {

    private final GenreStorage genreStorage; // изменено с static на non-static

    private final MpaStorage mpaStorage;

    public static CreateFilmDto toDto(Film film) {
        List<Long> genreIds = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());

        List<GenreDto> genreDtos = Optional.of(film.getGenres())
                .orElseGet(Collections::emptyList) // Если жанры равны null, возвращаем пустой список
                .stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toList());

        return CreateFilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(MpaMapper.toDto(film.getMpa()))
                .genreIds(genreIds)
                .genres(genreDtos)
                .build();
    }

    public static Film toModel(CreateFilmDto filmDto) {
        List<Genre> genres = Optional.ofNullable(filmDto.getGenres())
                .orElseGet(Collections::emptyList)  // возвращаем пустой список, если genres == null
                .stream()
                .map(GenreMapper::toModel)
                .collect(Collectors.toList());

        return Film.builder()
                .id(filmDto.getId())
                .name(filmDto.getName())
                .description(filmDto.getDescription())
                .releaseDate(filmDto.getReleaseDate())
                .duration(filmDto.getDuration())
                .mpa(MpaMapper.toModel(filmDto.getMpa()))
                .genres(genres)
                .build();
    }

}
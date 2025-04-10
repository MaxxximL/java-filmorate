package ru.yandex.practicum.filmorate.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Director;
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
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public static CreateFilmDto toDto(Film film) {
        List<GenreDto> genreDtos = film.getGenres().stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toList());

        List<DirectorDto> directorDtos = film.getDirectors().stream()
                .map(DirectorMapper::toDto)
                .collect(Collectors.toList());

        return CreateFilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(MpaMapper.toDto(film.getMpa()))
                .genres(genreDtos)
                .genreIds(genreDtos.stream().map(GenreDto::getId).collect(Collectors.toList()))
                .directors(directorDtos)
                .build();
    }

    public static Film toModel(CreateFilmDto filmDto) {
        List<Genre> genres = Optional.ofNullable(filmDto.getGenres())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(GenreMapper::toModel)
                .collect(Collectors.toList());

        List<Director> directors = Optional.ofNullable(filmDto.getDirectors())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(DirectorMapper::toModel)
                .collect(Collectors.toList());

        return Film.builder()
                .id(filmDto.getId())
                .name(filmDto.getName())
                .description(filmDto.getDescription())
                .releaseDate(filmDto.getReleaseDate())
                .duration(filmDto.getDuration())
                .mpa(MpaMapper.toModel(filmDto.getMpa()))
                .genres(genres)
                .directors(directors)
                .build();
    }
}
package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public ResponseEntity<List<DirectorDto>> getAllDirectors() {
        List<DirectorDto> directors = directorService.getAllDirectors().stream()
                .map(DirectorMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(directors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDirectorById(@PathVariable long id) {
        try {
            Director director = directorService.getDirectorById(id);
            return ResponseEntity.ok(DirectorMapper.toDto(director));
        } catch (DirectorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<DirectorDto> createDirector(@Valid @RequestBody DirectorDto directorDto) {
        Director director = directorService.createDirector(DirectorMapper.toModel(directorDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(DirectorMapper.toDto(director));
    }

    @PutMapping
    public ResponseEntity<?> updateDirector(@Valid @RequestBody DirectorDto directorDto) {
        try {
            Director director = directorService.updateDirector(DirectorMapper.toModel(directorDto));
            return ResponseEntity.ok(DirectorMapper.toDto(director));
        } catch (DirectorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDirector(@PathVariable long id) {
        try {
            directorService.deleteDirector(id);
            return ResponseEntity.noContent().build();
        } catch (DirectorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(long id) {
        return directorStorage.getDirectorById(id)
                .orElseThrow(() -> new DirectorNotFoundException("Director with id " + id + " not found"));
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        if (!directorStorage.getDirectorById(director.getId()).isPresent()) {
            throw new DirectorNotFoundException("Director with id " + director.getId() + " not found");
        }
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(long id) {
        if (!directorStorage.getDirectorById(id).isPresent()) {
            throw new DirectorNotFoundException("Director with id " + id + " not found");
        }
        directorStorage.deleteDirector(id);
    }
}
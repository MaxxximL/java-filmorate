package ru.yandex.practicum.filmorate.exceptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.dao.DuplicateKeyException; // Импорт DuplicateKeyException
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        return ResponseEntity.badRequest() // 400
                .body(ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404
                .body(ErrorResponse.create(e, HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(DuplicateKeyException.class) // Обработка DuplicateKeyException
    public ResponseEntity<ErrorResponse> handleDuplicateKeyException(DuplicateKeyException e) {
        return ResponseEntity.badRequest() // 400
                .body(ErrorResponse.create(e, HttpStatus.BAD_REQUEST, "Пользователь с таким email уже существует."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(ErrorResponse.create(e, HttpStatus.INTERNAL_SERVER_ERROR, "Произошла внутренняя ошибка сервера: " + e.getMessage()));
    }
}
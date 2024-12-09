package ru.yandex.practicum.filmorate.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    public int code;
    public String reason;

    public ErrorResponse() {

    }

    public ErrorResponse(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }
}

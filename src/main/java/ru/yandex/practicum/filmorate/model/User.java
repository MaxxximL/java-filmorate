package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"email"})
@ToString
public class User {
    private int id;

    @Email(message = "Электронная почта должна содержать символ '@'.")
    private String email;

    @NotBlank(message = "Логин не может быть пустым и содержать пробелы.")
    @Size(min = 1, message = "Логин не может быть пустым.")
    private String login;

    private String name;

    @Past(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
}
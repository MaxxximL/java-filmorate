package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private String testUser;
    private String mail;
    private String testName;
    private LocalDate localDate;

    public User(String testUser, String mail, String testName, LocalDate of) {
        this.testUser = testUser;
        this.mail = mail;
        this.testName = testName;
        localDate = of;
    }

    // Методы getId и setId
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
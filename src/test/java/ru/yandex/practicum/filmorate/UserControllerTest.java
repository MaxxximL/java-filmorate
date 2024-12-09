package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserController userController;

    @BeforeEach
    void setUp() {
        User validUser = new User();
        validUser.setEmail("valid@mail.com");
        validUser.setLogin("validLogin");
        validUser.setName("Valid User");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldCreateUserWhenValid() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"valid@mail.com\", \"login\":\"validLogin\", \"name\":\"Valid User\", \"birthday\": \"2000-01-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is("validLogin")));
    }

    @Test
    public void whenUserEmailIsInvalid_thenReturns400() throws Exception {
        User user = new User();
        user.setEmail("invalidEmail");


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenUserLoginIsBlank_thenReturns400() throws Exception {
        User user = new User();
        user.setLogin(" ");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testCreateUserWithEmptyName() throws Exception {
        User user = new User();
        user.setEmail("example@mail.com");
        user.setLogin("validLogin");
        user.setName(""); // Пустое имя
        user.setBirthday(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }
}


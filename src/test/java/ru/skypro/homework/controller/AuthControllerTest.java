package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.config.UserDetailsServiceImpl;
import ru.skypro.homework.config.WebSecurityConfig;
import ru.skypro.homework.dto.auth.Login;
import ru.skypro.homework.dto.auth.Register;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(WebSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginSuccess() throws Exception {
        when(authService.login("user@mail.com", "Password1")).thenReturn(true);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Login("user@mail.com", "Password1"))))
                .andExpect(status().isOk());
    }

    @Test
    void loginBadCredentials() throws Exception {
        when(authService.login("user@mail.com", "WrongPass1")).thenReturn(false);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Login("user@mail.com", "WrongPass1"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginInvalidEmail() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"not-email\",\"password\":\"Password1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerSuccess() throws Exception {
        when(authService.register(any(Register.class))).thenReturn(true);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new Register("new@mail.com", "Password1", "Иван", "Иванов", "+7 (999) 123-45-67", "USER"))))
                .andExpect(status().isCreated());
    }

    @Test
    void registerDuplicate() throws Exception {
        when(authService.register(any(Register.class))).thenReturn(false);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new Register("exist@mail.com", "Password1", "Иван", "Иванов", "+7 (999) 123-45-67", "USER"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerInvalidData() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\",\"firstName\":\"\",\"lastName\":\"\",\"phone\":\"\",\"role\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}

package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.auth.Login;
import ru.skypro.homework.dto.auth.Register;
import ru.skypro.homework.service.AuthService;

/**
 * Контроллер аутентификации и регистрации пользователей.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Авторизация", description = "API для входа и регистрации")
public class AuthController {

    private final AuthService authService;

    /**
     * Выполняет вход пользователя (аутентификацию).
     * Проверяет логин и пароль через сервис.
     *
     * @param login данные для входа
     * @return 200 OK при успехе, 401 Unauthorized при ошибке
     */
    @PostMapping("/login")
    @Operation(summary = "Авторизация пользователя", description = "Аутентификация пользователя по логину и паролю")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная авторизация"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    public ResponseEntity<Void> login(@Valid @RequestBody Login login) {
        if (authService.login(login.username(), login.password())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Регистрирует нового пользователя в системе.
     * Данные проходят валидацию перед передачей в сервис.
     *
     * @param register данные для регистрации
     * @return 201 Created при успехе, 400 Bad Request при ошибке
     */
    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", description = "Создает нового пользователя в базе данных")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или дубликат")
    })
    public ResponseEntity<Void> register(@Valid @RequestBody Register register) {
        if (authService.register(register)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
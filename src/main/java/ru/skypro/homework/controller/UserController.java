package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.user.NewPasswordDto;
import ru.skypro.homework.dto.user.UpdateUserDto;
import ru.skypro.homework.dto.user.UserDto;
import ru.skypro.homework.service.UserService;

/**
 * Контроллер управления профилем пользователя.
 * Реализует получение, обновление данных и смену пароля.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Управление данными авторизованного пользователя")
public class UserController {

    private final UserService userService;

    /**
     * Получает данные текущего авторизованного пользователя.
     *
     * @param userDetails данные авторизованного пользователя из контекста безопасности
     * @return ResponseEntity с объектом UserDto
     */
    @GetMapping("/me")
    @Operation(summary = "Получить профиль пользователя", description = "Возвращает данные авторизованного пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные пользователя успешно получены"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public ResponseEntity<UserDto> getUser(@Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getCurrentUser(userDetails));
    }

    /**
     * Частично обновляет данные профиля пользователя.
     *
     * @param updateUserDto DTO с новыми данными пользователя
     * @param userDetails   данные авторизованного пользователя
     * @return ResponseEntity с обновленным объектом UserDto
     */
    @PatchMapping("/me")
    @Operation(summary = "Обновить данные профиля", description = "Частичное обновление информации пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<UserDto> updateUser(
            @Valid @RequestBody UpdateUserDto updateUserDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserDto updatedUser = userService.updateUser(userDetails, updateUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Сменяет пароль текущего авторизованного пользователя.
     *
     * @param newPasswordDto DTO с текущим и новым паролем
     * @param userDetails    данные авторизованного пользователя
     * @return ResponseEntity со статусом 200 при успехе
     */
    @PostMapping("/set_password")
    @Operation(summary = "Сменить пароль", description = "Обновление пароля пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пароль успешно изменен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации формата пароля"),
            @ApiResponse(responseCode = "401", description = "Неверный текущий пароль или пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Запрещено изменение пароля")
    })
    public ResponseEntity<Void> setPassword(
            @Valid @RequestBody NewPasswordDto newPasswordDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.changePassword(userDetails, newPasswordDto);
        return ResponseEntity.ok().build();
    }

}
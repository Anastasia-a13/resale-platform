package ru.skypro.homework.exception;

public class BadRequestException extends RuntimeException {
    public static final String WRONG_PASSWORD = "Неверный текущий пароль";

    public BadRequestException(String message) {
        super(message);
    }
}

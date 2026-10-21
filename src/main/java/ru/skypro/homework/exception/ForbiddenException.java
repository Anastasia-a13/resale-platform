package ru.skypro.homework.exception;

import org.springframework.security.access.AccessDeniedException;

public class ForbiddenException extends AccessDeniedException {
    public static final String MESSAGE = "Нет прав на выполнение операции";

    public ForbiddenException(String message) {
        super(message);
    }
}

package ru.skypro.homework.exception;

public class ResourceNotFoundException extends RuntimeException {
    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String AD_NOT_FOUND = "Объявление не найдено";
    public static final String COMMENT_NOT_FOUND = "Комментарий не найден";

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

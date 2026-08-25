package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;

/**
 * Сервис проверки прав доступа.
 * Предоставляет методы проверки владельца объявления и комментария,
 * а также принадлежности комментария к указанному объявлению.
 */
@Slf4j
@Service
public class ValidatorService {

    public void checkAdOwnership(Ad ad, UserDetails userDetails) {
        if (isAdmin(userDetails)) {
            return;
        }
        if (!ad.getAuthor().getEmail().equals(userDetails.getUsername())) {
            log.warn("Доступ запрещён: adId={}, пользователь={}", ad.getId(), userDetails.getUsername());
            throw new ForbiddenException(ForbiddenException.MESSAGE);
        }
    }

    public void checkCommentAdMatch(Comment comment, Integer adId) {
        if (!comment.getAd().getId().equals(adId)) {
            throw new ResourceNotFoundException(ResourceNotFoundException.COMMENT_NOT_FOUND);
        }
    }

    public void checkCommentOwnership(Comment comment, UserDetails userDetails) {
        if (isAdmin(userDetails)) {
            return;
        }
        if (!comment.getAuthor().getEmail().equals(userDetails.getUsername())) {
            log.warn("Доступ запрещён: commentId={}, пользователь={}", comment.getId(), userDetails.getUsername());
            throw new ForbiddenException(ForbiddenException.MESSAGE);
        }
    }

    private boolean isAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }
}

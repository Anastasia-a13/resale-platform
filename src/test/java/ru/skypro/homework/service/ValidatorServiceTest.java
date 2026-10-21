package ru.skypro.homework.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;

import static org.springframework.security.core.userdetails.User.withUsername;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidatorServiceTest {

    @InjectMocks
    private ValidatorService validator;

    private UserDetails createUser(String email, String role) {
        return withUsername(email).password("password").authorities(role).build();
    }

    private Ad createAd(String authorEmail) {
        ru.skypro.homework.model.user.User author = new ru.skypro.homework.model.user.User();
        author.setEmail(authorEmail);
        Ad ad = new Ad();
        ad.setId(1);
        ad.setAuthor(author);
        return ad;
    }

    private Comment createComment(String authorEmail, Integer adId) {
        ru.skypro.homework.model.user.User author = new ru.skypro.homework.model.user.User();
        author.setEmail(authorEmail);
        Ad ad = new Ad();
        ad.setId(adId);
        Comment comment = new Comment();
        comment.setId(1);
        comment.setAuthor(author);
        comment.setAd(ad);
        return comment;
    }

    @Test
    void checkAdOwnership_AdminPasses() {
        Ad ad = createAd("owner@mail.com");
        UserDetails admin = createUser("admin@mail.com", "ROLE_ADMIN");
        assertDoesNotThrow(() -> validator.checkAdOwnership(ad, admin));
    }

    @Test
    void checkAdOwnership_OwnerPasses() {
        Ad ad = createAd("owner@mail.com");
        UserDetails owner = createUser("owner@mail.com", "ROLE_USER");
        assertDoesNotThrow(() -> validator.checkAdOwnership(ad, owner));
    }

    @Test
    void checkAdOwnership_OtherThrows() {
        Ad ad = createAd("owner@mail.com");
        UserDetails other = createUser("other@mail.com", "ROLE_USER");
        assertThrows(ForbiddenException.class, () -> validator.checkAdOwnership(ad, other));
    }

    @Test
    void checkCommentOwnership_AdminPasses() {
        Comment comment = createComment("owner@mail.com", 1);
        UserDetails admin = createUser("admin@mail.com", "ROLE_ADMIN");
        assertDoesNotThrow(() -> validator.checkCommentOwnership(comment, admin));
    }

    @Test
    void checkCommentOwnership_OwnerPasses() {
        Comment comment = createComment("owner@mail.com", 1);
        UserDetails owner = createUser("owner@mail.com", "ROLE_USER");
        assertDoesNotThrow(() -> validator.checkCommentOwnership(comment, owner));
    }

    @Test
    void checkCommentOwnership_OtherThrows() {
        Comment comment = createComment("owner@mail.com", 1);
        UserDetails other = createUser("other@mail.com", "ROLE_USER");
        assertThrows(ForbiddenException.class, () -> validator.checkCommentOwnership(comment, other));
    }

    @Test
    void checkCommentAdMatch_Success() {
        Comment comment = createComment("user@mail.com", 5);
        assertDoesNotThrow(() -> validator.checkCommentAdMatch(comment, 5));
    }

    @Test
    void checkCommentAdMatch_MismatchThrows() {
        Comment comment = createComment("user@mail.com", 5);
        assertThrows(ResourceNotFoundException.class, () -> validator.checkCommentAdMatch(comment, 99));
    }
}

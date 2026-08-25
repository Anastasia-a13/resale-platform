package ru.skypro.homework.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.dto.comment.CommentDto;
import ru.skypro.homework.dto.comment.CommentsDto;
import ru.skypro.homework.dto.comment.CreateOrUpdateCommentDto;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.core.userdetails.User.withUsername;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValidatorService validator;

    @InjectMocks
    private CommentService commentService;

    private UserDetails createUserDetails(String email) {
        return withUsername(email)
                .password("pass")
                .authorities("ROLE_USER")
                .build();
    }

    private ru.skypro.homework.model.user.User createUser(String email) {
        ru.skypro.homework.model.user.User user = new ru.skypro.homework.model.user.User();
        user.setId(1);
        user.setEmail(email);
        return user;
    }

    private Ad createAd(Integer id) {
        Ad ad = new Ad();
        ad.setId(id);
        return ad;
    }

    private Comment createComment(Integer id, Integer adId, String authorEmail) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText("Test comment");
        comment.setAd(createAd(adId));
        comment.setAuthor(createUser(authorEmail));
        return comment;
    }

    @Test
    void getCommentsByAdIdSuccess() {
        Ad ad = createAd(1);
        Comment comment = createComment(1, 1, "user@mail.com");
        CommentDto dto = new CommentDto(1, 1, "Иван", "img.jpg", "Test comment", 1000L);

        when(adRepository.existsById(1)).thenReturn(true);
        when(commentRepository.findByAdId(1)).thenReturn(List.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(dto);

        CommentsDto result = commentService.getCommentsByAdId(1);

        assertEquals(1, result.count());
        assertEquals("Test comment", result.results().get(0).text());
    }

    @Test
    void getCommentsByAdIdNotFound() {
        when(adRepository.existsById(99)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentsByAdId(99));
    }

    @Test
    void createComment() {
        UserDetails user = createUserDetails("author@mail.com");
        Ad ad = createAd(1);
        ru.skypro.homework.model.user.User author = createUser("author@mail.com");
        CreateOrUpdateCommentDto dto = new CreateOrUpdateCommentDto("New comment text");
        Comment comment = createComment(null, 1, "author@mail.com");
        Comment saved = createComment(1, 1, "author@mail.com");
        CommentDto commentDto = new CommentDto(1, 1, "Иван", null, "New comment text", 2000L);

        when(adRepository.findById(1)).thenReturn(Optional.of(ad));
        when(userRepository.findByEmail("author@mail.com")).thenReturn(Optional.of(author));
        when(commentMapper.toEntity(dto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);
        when(commentMapper.toDto(saved)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(1, dto, user);

        assertNotNull(result);
        assertEquals("New comment text", result.text());
    }

    @Test
    void deleteCommentSuccess() {
        UserDetails owner = createUserDetails("owner@mail.com");
        Comment comment = createComment(1, 5, "owner@mail.com");

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));

        commentService.deleteComment(5, 1, owner);

        verify(validator).checkCommentAdMatch(comment, 5);
        verify(validator).checkCommentOwnership(comment, owner);
        verify(commentRepository).deleteById(1);
    }

    @Test
    void deleteCommentWrongAdId() {
        UserDetails owner = createUserDetails("owner@mail.com");
        Comment comment = createComment(1, 5, "owner@mail.com");

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        doThrow(new ResourceNotFoundException("Not found")).when(validator).checkCommentAdMatch(comment, 99);

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(99, 1, owner));
        verify(commentRepository, never()).deleteById(any());
    }

    @Test
    void deleteCommentForbidden() {
        UserDetails other = createUserDetails("other@mail.com");
        Comment comment = createComment(1, 5, "owner@mail.com");

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        doThrow(new ForbiddenException("No access")).when(validator).checkCommentOwnership(comment, other);

        assertThrows(ForbiddenException.class, () -> commentService.deleteComment(5, 1, other));
        verify(commentRepository, never()).deleteById(any());
    }

    @Test
    void updateCommentSuccess() {
        UserDetails owner = createUserDetails("owner@mail.com");
        Comment comment = createComment(1, 5, "owner@mail.com");
        CreateOrUpdateCommentDto dto = new CreateOrUpdateCommentDto("Updated text");
        CommentDto commentDto = new CommentDto(1, 1, "Иван", null, "Updated text", 3000L);

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(5, 1, dto, owner);

        assertEquals("Updated text", result.text());
        verify(commentMapper).updateComment(dto, comment);
    }

    @Test
    void updateCommentForbidden() {
        UserDetails other = createUserDetails("other@mail.com");
        Comment comment = createComment(1, 5, "owner@mail.com");
        CreateOrUpdateCommentDto dto = new CreateOrUpdateCommentDto("Hacked text");

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        doThrow(new ForbiddenException("No access")).when(validator).checkCommentOwnership(comment, other);

        assertThrows(ForbiddenException.class, () -> commentService.updateComment(5, 1, dto, other));
        verify(commentRepository, never()).save(any());
    }
}

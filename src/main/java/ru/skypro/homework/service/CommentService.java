package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.comment.CommentDto;
import ru.skypro.homework.dto.comment.CommentsDto;
import ru.skypro.homework.dto.comment.CreateOrUpdateCommentDto;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.user.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final ValidatorService validator;

    @Transactional(readOnly = true)
    public CommentsDto getCommentsByAdId(Integer adId) {
        if (!adRepository.existsById(adId)) {
            throw new ResourceNotFoundException(ResourceNotFoundException.AD_NOT_FOUND);
        }
        List<CommentDto> comments = commentRepository.findByAdId(adId).stream()
                .map(commentMapper::toDto)
                .toList();
        return new CommentsDto(comments.size(), comments);
    }

    @Transactional
    public CommentDto createComment(Integer adId, CreateOrUpdateCommentDto dto, UserDetails userDetails) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.AD_NOT_FOUND));
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.USER_NOT_FOUND));
        Comment comment = commentMapper.toEntity(dto);
        comment.setAd(ad);
        comment.setAuthor(user);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Integer adId, Integer commentId, UserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.COMMENT_NOT_FOUND));
        validator.checkCommentAdMatch(comment, adId);
        validator.checkCommentOwnership(comment, userDetails);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public CommentDto updateComment(Integer adId, Integer commentId, CreateOrUpdateCommentDto dto, UserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceNotFoundException.COMMENT_NOT_FOUND));
        validator.checkCommentAdMatch(comment, adId);
        validator.checkCommentOwnership(comment, userDetails);
        commentMapper.updateComment(dto, comment);
        return commentMapper.toDto(commentRepository.save(comment));
    }
}
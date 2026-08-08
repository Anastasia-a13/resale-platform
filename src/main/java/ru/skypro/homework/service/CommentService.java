package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.comment.CommentDto;
import ru.skypro.homework.dto.comment.CommentsDto;
import ru.skypro.homework.dto.comment.CreateOrUpdateCommentDto;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentsDto getCommentsByAdId(Integer adId, UserDetails userDetails) {
    }

    public CommentDto createComment(Integer adId, CreateOrUpdateCommentDto dto, UserDetails userDetails) {
    }

    public void deleteComment(Integer adId, Integer commentId, UserDetails userDetails) {
    }

    public CommentDto updateComment(Integer adId, Integer commentId, CreateOrUpdateCommentDto dto, UserDetails userDetails) {
    }
}
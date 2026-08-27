package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.config.UserDetailsServiceImpl;
import ru.skypro.homework.config.WebSecurityConfig;
import ru.skypro.homework.dto.comment.CommentDto;
import ru.skypro.homework.dto.comment.CommentsDto;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CommentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@Import(WebSecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCommentsSuccess() throws Exception {
        CommentsDto dto = new CommentsDto(1, List.of(
                new CommentDto(1, 1, "Иван", "img.jpg", "Great!", 1000L)
        ));
        when(commentService.getCommentsByAdId(1)).thenReturn(dto);

        mockMvc.perform(get("/ads/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].text").value("Great!"));
    }

    @Test
    void getCommentsNotFound() throws Exception {
        when(commentService.getCommentsByAdId(99)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/ads/99/comments"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void addCommentSuccess() throws Exception {
        CommentDto dto = new CommentDto(1, 1, "Иван", null, "New comment", 2000L);
        when(commentService.createComment(anyInt(), any(), any())).thenReturn(dto);

        mockMvc.perform(post("/ads/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"New comment text\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("New comment"));
    }

    @Test
    void addCommentUnauthorized() throws Exception {
        mockMvc.perform(post("/ads/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"New comment text\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "owner@mail.com", roles = "USER")
    void deleteCommentSuccess() throws Exception {
        mockMvc.perform(delete("/ads/1/comments/1"))
                .andExpect(status().isNoContent());
        verify(commentService).deleteComment(eq(1), eq(1), any());
    }

    @Test
    @WithMockUser(username = "other@mail.com", roles = "USER")
    void deleteCommentForbidden() throws Exception {
        doThrow(new ForbiddenException("No access")).when(commentService).deleteComment(eq(1), eq(1), any());

        mockMvc.perform(delete("/ads/1/comments/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "owner@mail.com", roles = "USER")
    void updateCommentSuccess() throws Exception {
        CommentDto dto = new CommentDto(1, 1, "Иван", null, "Updated text", 3000L);
        when(commentService.updateComment(anyInt(), anyInt(), any(), any())).thenReturn(dto);

        mockMvc.perform(patch("/ads/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Updated comment text\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated text"));
    }

    @Test
    @WithMockUser(username = "other@mail.com", roles = "USER")
    void updateCommentForbidden() throws Exception {
        when(commentService.updateComment(anyInt(), anyInt(), any(), any()))
                .thenThrow(new ForbiddenException("No access"));

        mockMvc.perform(patch("/ads/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Hacked comment text\"}"))
                .andExpect(status().isForbidden());
    }
}

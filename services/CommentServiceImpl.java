package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.CommentDto;
import com.example.moviesbymood.models.Comment;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.CommentRepository;
import com.example.moviesbymood.repositories.MovieRepository;
import com.example.moviesbymood.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MovieRepository   movieRepository;
    private final UserRepository    userRepository;

    private User current() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + email));
    }

    @Override
    public void save(Long movieId, CommentDto dto) {
        User user = current();
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Нет фильма: " + movieId));
        Comment c = new Comment();
        c.setCommentMovie(movie);
        c.setCommentUser(user);
        c.setCommentText(dto.getCommentText());
        c.setCommentCreatedAt(Instant.now());
        commentRepository.save(c);
    }

    @Override
    public List<Comment> findByMovie(Long movieId) {
        return commentRepository.findByCommentMovie_MovieIdOrderByCommentCreatedAtDesc(movieId);
    }

    @Override
    public Comment addComment(Long movieId, String userEmail, String text) {
        User user  = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Нет фильма: " + movieId));
        Comment c = new Comment();
        c.setCommentUser(user);
        c.setCommentMovie(movie);
        c.setCommentText(text);
        c.setCommentCreatedAt(Instant.now());
        return commentRepository.save(c);
    }

    @Override
    public List<CommentDto> findAllComments() {
        return commentRepository
                .findAllByOrderByCommentCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private CommentDto toDto(Comment c) {
        LocalDateTime ldt = LocalDateTime.ofInstant(
                c.getCommentCreatedAt(),
                ZoneId.systemDefault()
        );

        return CommentDto.builder()
                .commentId(c.getCommentId())
                .commentText(c.getCommentText())
                .commentCreatedAt(ldt)
                .userId(c.getCommentUser().getUserId())
                .userNickname(c.getCommentUser().getUserNickname())
                .contextType("MOVIE")
                .contextId(c.getCommentMovie().getMovieId())
                .contextTitle(c.getCommentMovie().getMovieTitle())
                .build();
    }
}

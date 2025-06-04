package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.CommentRequest;
import com.example.moviesbymood.dto.RatingRequest;
import com.example.moviesbymood.models.Comment;
import com.example.moviesbymood.models.Rating;
import com.example.moviesbymood.services.CommentService;
import com.example.moviesbymood.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/movies/{movieId}")
@RequiredArgsConstructor
@Validated
public class AjaxCsrfController {

    private final RatingService ratingService;
    private final CommentService commentService;

    @PostMapping("/rate")
    public ResponseEntity<Map<String, Object>> rateMovie(
            @PathVariable Long movieId,
            @RequestBody @Validated RatingRequest req,
            @AuthenticationPrincipal UserDetails user
    ) {
        Rating updated = ratingService.saveOrUpdate(movieId, user.getUsername(), req.getScore());
        double avg = ratingService.getAverageScore(movieId);
        return ResponseEntity.ok(Map.of(
                "userScore", updated.getRatingScore(),
                "averageScore", avg
        ));
    }

    @PostMapping("/comment")
    public ResponseEntity<Comment> commentMovie(
            @PathVariable Long movieId,
            @RequestBody @Validated CommentRequest req,
            @AuthenticationPrincipal UserDetails user
    ) {
        Comment c = commentService.addComment(movieId, user.getUsername(), req.getCommentText());
        return ResponseEntity.ok(c);
    }
}

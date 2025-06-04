package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.SentimentRequest;
import com.example.moviesbymood.dto.SentimentResult;
import com.example.moviesbymood.dto.TextRazorSentimentResponse;
import com.example.moviesbymood.services.TextRazorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SentimentController {

    private final TextRazorService textRazorService;

    @Value("${mood.id.positive:1}")
    private Long positiveMoodId;

    @Value("${mood.id.negative:2}")
    private Long negativeMoodId;

    @Value("${mood.id.neutral:3}")
    private Long neutralMoodId;

    @PostMapping("/sentiment")
    public ResponseEntity<?> analyze(@RequestBody SentimentRequest request) {
        String text = request.getText();
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Поле 'text' не должно быть пустым"));
        }

        try {
            TextRazorSentimentResponse trResponse = textRazorService.analyzeSentiment(text);
            double score = trResponse.getResponse().getSentiment().getScore();

            String type;
            Long moodId;
            if (score > 0.1) {
                type = "positive";
                moodId = positiveMoodId;
            } else if (score < -0.1) {
                type = "negative";
                moodId = negativeMoodId;
            } else {
                type = "neutral";
                moodId = neutralMoodId;
            }

            SentimentResult result = new SentimentResult(score, type, moodId);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("error", "Не удалось обработать тональность: " + e.getMessage()));
        }
    }
}

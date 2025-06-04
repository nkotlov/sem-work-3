package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.TextRazorSentimentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class TextRazorService {
    private static final String TEXTRAZOR_URL = "https://api.textrazor.com/";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${textrazor.api.key}")
    private String apiKey;

    @PostConstruct
    private void validateApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Не задан textrazor.api.key в application.properties");
        }
    }

    public TextRazorSentimentResponse analyzeSentiment(String text) throws IOException, InterruptedException {
        String formData = "apiKey=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
                + "&extractors=sentiment"
                + "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TEXTRAZOR_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("TextRazor вернул статус " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), TextRazorSentimentResponse.class);
    }
}

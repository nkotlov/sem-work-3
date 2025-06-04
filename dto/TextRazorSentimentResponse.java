package com.example.moviesbymood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextRazorSentimentResponse {

    @JsonProperty("response")
    private Response response;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        @JsonProperty("sentiment")
        private Sentiment sentiment;

        public Sentiment getSentiment() {
            return sentiment;
        }
        public void setSentiment(Sentiment sentiment) {
            this.sentiment = sentiment;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sentiment {
        @JsonProperty("score")
        private double score;

        public double getScore() {
            return score;
        }
        public void setScore(double score) {
            this.score = score;
        }
    }

    public Response getResponse() {
        return response;
    }
    public void setResponse(Response response) {
        this.response = response;
    }
}

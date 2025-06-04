package com.example.moviesbymood.dto;

public class SentimentResult {
    private double score;
    private String type;
    private Long moodId;

    public SentimentResult() {}

    public SentimentResult(double score, String type, Long moodId) {
        this.score = score;
        this.type = type;
        this.moodId = moodId;
    }

    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Long getMoodId() {
        return moodId;
    }
    public void setMoodId(Long moodId) {
        this.moodId = moodId;
    }
}

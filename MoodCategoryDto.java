package com.example.moviesbymood.dto;

import com.example.moviesbymood.models.MoodCategory;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoodCategoryDto {
    private Long moodId;
    private String moodName;
    private String moodDescription;
    @Builder.Default
    private Set<Long> movieIds = new HashSet<>();

    public static MoodCategoryDto fromEntity(MoodCategory m) {
        return MoodCategoryDto.builder()
                .moodId(m.getMoodId())
                .moodName(m.getMoodName())
                .moodDescription(m.getMoodDescription())
                .movieIds(
                        m.getMoodMovies().stream()
                                .map(movie -> movie.getMovieId())
                                .collect(Collectors.toSet())
                )
                .build();
    }

    public MoodCategory toEntity() {
        MoodCategory m = new MoodCategory();
        m.setMoodName(this.moodName);
        m.setMoodDescription(this.moodDescription);
        return m;
    }
}

package com.example.moviesbymood.dto;

import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.models.Movie;
import lombok.*;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistDto {
    private Long playlistId;
    private String playlistName;
    private Instant playlistCreatedAt;
    private Long userId;
    private Set<Long> movieIds;
    private String playlistPoster;

    public static MovieDto fromEntity(Movie movie) {
        if (movie == null) return null;
        String poster = null;
        FileInfo fi = movie.getMoviePoster();
        if (fi != null && fi.getFileInfoFilename() != null) {
            poster = "/files/" + fi.getFileInfoFilename();
        }
        return MovieDto.builder()
                .movieId(movie.getMovieId())
                .movieTitle(movie.getMovieTitle())
                .movieDescription(movie.getMovieDescription())
                .movieReleaseDate(movie.getMovieReleaseDate())
                .movieDuration(movie.getMovieDuration())
                .moviePoster(poster)
                .build();
    }
}

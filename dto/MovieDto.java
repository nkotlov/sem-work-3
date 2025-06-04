package com.example.moviesbymood.dto;

import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.models.Actor;
import com.example.moviesbymood.models.Director;
import com.example.moviesbymood.models.Genre;
import com.example.moviesbymood.models.MoodCategory;
import lombok.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Long movieId;
    private String movieTitle;
    private String movieDescription;
    private LocalDate movieReleaseDate;
    private Integer movieDuration;
    private String moviePoster;

    private Set<Long> genreIds;
    private Set<Long> actorIds;
    private Set<Long> directorIds;
    private Set<Long> moodIds;

    private Set<GenreDto> genres;
    private Set<ActorDto> actors;
    private Set<DirectorDto> directors;
    private Set<MoodCategoryDto> moods;

    private boolean movieFavorite;
    private Double movieAverageRating;
    private List<CommentDto> comments;
    private List<RatingDto> ratings;

    public static MovieDto fromEntity(Movie movie) {
        if (movie == null) {
            return null;
        }

        MovieDto.MovieDtoBuilder builder = MovieDto.builder()
                .movieId(movie.getMovieId())
                .movieTitle(movie.getMovieTitle())
                .movieDescription(movie.getMovieDescription())
                .movieReleaseDate(movie.getMovieReleaseDate())
                .movieDuration(movie.getMovieDuration());

        if (movie.getMoviePoster() != null) {
            FileInfo fi = movie.getMoviePoster();
            builder.moviePoster(fi.getFileInfoFilename());
        } else {
            builder.moviePoster(null);
        }

        if (movie.getMovieGenres() != null && !movie.getMovieGenres().isEmpty()) {
            builder.genreIds(
                    movie.getMovieGenres().stream()
                            .map(Genre::getGenreId)
                            .collect(Collectors.toSet())
            );
        } else {
            builder.genreIds(Collections.emptySet());
        }

        if (movie.getMovieActors() != null && !movie.getMovieActors().isEmpty()) {
            builder.actorIds(
                    movie.getMovieActors().stream()
                            .map(Actor::getActorId)
                            .collect(Collectors.toSet())
            );
        } else {
            builder.actorIds(Collections.emptySet());
        }

        if (movie.getMovieDirectors() != null && !movie.getMovieDirectors().isEmpty()) {
            builder.directorIds(
                    movie.getMovieDirectors().stream()
                            .map(Director::getDirectorId)
                            .collect(Collectors.toSet())
            );
        } else {
            builder.directorIds(Collections.emptySet());
        }

        if (movie.getMovieMoods() != null && !movie.getMovieMoods().isEmpty()) {
            builder.moodIds(
                    movie.getMovieMoods().stream()
                            .map(MoodCategory::getMoodId)
                            .collect(Collectors.toSet())
            );
        } else {
            builder.moodIds(Collections.emptySet());
        }

        if (movie.getMovieGenres() != null && !movie.getMovieGenres().isEmpty()) {
            builder.genres(
                    movie.getMovieGenres().stream()
                            .map(GenreDto::fromEntity)
                            .collect(Collectors.toSet())
            );
        } else {
            builder.genres(Collections.emptySet());
        }

        if (movie.getMovieActors() != null && !movie.getMovieActors().isEmpty()) {
            builder.actors(
                    movie.getMovieActors().stream()
                            .map(ActorDto::fromEntity)
                            .collect(Collectors.toSet())
            );
        } else {
            builder.actors(Collections.emptySet());
        }

        if (movie.getMovieDirectors() != null && !movie.getMovieDirectors().isEmpty()) {
            builder.directors(
                    movie.getMovieDirectors().stream()
                            .map(DirectorDto::fromEntity)
                            .collect(Collectors.toSet())
            );
        } else {
            builder.directors(Collections.emptySet());
        }

        if (movie.getMovieMoods() != null && !movie.getMovieMoods().isEmpty()) {
            builder.moods(
                    movie.getMovieMoods().stream()
                            .map(MoodCategoryDto::fromEntity)
                            .collect(Collectors.toSet())
            );
        } else {
            builder.moods(Collections.emptySet());
        }
        builder.movieFavorite(false);
        builder.movieAverageRating(null);
        builder.comments(Collections.emptyList());
        builder.ratings(Collections.emptyList());

        return builder.build();
    }
}

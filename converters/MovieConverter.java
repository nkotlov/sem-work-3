package com.example.moviesbymood.converters;

import com.example.moviesbymood.dto.MovieDto;
import com.example.moviesbymood.models.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieConverter implements Converter<Movie, MovieDto> {
    private final GenreConverter        genreConverter;
    private final ActorConverter        actorConverter;
    private final DirectorConverter     directorConverter;
    private final MoodCategoryConverter moodCategoryConverter;

    @Override
    public MovieDto convert(Movie m) {
        return MovieDto.builder()
                .movieId(m.getMovieId())
                .movieTitle(m.getMovieTitle())
                .movieDescription(m.getMovieDescription())
                .movieReleaseDate(m.getMovieReleaseDate())
                .movieDuration(m.getMovieDuration())
                .moviePoster(m.getMoviePoster() != null
                        ? "/files/" + m.getMoviePoster().getFileInfoFilename()
                        : null)
                .genres(m.getMovieGenres().stream()
                        .map(genreConverter::convert)
                        .collect(Collectors.toSet()))
                .actors(m.getMovieActors().stream()
                        .map(actorConverter::convert)
                        .collect(Collectors.toSet()))
                .directors(m.getMovieDirectors().stream()
                        .map(directorConverter::convert)
                        .collect(Collectors.toSet()))
                .moods(m.getMovieMoods().stream()
                        .map(moodCategoryConverter::convert)
                        .collect(Collectors.toSet()))
                .build();
    }
}

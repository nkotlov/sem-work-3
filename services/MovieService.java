package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.MovieDto;
import com.example.moviesbymood.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface MovieService {
    List<MovieDto> findAllVisible();
    MovieDto findDtoById(Long id);
    Movie create(MovieDto dto);
    Movie update(Long id, MovieDto dto);
    void delete(Long id);
    Movie save(Movie movie);
    Set<Movie> findAllByIds(Set<Long> ids);
    List<Movie> findAll();
    List<MovieDto> findByMood(Long moodId);
    Page<Movie> smartSearch(Long moodId, Long genreId, Long actorId, Long directorId, Pageable pageable);
    List<MovieDto> findByTitleContaining(String titlePart);
    List<MovieDto> findByActor(Long actorId);
    List<MovieDto> findByDirector(Long directorId);
    List<MovieDto> findByGenre(Long genreId);
    List<MovieDto> searchByTitle(String substring);
}

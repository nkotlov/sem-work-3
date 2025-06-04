package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.*;
import com.example.moviesbymood.models.*;
import com.example.moviesbymood.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository        movieRepo;
    private final GenreRepository        genreRepo;
    private final ActorRepository        actorRepo;
    private final DirectorRepository     directorRepo;
    private final MoodCategoryRepository moodRepo;

    private final CommentRepository      commentRepo;
    private final RatingRepository       ratingRepo;

    private final FavoriteMovieService   favoriteMovieService;
    private final RatingService          ratingService;
    private final CommentService         commentService;

    private final Converter<Genre, GenreDto>              genreConverter;
    private final Converter<Actor, ActorDto>              actorConverter;
    private final Converter<Director, DirectorDto>        directorConverter;
    private final Converter<MoodCategory, MoodCategoryDto> moodCategoryConverter;
    private final Converter<Comment, CommentDto>          commentConverter;

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> findAllVisible() {
        return movieRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDto findDtoById(Long id) {
        Movie movie = movieRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм не найден: " + id));
        return toDto(movie);
    }

    @Override
    @Transactional
    public Movie create(MovieDto dto) {
        Movie movie = new Movie();
        applyDto(movie, dto);
        return movieRepo.save(movie);
    }

    @Override
    @Transactional
    public Movie update(Long id, MovieDto dto) {
        Movie movie = movieRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм не найден: " + id));
        applyDto(movie, dto);
        return movieRepo.save(movie);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!movieRepo.existsById(id)) {
            throw new EntityNotFoundException("Фильм не найден: " + id);
        }
        commentRepo.deleteByCommentMovie_MovieId(id);
        ratingRepo.deleteByRatedMovie_MovieId(id);
        movieRepo.deleteById(id);
    }

    @Override
    public Movie save(Movie movie) {
        return movieRepo.save(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> findAll() {
        return movieRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Movie> findAllByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(movieRepo.findAllById(ids));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> findByMood(Long moodId) {
        return movieRepo.findByMovieMoods_MoodId(moodId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Movie> smartSearch(Long moodId, Long genreId, Long actorId, Long directorId, Pageable pageable) {
        return movieRepo.smartSearch(moodId, genreId, actorId, directorId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> findByTitleContaining(String titlePart) {
        return movieRepo
                .findByMovieTitleContainingIgnoreCase(titlePart)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> findByActor(Long actorId) {
        return movieRepo
                .findByMovieActors_ActorId(actorId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> findByDirector(Long directorId) {
        return movieRepo
                .findByMovieDirectors_DirectorId(directorId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> findByGenre(Long genreId) {
        return movieRepo
                .findByMovieGenres_GenreId(genreId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> searchByTitle(String q) {
        return movieRepo.findByMovieTitleContainingIgnoreCase(q).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private void applyDto(Movie m, MovieDto dto) {
        m.setMovieTitle(dto.getMovieTitle());
        m.setMovieDescription(dto.getMovieDescription());
        m.setMovieReleaseDate(dto.getMovieReleaseDate());
        m.setMovieDuration(dto.getMovieDuration());

        m.setMovieGenres(dto.getGenreIds().stream()
                .map(id -> genreRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Жанр не найден: " + id)))
                .collect(Collectors.toSet()));

        m.setMovieActors(dto.getActorIds().stream()
                .map(id -> actorRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Актер не найден: " + id)))
                .collect(Collectors.toSet()));

        m.setMovieDirectors(dto.getDirectorIds().stream()
                .map(id -> directorRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Режиссер не найден: " + id)))
                .collect(Collectors.toSet()));

        m.setMovieMoods(dto.getMoodIds().stream()
                .map(id -> moodRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Настроение не найдено: " + id)))
                .collect(Collectors.toSet()));
    }

    private MovieDto toDto(Movie m) {
        MovieDto dto = new MovieDto();

        dto.setMovieId(m.getMovieId());
        dto.setMovieTitle(m.getMovieTitle());
        dto.setMovieDescription(m.getMovieDescription());
        dto.setMovieReleaseDate(m.getMovieReleaseDate());
        dto.setMovieDuration(m.getMovieDuration());

        if (m.getMoviePoster() != null) {
            dto.setMoviePoster("/files/" + m.getMoviePoster().getFileInfoFilename());
        }

        dto.setGenreIds(m.getMovieGenres().stream()
                .map(Genre::getGenreId)
                .collect(Collectors.toSet()));
        dto.setActorIds(m.getMovieActors().stream()
                .map(Actor::getActorId)
                .collect(Collectors.toSet()));
        dto.setDirectorIds(m.getMovieDirectors().stream()
                .map(Director::getDirectorId)
                .collect(Collectors.toSet()));
        dto.setMoodIds(m.getMovieMoods().stream()
                .map(MoodCategory::getMoodId)
                .collect(Collectors.toSet()));

        dto.setGenres(m.getMovieGenres().stream()
                .map(genreConverter::convert)
                .collect(Collectors.toSet()));
        dto.setActors(m.getMovieActors().stream()
                .map(actorConverter::convert)
                .collect(Collectors.toSet()));
        dto.setDirectors(m.getMovieDirectors().stream()
                .map(directorConverter::convert)
                .collect(Collectors.toSet()));
        dto.setMoods(m.getMovieMoods().stream()
                .map(moodCategoryConverter::convert)
                .collect(Collectors.toSet()));

        dto.setMovieAverageRating(ratingService.getAverageScore(m.getMovieId()));

        dto.setRatings(ratingService.findByMovieDto(m.getMovieId()));

        dto.setComments(commentService.findByMovie(m.getMovieId()).stream()
                .map(commentConverter::convert)
                .collect(Collectors.toList()));

        dto.setMovieFavorite(favoriteMovieService.isFavoriteMovie(m.getMovieId()));

        return dto;
    }
}

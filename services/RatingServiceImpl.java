package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.RatingDto;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.models.Rating;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MovieRepository;
import com.example.moviesbymood.repositories.RatingRepository;
import com.example.moviesbymood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepo;
    private final MovieRepository movieRepo;
    private final UserRepository userRepo;

    private User findUserByEmail(String email) {
        return userRepo.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + email));
    }

    @Override
    public Rating saveOrUpdate(Long movieId, String userEmail, Short score) {
        User user = findUserByEmail(userEmail);
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Нет фильма с таким id=" + movieId));

        Optional<Rating> existing = ratingRepo
                .findByRatedMovie_MovieIdAndRatingUser_UserId(movieId, user.getUserId());

        Rating rating = existing.orElseGet(Rating::new);
        rating.setRatedMovie(movie);
        rating.setRatingUser(user);
        rating.setRatingScore(score);
        rating.setRatingCreatedAt(Instant.now());
        return ratingRepo.save(rating);
    }

    @Override
    public double getAverageScore(Long movieId) {
        return ratingRepo.findAverageByMovieId(movieId).orElse(0.0);
    }

    @Override
    public List<RatingDto> findByMovieDto(Long movieId) {
        return List.of();
    }

    @Override
    public void save(Long movieId, RatingDto dto) {
        saveOrUpdate(movieId,
                SecurityContextHolder.getContext().getAuthentication().getName(),
                dto.getScore().shortValue());
    }

    @Override
    public Optional<Rating> findByMovieAndUser(Long movieId, String userEmail) {
        User user = findUserByEmail(userEmail);
        return ratingRepo.findByRatedMovie_MovieIdAndRatingUser_UserId(movieId, user.getUserId());
    }
}

package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.MovieDto;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MovieRepository;
import com.example.moviesbymood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteMovieServiceImpl implements FavoriteMovieService {

    private final UserRepository                     userRepo;
    private final MovieRepository                    movieRepo;
    private final Converter<Movie, MovieDto>         movieDtoConverter;

    private User currentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepo.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No user: " + email));
    }

    @Override
    public Set<Long> getFavoriteMovieIds() {
        return currentUser().getFavoriteMovies()
                .stream()
                .map(Movie::getMovieId)
                .collect(Collectors.toSet());
    }

    @Override
    public List<MovieDto> getFavoriteMovies() {
        Set<Long> ids = getFavoriteMovieIds();
        return movieRepo.findAllById(ids).stream()
                .map(movieDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavoriteMovie(Long movieId) {
        return getFavoriteMovieIds().contains(movieId);
    }

    @Override
    public void toggleFavoriteMovie(Long movieId) {
        User u = currentUser();
        boolean removed = u.getFavoriteMovies()
                .removeIf(m -> m.getMovieId().equals(movieId));
        if (!removed) {
            Movie proxy = new Movie();
            proxy.setMovieId(movieId);
            u.getFavoriteMovies().add(proxy);
        }
        userRepo.save(u);
    }
}

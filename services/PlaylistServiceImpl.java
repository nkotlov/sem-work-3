package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.CreatePlaylistRequest;
import com.example.moviesbymood.dto.PlaylistDto;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.models.Playlist;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MovieRepository;
import com.example.moviesbymood.repositories.PlaylistRepository;
import com.example.moviesbymood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + email));
    }

    private PlaylistDto toDto(Playlist pl) {
        Set<Long> movieIds = pl.getPlaylistMovies().stream()
                .map(movie -> movie.getMovieId())
                .collect(Collectors.toSet());

        String posterUrl = null;
        if (pl.getPlaylistPoster() != null) {
            posterUrl = pl.getPlaylistPoster().getFileInfoUrl();
        }

        return PlaylistDto.builder()
                .playlistId(pl.getPlaylistId())
                .playlistName(pl.getPlaylistName())
                .playlistCreatedAt(pl.getPlaylistCreatedAt())
                .userId(pl.getPlaylistUser().getUserId())
                .movieIds(movieIds)
                .playlistPoster(posterUrl)
                .build();
    }

    @Override
    public PlaylistDto createPlaylist(String userEmail, CreatePlaylistRequest request) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        Playlist pl = Playlist.builder()
                .playlistName(request.getPlaylistName())
                .playlistUser(user)
                .build();
        Playlist saved = playlistRepository.save(pl);
        return toDto(saved);
    }

    @Override
    public java.util.List<PlaylistDto> getUserPlaylists(String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        return playlistRepository.findByPlaylistUserOrderByPlaylistCreatedAtDesc(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlaylistDto getPlaylistById(Long playlistId, String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        Playlist pl = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Плейлист не найден: " + playlistId));
        if (!pl.getPlaylistUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Доступ запрещён к этому плейлисту");
        }
        return toDto(pl);
    }

    @Override
    public void deletePlaylist(Long playlistId, String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        Playlist pl = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Плейлист не найден: " + playlistId));
        if (!pl.getPlaylistUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Доступ запрещён к этому плейлисту");
        }
        playlistRepository.delete(pl);
    }

    @Override
    public void addMovieToPlaylist(Long playlistId, Long movieId, String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        Playlist pl = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Плейлист не найден: " + playlistId));
        if (!pl.getPlaylistUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Доступ запрещён к этому плейлисту");
        }
        Movie mv = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Фильм не найден: " + movieId));
        pl.getPlaylistMovies().add(mv);
        playlistRepository.save(pl);
    }

    @Override
    public void removeMovieFromPlaylist(Long playlistId, Long movieId, String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        Playlist pl = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Плейлист не найден: " + playlistId));
        if (!pl.getPlaylistUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Доступ запрещён к этому плейлисту");
        }
        Movie mv = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Фильм не найден: " + movieId));
        pl.getPlaylistMovies().remove(mv);
        playlistRepository.save(pl);
    }
}

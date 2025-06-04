package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.CreatePlaylistRequest;
import com.example.moviesbymood.dto.PlaylistDto;
import com.example.moviesbymood.services.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistRestController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<?> createPlaylist(
            @RequestBody @Valid CreatePlaylistRequest request,
            BindingResult br,
            Principal principal
    ) {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(br.getAllErrors());
        }
        String userEmail = principal.getName();
        PlaylistDto created = playlistService.createPlaylist(userEmail, request);
        return ResponseEntity
                .created(URI.create("/api/playlists/" + created.getPlaylistId()))
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<PlaylistDto>> getAllPlaylists(Principal principal) {
        String userEmail = principal.getName();
        List<PlaylistDto> list = playlistService.getUserPlaylists(userEmail);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaylist(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        String userEmail = principal.getName();
        try {
            PlaylistDto dto = playlistService.getPlaylistById(id, userEmail);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlaylist(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        String userEmail = principal.getName();
        try {
            playlistService.deletePlaylist(id, userEmail);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @PostMapping("/{playlistId}/movies/{movieId}")
    public ResponseEntity<?> addMovieToPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long movieId,
            Principal principal
    ) {
        String userEmail = principal.getName();
        try {
            playlistService.addMovieToPlaylist(playlistId, movieId, userEmail);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{playlistId}/movies/{movieId}")
    public ResponseEntity<?> removeMovieFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long movieId,
            Principal principal
    ) {
        String userEmail = principal.getName();
        try {
            playlistService.removeMovieFromPlaylist(playlistId, movieId, userEmail);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }
}

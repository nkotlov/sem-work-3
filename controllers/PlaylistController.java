package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.CreatePlaylistRequest;
import com.example.moviesbymood.dto.PlaylistDto;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.services.FileStorageService;
import com.example.moviesbymood.services.MovieService;
import com.example.moviesbymood.services.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final MovieService movieService;
    private final FileStorageService fileStorageService;

    @GetMapping("/new")
    public String newPlaylistForm(Model model) {
        model.addAttribute("createRequest", new CreatePlaylistRequest());
        List<Movie> allMovies = movieService.findAll();
        model.addAttribute("allMovies", allMovies);
        return "playlists/create";
    }

    @PostMapping
    public String createPlaylist(
            @ModelAttribute("createRequest") @Valid CreatePlaylistRequest request,
            BindingResult br,
            @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
            Authentication auth,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("allMovies", movieService.findAll());
            return "playlists/create";
        }

        String userEmail = auth.getName();
        String storedFilename = null;

        if (posterFile != null && !posterFile.isEmpty()) {
            storedFilename = fileStorageService.saveFile(posterFile);
        }

        request.setPosterFilename(storedFilename);

        PlaylistDto created = playlistService.createPlaylist(userEmail, request);
        return "redirect:/playlists/" + created.getPlaylistId();
    }

    @GetMapping("/{id}")
    public String viewPlaylist(
            @PathVariable("id") Long playlistId,
            Authentication auth,
            Model model
    ) {
        String userEmail = auth.getName();
        PlaylistDto dto = playlistService.getPlaylistById(playlistId, userEmail);
        model.addAttribute("playlist", dto);
        return "playlists/details";
    }

    @PostMapping("/{id}/delete")
    public String deletePlaylist(
            @PathVariable("id") Long playlistId,
            Authentication auth
    ) {
        String userEmail = auth.getName();
        playlistService.deletePlaylist(playlistId, userEmail);
        return "redirect:/moods";
    }
}

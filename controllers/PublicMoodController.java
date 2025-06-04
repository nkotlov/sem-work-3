package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.*;
import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MoodCategoryRepository;
import com.example.moviesbymood.repositories.UserRepository;
import com.example.moviesbymood.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequestMapping("/moods")
@RequiredArgsConstructor
public class PublicMoodController {

    private final MoodCategoryService    moodCategoryService;
    private final MoodCommentService     moodCommentService;
    private final MoodRatingService      moodRatingService;
    private final MovieService           movieService;
    private final UserRepository         userRepository;
    private final MoodCategoryRepository moodCategoryRepository;
    private final PlaylistService        playlistService;

    @GetMapping
    public String catalog(Model model, Principal principal) {
        List<MoodCategoryDto> moods = moodCategoryService.findAll();
        model.addAttribute("moods", moods);
        if (principal != null) {
            List<PlaylistDto> userPlaylists = playlistService.getUserPlaylists(principal.getName());
            model.addAttribute("userPlaylists", userPlaylists);
        }
        model.addAttribute("userFavoriteMoodIds", moodCategoryService.getFavoriteMoodIds());
        model.addAttribute("activeTab", "moods");
        return "moods/catalog";
    }

    @GetMapping("/{id:\\d+}")
    public String detail(
            @PathVariable Long id,
            @RequestParam(name = "fromProfile", required = false) Boolean fromProfile,
            Model model,
            Principal principal
    ) {
        MoodCategory entity = moodCategoryService.findEntity(id);
        MoodCategoryDto dto = MoodCategoryDto.fromEntity(entity);
        model.addAttribute("mood", dto);

        boolean isFav = (principal != null && moodCategoryService.isFavorite(id));
        model.addAttribute("isFavoriteMood", isFav);

        model.addAttribute("comments", moodCommentService.findByMood(id));

        double avg = moodRatingService.getAverageMoodScore(id);
        model.addAttribute("averageRating", avg);

        model.addAttribute("newComment", new CommentRequest());
        model.addAttribute("newRating", new RatingDto());

        Short userRating = null;
        if (principal != null) {
            String email = principal.getName();
            userRating = moodRatingService.getUserRating(id, email);
        }
        model.addAttribute("userRating", userRating);

        List<MovieDto> moodMovies = movieService.findByMood(id);
        model.addAttribute("moodMovies", moodMovies);

        model.addAttribute("fromProfile", fromProfile != null && fromProfile);

        model.addAttribute("activeTab", "moods");
        return "moods/detail";
    }

    @PostMapping("/{id}/favorite")
    @ResponseBody
    public ResponseEntity<Void> toggleFavoriteMood(
            @PathVariable Long id,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }
        User user = userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        MoodCategory moodEntity = moodCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        moodCategoryService.toggle(id, user, moodEntity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/comments")
    public String addComment(
            @PathVariable Long id,
            @ModelAttribute("newComment") @Valid CommentRequest requestDto,
            BindingResult br,
            Principal principal
    ) {
        if (!br.hasErrors() && principal != null) {
            String userEmail = principal.getName();
            moodCommentService.saveForMood(id, userEmail, requestDto.getCommentText());
        }
        return "redirect:/moods/" + id;
    }

    @PostMapping("/{id}/rating")
    public String saveRating(
            @PathVariable Long id,
            @ModelAttribute("newRating") @Valid RatingDto dto,
            BindingResult br,
            Principal principal
    ) {
        if (!br.hasErrors() && principal != null) {
            moodRatingService.saveForMood(id, dto);
        }
        return "redirect:/moods/" + id;
    }
}

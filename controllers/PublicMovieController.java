package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.CommentDto;
import com.example.moviesbymood.dto.MovieDto;
import com.example.moviesbymood.dto.RatingDto;
import com.example.moviesbymood.models.Comment;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequestMapping("/movies")
@RequiredArgsConstructor
public class PublicMovieController {

    private final MovieService          movieService;
    private final FavoriteMovieService  favoriteMovieService;
    private final CommentService        commentService;
    private final RatingService         ratingService;
    private final MoodCategoryService   moodCategoryService;
    private final GenreService          genreService;
    private final ActorService          actorService;
    private final DirectorService       directorService;

    @GetMapping
    public String catalog(
            @RequestParam(name = "mood",     required = false) Long moodId,
            @RequestParam(name = "genre",    required = false) Long genreId,
            @RequestParam(name = "actor",    required = false) Long actorId,
            @RequestParam(name = "director", required = false) Long directorId,
            @RequestParam(name = "page",     defaultValue = "0") int page,
            Model model,
            Principal principal
    ) {
        PageRequest pageRequest = PageRequest.of(page, 10);

        Page<Movie> pageResult = movieService.smartSearch(moodId, genreId, actorId, directorId, pageRequest);

        List<MovieDto> movies = pageResult.stream()
                .map(movie -> movieService.findDtoById(movie.getMovieId()))
                .collect(Collectors.toList());

        model.addAttribute("movies", movies);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("currentPage", pageResult.getNumber());

        model.addAttribute("filterMood", moodId);
        model.addAttribute("filterGenre", genreId);
        model.addAttribute("filterActor", actorId);
        model.addAttribute("filterDirector", directorId);

        model.addAttribute("allMoods",     moodCategoryService.findAllDto());
        model.addAttribute("allGenres",    genreService.findAllDto());
        model.addAttribute("allActors",    actorService.findAllDto());
        model.addAttribute("allDirectors", directorService.findAllDto());

        Set<Long> userFavIds = Collections.emptySet();
        if (principal != null) {
            userFavIds = favoriteMovieService.getFavoriteMovieIds();
        }
        model.addAttribute("userFavorites", userFavIds);

        model.addAttribute("positiveMoodId", 1L);
        model.addAttribute("negativeMoodId", 2L);
        model.addAttribute("neutralMoodId",  3L);

        return "movies/catalog";
    }

    @GetMapping("/{id:\\d+}")
    public String detail(
            @PathVariable Long id,
            @RequestParam(name = "fromMood",    required = false) Long fromMood,
            @RequestParam(name = "fromProfile", required = false) Boolean fromProfile,
            Model model,
            Principal principal
    ) {
        MovieDto movieDto = movieService.findDtoById(id);
        model.addAttribute("movie", movieDto);

        boolean isFav = false;
        if (principal != null) {
            isFav = favoriteMovieService.isFavoriteMovie(id);
        }
        model.addAttribute("isFavorite", isFav);

        model.addAttribute("fromMood", fromMood);

        model.addAttribute("fromProfile", fromProfile != null && fromProfile);

        List<Comment> comments = commentService.findByMovie(id);
        model.addAttribute("comments", comments);

        double avg = ratingService.getAverageScore(id);
        model.addAttribute("averageRating", avg);

        model.addAttribute("newComment", new CommentDto());
        model.addAttribute("newRating",  new RatingDto());

        Integer userRating = null;
        if (principal != null) {
            userRating = ratingService.getUserRating(id, principal.getName());
        }
        model.addAttribute("userRating", userRating);

        model.addAttribute("activeTab", "movies");
        return "movies/detail";
    }

    @PostMapping("/{id:\\d+}/favorite")
    @ResponseBody
    public ResponseEntity<Void> toggleFavorite(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }
        favoriteMovieService.toggleFavoriteMovie(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id:\\d+}/comments")
    public String addComment(@PathVariable Long id,
                             @ModelAttribute("newComment") @Valid CommentDto dto,
                             BindingResult br) {
        if (!br.hasErrors()) {
            commentService.save(id, dto);
        }
        return "redirect:/movies/" + id;
    }

    @PostMapping("/{id:\\d+}/rating")
    public String saveRating(@PathVariable Long id,
                             @ModelAttribute("newRating") @Valid RatingDto dto,
                             BindingResult br,
                             Principal principal) {
        if (principal != null && !br.hasErrors()) {
            ratingService.save(id, dto);
        }
        return "redirect:/movies/" + id;
    }
}

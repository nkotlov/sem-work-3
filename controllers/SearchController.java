package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.*;
import com.example.moviesbymood.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final MovieService             movieService;
    private final GenreService     genreService;
    private final ActorService             actorService;
    private final DirectorService          directorService;
    private final MoodCategoryService      moodService;
    private final FavoriteMovieService     favoriteMovieService;


    @GetMapping("/search")
    public String unifiedSearch(
            Model model,
            String type,
            String q,
            Principal principal
    ) {

        model.addAttribute("searchType", type);
        model.addAttribute("query", q);

        Set<Long> userFavs = Collections.emptySet();
        if (principal != null) {
            userFavs = favoriteMovieService.getFavoriteMovieIds();
        }
        model.addAttribute("userFavorites", userFavs);

        if (q == null || q.trim().isEmpty()) {
            model.addAttribute("noResults", false);
            return "search";
        }

        String searchType = (type == null ? "movies" : type);

        switch (searchType) {
            case "movies" -> {
                List<MovieDto> foundMovies = movieService.searchByTitle(q);
                model.addAttribute("movieResults", foundMovies);

                boolean no = foundMovies.isEmpty();
                model.addAttribute("noResults", no);
            }

            case "genres" -> {
                List<GenreDto> foundGenres = genreService.searchByName(q);
                model.addAttribute("genreResults", foundGenres);

                if (foundGenres.isEmpty()) {
                    model.addAttribute("noResults", true);
                    model.addAttribute("genreMovies", Collections.emptyMap());
                } else {
                    Map<Long, List<MovieDto>> map = new HashMap<>();
                    for (GenreDto g : foundGenres) {
                        List<MovieDto> moviesInGenre = movieService.findByGenre(g.getGenreId());
                        map.put(g.getGenreId(), moviesInGenre);
                    }
                    model.addAttribute("genreMovies", map);
                    model.addAttribute("noResults", false);
                }
            }

            case "actors" -> {
                List<ActorDto> foundActors = actorService.searchByName(q);
                model.addAttribute("actorResults", foundActors);

                if (foundActors.isEmpty()) {
                    model.addAttribute("noResults", true);
                    model.addAttribute("actorMovies", Collections.emptyMap());
                } else {
                    Map<Long, List<MovieDto>> map = new HashMap<>();
                    for (ActorDto a : foundActors) {
                        List<MovieDto> moviesByActor = movieService.findByActor(a.getActorId());
                        map.put(a.getActorId(), moviesByActor);
                    }
                    model.addAttribute("actorMovies", map);
                    model.addAttribute("noResults", false);
                }
            }

            case "directors" -> {
                List<DirectorDto> foundDirectors = directorService.searchByName(q);
                model.addAttribute("directorResults", foundDirectors);

                if (foundDirectors.isEmpty()) {
                    model.addAttribute("noResults", true);
                    model.addAttribute("directorMovies", Collections.emptyMap());
                } else {
                    Map<Long, List<MovieDto>> map = new HashMap<>();
                    for (DirectorDto d : foundDirectors) {
                        List<MovieDto> moviesByDirector = movieService.findByDirector(d.getDirectorId());
                        map.put(d.getDirectorId(), moviesByDirector);
                    }
                    model.addAttribute("directorMovies", map);
                    model.addAttribute("noResults", false);
                }
            }

            case "moods" -> {
                List<MoodCategoryDto> foundMoods = moodService.searchByName(q);
                model.addAttribute("moodResults", foundMoods);

                if (foundMoods.isEmpty()) {
                    model.addAttribute("noResults", true);
                    model.addAttribute("moodMovies", Collections.emptyMap());
                } else {
                    Map<Long, List<MovieDto>> map = new HashMap<>();
                    for (MoodCategoryDto mo : foundMoods) {
                        List<MovieDto> moviesByMood = movieService.findByMood(mo.getMoodId());
                        map.put(mo.getMoodId(), moviesByMood);
                    }
                    model.addAttribute("moodMovies", map);
                    model.addAttribute("noResults", false);
                }
            }

            default -> {
                model.addAttribute("noResults", true);
            }
        }

        return "search";
    }
}
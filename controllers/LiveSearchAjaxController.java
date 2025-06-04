package com.example.moviesbymood.controllers;

import com.example.moviesbymood.repositories.ActorRepository;
import com.example.moviesbymood.repositories.DirectorRepository;
import com.example.moviesbymood.repositories.GenreRepository;
import com.example.moviesbymood.repositories.MoodCategoryRepository;
import com.example.moviesbymood.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class LiveSearchAjaxController {

    private final MovieRepository        movieRepo;
    private final GenreRepository        genreRepo;
    private final ActorRepository        actorRepo;
    private final DirectorRepository     directorRepo;
    private final MoodCategoryRepository moodRepo;

    @GetMapping(value = "/search/ajax", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> liveSearch(@RequestParam("q") String query) {
        String q = query.trim().toLowerCase();

        List<Map<String, ? extends Serializable>> movies = movieRepo.searchByTitle(q).stream()
                .map(m -> Map.of(
                        "id",    m.getMovieId(),
                        "title", m.getMovieTitle()
                ))
                .collect(Collectors.toList());

        List<Map<String, ? extends Serializable>> genres = genreRepo.searchByName(q).stream()
                .map(g -> Map.of(
                        "id",   g.getGenreId(),
                        "name", g.getGenreName()
                ))
                .collect(Collectors.toList());

        List<Map<String, ? extends Serializable>> actors = actorRepo.searchByFullName(q).stream()
                .map(a -> Map.of(
                        "id",       a.getActorId(),
                        "fullName", a.getActorFullName()
                ))
                .collect(Collectors.toList());

        List<Map<String, ? extends Serializable>> directors = directorRepo.searchByFullName(q).stream()
                .map(d -> Map.of(
                        "id",       d.getDirectorId(),
                        "fullName", d.getDirectorFullName()
                ))
                .collect(Collectors.toList());

        List<Map<String, ? extends Serializable>> moods = moodRepo.searchByName(q).stream()
                .map(mo -> Map.of(
                        "id",       mo.getMoodId(),
                        "moodName", mo.getMoodName()
                ))
                .collect(Collectors.toList());

        return Map.of(
                "movies",    movies,
                "genres",    genres,
                "actors",    actors,
                "directors", directors,
                "moods",     moods
        );
    }
}

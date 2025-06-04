package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.MovieDto;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.repositories.FileInfoRepository;
import com.example.moviesbymood.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService         movieService;
    private final GenreService         genreService;
    private final ActorService         actorService;
    private final DirectorService      directorService;
    private final MoodCategoryService  moodCategoryService;
    private final FileStorageService   fileStorageService;
    private final FileInfoRepository   fileInfoRepository;

    @GetMapping
    public String list() {
        return "redirect:/admin?tab=movies";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("movieDto",      new MovieDto());
        model.addAttribute("allGenres",     genreService.findAll());
        model.addAttribute("allActors",     actorService.findAll());
        model.addAttribute("allDirectors",  directorService.findAll());
        model.addAttribute("allMoods",      moodCategoryService.findAll());
        return "movies/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("movieDto") MovieDto dto,
            BindingResult br,
            @RequestParam("poster") MultipartFile poster,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("allGenres",    genreService.findAll());
            model.addAttribute("allActors",    actorService.findAll());
            model.addAttribute("allDirectors", directorService.findAll());
            model.addAttribute("allMoods",     moodCategoryService.findAll());
            return "movies/form";
        }

        Movie movie = movieService.create(dto);

        if (!poster.isEmpty()) {
            String storedName = fileStorageService.saveFile(poster);
            FileInfo fi = fileInfoRepository.findByFileInfoFilename(storedName)
                    .orElseThrow(() -> new IllegalStateException("FileInfo не найден по имени"));
            movie.setMoviePoster(fi);
            movieService.save(movie);
        }

        return "redirect:/admin?tab=movies";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        MovieDto dto = movieService.findDtoById(id);
        model.addAttribute("movieDto",     dto);
        model.addAttribute("allGenres",    genreService.findAll());
        model.addAttribute("allActors",    actorService.findAll());
        model.addAttribute("allDirectors", directorService.findAll());
        model.addAttribute("allMoods",     moodCategoryService.findAll());
        return "movies/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("movieDto") MovieDto dto,
            BindingResult br,
            @RequestParam("poster") MultipartFile poster,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("allGenres",    genreService.findAll());
            model.addAttribute("allActors",    actorService.findAll());
            model.addAttribute("allDirectors", directorService.findAll());
            model.addAttribute("allMoods",     moodCategoryService.findAll());
            return "movies/form";
        }

        Movie movie = movieService.update(id, dto);

        if (!poster.isEmpty()) {
            String storedName = fileStorageService.saveFile(poster);
            FileInfo fi = fileInfoRepository.findByFileInfoFilename(storedName)
                    .orElseThrow(() -> new IllegalStateException("FileInfo не найден по имени"));
            movie.setMoviePoster(fi);
            movieService.save(movie);
        }

        return "redirect:/admin?tab=movies";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        movieService.delete(id);
        return "redirect:/admin?tab=movies";
    }
}

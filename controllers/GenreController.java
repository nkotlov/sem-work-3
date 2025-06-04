package com.example.moviesbymood.controllers;

import com.example.moviesbymood.models.Genre;
import com.example.moviesbymood.services.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public String list() {
        return "redirect:/admin?tab=genres";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("genre", new Genre());
        return "genres/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("genre") Genre genre,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            return "genres/form";
        }
        genreService.create(genre);
        return "redirect:/admin?tab=genres";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Genre existing = genreService.findById(id);
        model.addAttribute("genre", existing);
        return "genres/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("genre") Genre genre,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            return "genres/form";
        }
        genreService.update(id, genre);
        return "redirect:/admin?tab=genres";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        genreService.deleteById(id);
        return "redirect:/admin?tab=genres";
    }
}

package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.DirectorDto;
import com.example.moviesbymood.services.DirectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public String list() {
        return "redirect:/admin?tab=directors";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("directorDto", new DirectorDto());
        return "directors/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("directorDto") DirectorDto directorDto,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            return "directors/form";
        }
        directorService.create(directorDto.toEntity());
        return "redirect:/admin?tab=directors";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        DirectorDto existing = DirectorDto.fromEntity(directorService.findById(id));
        model.addAttribute("directorDto", existing);
        return "directors/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("directorDto") DirectorDto directorDto,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            return "directors/form";
        }
        directorService.update(id, directorDto.toEntity());
        return "redirect:/admin?tab=directors";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        directorService.delete(id);
        return "redirect:/admin?tab=directors";
    }
}

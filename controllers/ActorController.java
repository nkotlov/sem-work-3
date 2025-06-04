package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.ActorDto;
import com.example.moviesbymood.services.ActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/actors")
public class ActorController {

    private final ActorService actorService;

    @GetMapping
    public String list() {
        return "redirect:/admin?tab=actors";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("actorDto", new ActorDto());
        return "actors/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("actorDto") ActorDto actorDto,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            return "actors/form";
        }
        actorService.create(actorDto.toEntity());
        return "redirect:/admin?tab=actors";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ActorDto existing = ActorDto.fromEntity(actorService.findById(id));
        model.addAttribute("actorDto", existing);
        return "actors/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("actorDto") ActorDto actorDto,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            return "actors/form";
        }
        actorService.update(id, actorDto.toEntity());
        return "redirect:/admin?tab=actors";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        actorService.delete(id);
        return "redirect:/admin?tab=actors";
    }
}

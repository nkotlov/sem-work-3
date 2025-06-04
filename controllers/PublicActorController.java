package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.ActorDto;
import com.example.moviesbymood.services.ActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/actors")
@RequiredArgsConstructor
public class PublicActorController {
    private final ActorService actorService;

    @GetMapping("/{id:\\d+}")
    public String detail(
            @PathVariable Long id,
            @RequestParam(name = "fromMovie", required = false) Long fromMovie,
            Model model
    ) {
        ActorDto dto = actorService.findDtoById(id);
        model.addAttribute("actor", dto);
        model.addAttribute("fromMovie", fromMovie);
        return "actors/detail";
    }
}

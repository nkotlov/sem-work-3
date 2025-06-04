package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.DirectorDto;
import com.example.moviesbymood.services.DirectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/directors")
@RequiredArgsConstructor
public class PublicDirectorController {
    private final DirectorService directorService;

    @GetMapping("/{id:\\d+}")
    public String detail(
            @PathVariable Long id,
            @RequestParam(name = "fromMovie", required = false) Long fromMovie,
            Model model
    ) {
        DirectorDto dto = directorService.findDtoById(id);
        model.addAttribute("director", dto);
        model.addAttribute("fromMovie", fromMovie);
        return "directors/detail";
    }
}

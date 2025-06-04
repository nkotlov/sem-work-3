package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.*;
import com.example.moviesbymood.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MovieService        movieService;
    private final GenreService        genreService;
    private final ActorService        actorService;
    private final DirectorService     directorService;
    private final MoodCategoryService moodCategoryService;
    private final UserService         userService;
    private final CommentService      commentService;
    private final MoodCommentService  moodCommentService;

    @GetMapping
    public String dashboard(
            @RequestParam(name = "tab", required = false, defaultValue = "movies") String activeTab,
            Model model
    ) {
        model.addAttribute("activeTab", activeTab);

        model.addAttribute("movies", movieService.findAllVisible());
        model.addAttribute("newMovieDto", new MovieDto());

        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("newGenre", new GenreDto());

        model.addAttribute("actors", actorService.findAll());
        model.addAttribute("newActor", new ActorDto());

        model.addAttribute("directors", directorService.findAll());
        model.addAttribute("newDirector", new DirectorDto());

        model.addAttribute("moods", moodCategoryService.findAllDto());
        model.addAttribute("newMood", new MoodCategoryDto());

        model.addAttribute("users", userService.findAllUsers());

        model.addAttribute("comments", commentService.findAllComments());

        model.addAttribute("moodComments", moodCommentService.findAllComments());

        return "admin/dashboard";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(
            @PathVariable("id") Long userId,
            @RequestParam(name = "tab", required = false, defaultValue = "users") String activeTab
    ) {
        userService.deleteById(userId);
        return "redirect:/admin?tab=" + activeTab;
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(
            @PathVariable("id") Long commentId,
            @RequestParam(name = "tab", required = false, defaultValue = "comments") String activeTab
    ) {
        commentService.deleteById(commentId);
        return "redirect:/admin?tab=" + activeTab;
    }

    @PostMapping("/mood-comments/{id}/delete")
    public String deleteMoodComment(
            @PathVariable("id") Long moodCommentId,
            @RequestParam(name = "tab", required = false, defaultValue = "moodComments") String activeTab
    ) {
        moodCommentService.deleteById(moodCommentId);
        return "redirect:/admin?tab=" + activeTab;
    }
}

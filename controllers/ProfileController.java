package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.MovieDto;
import com.example.moviesbymood.dto.UserDto;
import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.FileInfoRepository;
import com.example.moviesbymood.repositories.UserRepository;
import com.example.moviesbymood.services.FavoriteMovieService;
import com.example.moviesbymood.services.FavoriteMoodService;
import com.example.moviesbymood.services.FileStorageService;
import com.example.moviesbymood.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService           userService;
    private final UserRepository        userRepo;
    private final FileStorageService    fileStorageService;
    private final FileInfoRepository    fileInfoRepo;
    private final FavoriteMovieService  favoriteMovieService;
    private final FavoriteMoodService   favoriteMoodService;

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        UserDto userDto = userService.findByUsername(principal.getName());
        model.addAttribute("user", userDto);

        List<MovieDto> favMovies   = favoriteMovieService.getFavoriteMovies();
        Set<Long>      favMovieIds = favoriteMovieService.getFavoriteMovieIds();
        model.addAttribute("favoriteMovies", favMovies);
        model.addAttribute("favoriteMovieIds", favMovieIds);

        model.addAttribute("favoriteMoods", favoriteMoodService.getFavoriteMoods());
        model.addAttribute("favoriteMoodIds", favoriteMoodService.getFavoriteMoodIds());

        model.addAttribute("activeTab", "profile");
        return "profile";
    }

    @PostMapping("/profile/avatar")
    public String updateAvatar(@RequestParam("avatar") MultipartFile avatar,
                               Principal principal) {
        if (!avatar.isEmpty()) {
            String stored = fileStorageService.saveFile(avatar);
            FileInfo fi = fileInfoRepo.findByFileInfoFilename(stored)
                    .orElseThrow();
            User u = userRepo.findByUserEmail(principal.getName())
                    .orElseThrow();
            u.setAvatar(fi);
            userRepo.save(u);
        }
        return "redirect:/profile";
    }

    @GetMapping("/profile/change-password")
    public String showChangePasswordForm() {
        return "change_password";
    }

    @PostMapping("/profile/change-password")
    public String processChangePassword(@RequestParam("currentPassword") String currentPassword,
                                        @RequestParam("newPassword") String newPassword,
                                        @RequestParam("confirmPassword") String confirmPassword,
                                        Principal principal,
                                        Model model) {
        User user = userRepo.findByUserEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(currentPassword, user.getUserPassword())) {
            model.addAttribute("error", "Текущий пароль указан неверно");
            return "change_password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Новый пароль и подтверждение не совпадают");
            return "change_password";
        }

        String encodedNew = encoder.encode(newPassword);
        user.setUserPassword(encodedNew);
        user.setOauthOnly(false);
        userRepo.save(user);

        return "redirect:/profile?passwordChanged";
    }

    @GetMapping("/profile/set-password")
    public String setPasswordForm() {
        return "set_password";
    }

    @PostMapping("/profile/set-password")
    public String processSetPassword(@RequestParam("newPassword") String newPassword,
                                     @RequestParam("confirmPassword") String confirmPassword,
                                     Principal principal,
                                     Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Пароли не совпадают");
            return "set_password";
        }
        User user = userRepo.findByUserEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String encoded = new BCryptPasswordEncoder().encode(newPassword);
        user.setUserPassword(encoded);
        user.setOauthOnly(false);
        userRepo.save(user);

        return "redirect:/profile?passwordSet";
    }

    @PostMapping("/profile/favorite/movie/{id}")
    @ResponseBody
    public void toggleProfileMovie(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        userRepo.findByUserEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        favoriteMovieService.toggleFavoriteMovie(id);
    }

    @PostMapping("/profile/favorite/mood/{id}")
    @ResponseBody
    public void toggleProfileMood(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        userRepo.findByUserEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        favoriteMoodService.toggleFavoriteMood(id);
    }
}

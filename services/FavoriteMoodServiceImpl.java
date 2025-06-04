package com.example.moviesbymood.services;

import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MoodCategoryRepository;
import com.example.moviesbymood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteMoodServiceImpl implements FavoriteMoodService {
    private final UserRepository           userRepo;
    private final MoodCategoryRepository   moodRepo;

    @Override
    public Set<Long> getFavoriteMoodIds() {
        return currentUser().getFavoriteMoods()
                .stream()
                .map(MoodCategory::getMoodId)
                .collect(Collectors.toSet());
    }

    @Override
    public List<MoodCategory> getFavoriteMoods() {
        return currentUser().getFavoriteMoods()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavoriteMood(Long moodId) {
        return currentUser().getFavoriteMoods()
                .stream()
                .anyMatch(m -> m.getMoodId().equals(moodId));
    }

    @Override
    public void toggleFavoriteMood(Long moodId) {
        User user = currentUser();
        MoodCategory mood = moodRepo.getReferenceById(moodId);
        if (!user.getFavoriteMoods().remove(mood)) {
            user.getFavoriteMoods().add(mood);
        }
        userRepo.save(user);
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findByUserEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("No user: " + auth.getName()));
    }
}

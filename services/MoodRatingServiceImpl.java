package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.RatingDto;
import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.models.MoodRating;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MoodCategoryRepository;
import com.example.moviesbymood.repositories.MoodRatingRepository;
import com.example.moviesbymood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MoodRatingServiceImpl implements MoodRatingService {

    private final MoodRatingRepository moodRatingRepo;
    private final MoodCategoryRepository moodCategoryRepo;
    private final UserRepository userRepo;

    private User findUserByEmail(String email) {
        return userRepo.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + email));
    }

    private MoodCategory findMoodById(Long moodId) {
        return moodCategoryRepo.findById(moodId)
                .orElseThrow(() -> new IllegalArgumentException("MoodCategory не найден с id = " + moodId));
    }

    @Override
    public Short getUserRating(Long moodId, String userEmail) {
        User user = findUserByEmail(userEmail);
        Optional<MoodRating> optional = moodRatingRepo
                .findByRatedMood_MoodIdAndRatingUser_UserId(moodId, user.getUserId());
        return optional.map(MoodRating::getMoodRatingScore).orElse(null);
    }

    @Override
    public double getAverageMoodScore(Long moodId) {
        return moodRatingRepo.findAverageByMoodId(moodId).orElse(0.0);
    }

    @Override
    public void saveForMood(Long moodId, RatingDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = findUserByEmail(email);
        MoodCategory mood = findMoodById(moodId);

        Optional<MoodRating> existing = moodRatingRepo
                .findByRatedMood_MoodIdAndRatingUser_UserId(moodId, user.getUserId());

        MoodRating rating;
        if (existing.isPresent()) {
            rating = existing.get();
            rating.setMoodRatingScore(dto.getScore().shortValue());
            rating.setMoodRatingCreatedAt(Instant.now());
        } else {
            rating = MoodRating.builder()
                    .ratedMood(mood)
                    .ratingUser(user)
                    .moodRatingScore(dto.getScore().shortValue())
                    .moodRatingCreatedAt(Instant.now())
                    .build();
        }

        moodRatingRepo.save(rating);
    }
}

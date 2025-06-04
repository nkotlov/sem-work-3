package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.CommentDto;
import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.models.MoodComment;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MoodCategoryRepository;
import com.example.moviesbymood.repositories.MoodCommentRepository;
import com.example.moviesbymood.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MoodCommentServiceImpl implements MoodCommentService {

    private final MoodCommentRepository  moodCommentRepository;
    private final MoodCategoryRepository moodCategoryRepository;
    private final UserRepository         userRepository;

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + email));
    }

    @Override
    public List<MoodComment> findByMood(Long moodId) {
        return moodCommentRepository.findByCommentMood_MoodIdOrderByMoodCommentCreatedAtDesc(moodId);
    }

    @Override
    public MoodComment saveForMood(Long moodId, String userEmail, String text) {
        User user  = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        MoodCategory mood = moodCategoryRepository.findById(moodId)
                .orElseThrow(() -> new IllegalArgumentException("Нет настроения: " + moodId));

        MoodComment mc = new MoodComment();
        mc.setCommentUser(user);
        mc.setCommentMood(mood);
        mc.setMoodCommentText(text);
        mc.setMoodCommentCreatedAt(Instant.now());
        return moodCommentRepository.save(mc);
    }

    @Override
    public List<CommentDto> findAllComments() {
        return moodCommentRepository
                .findAllByOrderByMoodCommentCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        moodCommentRepository.deleteById(id);
    }

    private CommentDto toDto(MoodComment mc) {
        LocalDateTime ldt = LocalDateTime.ofInstant(
                mc.getMoodCommentCreatedAt(),
                ZoneId.systemDefault()
        );

        return CommentDto.builder()
                .commentId(mc.getMoodCommentId())
                .commentText(mc.getMoodCommentText())
                .commentCreatedAt(ldt)
                .userId(mc.getCommentUser().getUserId())
                .userNickname(mc.getCommentUser().getUserNickname())
                .contextType("MOOD")
                .contextId(mc.getCommentMood().getMoodId())
                .contextTitle(mc.getCommentMood().getMoodName())
                .build();
    }
}

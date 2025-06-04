package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.MoodCategoryDto;
import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MoodCategoryRepository;
import com.example.moviesbymood.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoodCategoryServiceImpl implements MoodCategoryService {

    private final MoodCategoryRepository repo;
    private final UserRepository         userRepo;

    @Override
    public MoodCategory create(MoodCategory mood) {
        return repo.save(mood);
    }

    @Override
    public MoodCategory update(Long id, MoodCategory mood) {
        MoodCategory existing = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Настроение не найдено: " + id));
        existing.setMoodName(mood.getMoodName());
        existing.setMoodDescription(mood.getMoodDescription());
        return repo.save(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repo.deleteMovieMoodsByMoodId(id);
        repo.deleteMoodRatingsByMoodId(id);
        repo.deleteFavoriteMoodsByMoodId(id);
        repo.deleteById(id);
    }

    @Override
    public MoodCategory findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Настроение не найдено: " + id));
    }

    @Override
    public MoodCategory findEntity(Long id) {
        return findById(id);
    }

    @Override
    public MoodCategory saveEntity(MoodCategory entity) {
        return repo.save(entity);
    }

    @Override
    public MoodCategory createEntity(MoodCategoryDto dto) {
        MoodCategory m = new MoodCategory();
        m.setMoodName(dto.getMoodName());
        m.setMoodDescription(dto.getMoodDescription());
        return repo.save(m);
    }

    @Override
    public List<MoodCategoryDto> findAllDto() {
        return repo.findAll().stream()
                .map(MoodCategoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<MoodCategoryDto> findAll() {
        return findAllDto();
    }

    @Override
    public MoodCategoryDto create(MoodCategoryDto dto) {
        MoodCategory m = new MoodCategory();
        m.setMoodName(dto.getMoodName());
        m.setMoodDescription(dto.getMoodDescription());
        MoodCategory saved = repo.save(m);
        return MoodCategoryDto.fromEntity(saved);
    }

    @Override
    public Optional<MoodCategoryDto> update(Long id, MoodCategoryDto dto) {
        return repo.findById(id).map(existing -> {
            existing.setMoodName(dto.getMoodName());
            existing.setMoodDescription(dto.getMoodDescription());
            MoodCategory saved = repo.save(existing);
            return MoodCategoryDto.fromEntity(saved);
        });
    }

    @Override
    public boolean delete(Long id) {
        if (!repo.existsById(id)) {
            return false;
        }
        MoodCategory toDelete = repo.findById(id).orElseThrow();
        toDelete.getMoodMovies().forEach(movie -> movie.getMovieMoods().remove(toDelete));
        repo.deleteById(id);
        return true;
    }

    @Override
    public MoodCategoryDto createDto(MoodCategoryDto dto) {
        MoodCategory m = new MoodCategory();
        m.setMoodName(dto.getMoodName());
        m.setMoodDescription(dto.getMoodDescription());
        MoodCategory saved = repo.save(m);
        return MoodCategoryDto.fromEntity(saved);
    }

    @Override
    public MoodCategoryDto updateDto(Long id, MoodCategoryDto dto) {
        MoodCategory existing = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Настроение не найдено: " + id));
        existing.setMoodName(dto.getMoodName());
        existing.setMoodDescription(dto.getMoodDescription());
        MoodCategory saved = repo.save(existing);
        return MoodCategoryDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public void toggle(Long id, User ignoredUser, MoodCategory ignoredMood) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + email));
        MoodCategory mood = findEntity(id);

        if (user.getFavoriteMoods().contains(mood)) {
            user.getFavoriteMoods().remove(mood);
        } else {
            user.getFavoriteMoods().add(mood);
        }
        userRepo.save(user);
    }

    @Override
    public boolean isFavorite(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUserEmail(email)
                .map(u -> u.getFavoriteMoods().stream()
                        .anyMatch(m -> m.getMoodId().equals(id)))
                .orElse(false);
    }

    @Override
    public Object getFavoriteMoodIds() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUserEmail(email)
                .map(u -> u.getFavoriteMoods().stream()
                        .map(MoodCategory::getMoodId)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoodCategoryDto> searchByName(String namePart) {
        return repo
                .findByMoodNameContainingIgnoreCase(namePart)
                .stream()
                .map(MoodCategoryDto::fromEntity)
                .collect(Collectors.toList());
    }
}

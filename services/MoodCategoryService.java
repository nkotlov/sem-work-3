package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.MoodCategoryDto;
import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.models.User;

import java.util.List;
import java.util.Optional;

public interface MoodCategoryService {
    List<MoodCategoryDto> findAll();
    MoodCategory findById(Long id);
    MoodCategoryDto create(MoodCategoryDto dto);
    Optional<MoodCategoryDto> update(Long id, MoodCategoryDto dto);
    boolean delete(Long id);
    MoodCategory createEntity(MoodCategoryDto dto);
    MoodCategory saveEntity(MoodCategory entity);
    MoodCategory findEntity(Long id);
    List<MoodCategoryDto> findAllDto();
    void toggle(Long id, User user, MoodCategory mood);
    boolean isFavorite(Long id);
    Object getFavoriteMoodIds();
    MoodCategory create(MoodCategory mood);
    MoodCategory update(Long id, MoodCategory mood);
    void deleteById(Long id);
    MoodCategoryDto createDto(MoodCategoryDto dto);
    MoodCategoryDto updateDto(Long id, MoodCategoryDto dto);
    List<MoodCategoryDto> searchByName(String namePart);
}
